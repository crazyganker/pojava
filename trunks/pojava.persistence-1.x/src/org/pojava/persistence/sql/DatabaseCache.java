package org.pojava.persistence.sql;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This singleton caches object properties that facilitate interchange between
 * Java and external data stores.
 * 
 * @author John Pile
 */
public class DatabaseCache {

	/**
	 * Holds DataSourceMetadata objects by DataSource name.
	 */
	private static Map dataSourceMetadataCache = new HashMap();

	/**
	 * Holds DataSource objects by DataSource name.
	 */
	private static Map dataSourceCache = new HashMap();

	/**
	 * Holds TableMap objects by Java class + table name.
	 */
	private static Map tableMapCache = new HashMap();

	/**
	 * Holds the names of DataSource objects used for locks.
	 */
	private static Map dataSourceLocks = new HashMap();

	/**
	 * Return the metadata for the named DataSource
	 * 
	 * @param dsName
	 *            Name of DataSource
	 * @return
	 */
	public static DataSourceMetadata getDataSourceMetaData(String dsName)
			throws SQLException {
		// Try the fastest option first
		if (dataSourceMetadataCache.containsKey(dsName)) {
			return (DataSourceMetadata) dataSourceMetadataCache.get(dsName);
		}
		DataSourceMetadata metadata;
		Object lock;
		// Acquire a lock unique to the dsName
		synchronized (dataSourceLocks) {
			lock = dataSourceLocks.get(dsName);
			if (lock == null) {
				lock = new Object();
				dataSourceLocks.put(dsName, lock);
			}
		}
		// The lock allows one thread per DataSource name
		synchronized (lock) {
			metadata = (DataSourceMetadata) dataSourceMetadataCache.get(dsName);
			if (metadata == null) {
				DataSource dataSource = getDataSource(dsName);
				Connection conn = dataSource.getConnection();
				metadata = new DataSourceMetadata(conn);
				dataSourceMetadataCache.put(dsName, metadata);
			}
		}
		return metadata;
	}

	/**
	 * Fetch a DataSource from the registry.
	 * 
	 * @param name
	 * @return
	 */
	public static DataSource getDataSource(String dsName) {
		// Try the fastest option first
		if (dataSourceCache.containsKey(dsName)) {
			return (DataSource) dataSourceCache.get(dsName);
		}
		// Retrieve from JNDI Registry
		Context ctx;
		DataSource ds;
		try {
			ctx = new InitialContext();
		} catch (NamingException ex) {
			throw new IllegalStateException(
					ex.getMessage()
							+ "  See org.pojava.testing.JNDIRegistry if you need to create an InitialContext for unit testing.");
		}
		try {
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/" + dsName.trim());
		} catch (NamingException ex) {
			throw new InvalidParameterException(dsName.trim()
					+ " triggered NamingException " + ex.getMessage());
		}
		if (ds == null) {
			throw new IllegalStateException(
					"Could not find java:comp/env/jdbc/" + dsName.trim()
							+ " in registry.");
		} else {
			dataSourceCache.put(dsName, ds);
		}
		return ds;
	}

	/**
	 * Build a key combining a java class with a table name.
	 * 
	 * @param javaClass
	 * @param tableName
	 * @param dataSourceName
	 * @return
	 */
	private static String tableMapKey(Class javaClass, String tableName,
			String dataSourceName) {
		StringBuffer key = new StringBuffer();
		key.append(javaClass.getName());
		key.append(":");
		key.append(tableName);
		key.append(":");
		key.append(dataSourceName);
		return key.toString();
	}

	/**
	 * Register a tableMap
	 * 
	 * @param tableMap
	 */
	public static void registerTableMap(TableMap tableMap) {
		tableMapCache.put(tableMapKey(tableMap.getClass(), tableMap
				.getTableName(), tableMap.getDataSourceName()), tableMap);
	}
	
	/**
	 * Register a DataSource.
	 * @param dataSourceName
	 * @param dataSource
	 */
	public static void registerDataSource(String dataSourceName, DataSource dataSource) {
		dataSourceCache.put(dataSourceName, dataSource);
	}

	/**
	 * Retrieve a tableMap
	 * 
	 * @param javaClass
	 * @param tableName
	 * @param dataSourceName
	 * @return TableMap or null if not found.
	 */
	public static TableMap getTableMap(Class javaClass, String tableName,
			String dataSourceName) {
		return (TableMap) tableMapCache.get(tableMapKey(javaClass, tableName,
				dataSourceName));
	}

}
