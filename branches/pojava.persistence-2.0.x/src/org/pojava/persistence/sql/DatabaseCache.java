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

import org.pojava.persistence.util.SqlTool;

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
	private static Map<String, DataSourceMetadata> dataSourceMetadataCache = new HashMap<String, DataSourceMetadata>();

	/**
	 * Holds DataSource objects by DataSource name.
	 */
	private static Map<String,DataSource> dataSourceCache = new HashMap<String,DataSource>();

	/**
	 * Holds TableMap objects by Java class + table name.
	 */
	private static Map<String,TableMap<?>> tableMapCache = new HashMap<String,TableMap<?>>();

	/**
	 * Holds the names of DataSource objects used for locks.
	 */
	private static Map<String,Object> dataSourceLocks = new HashMap<String,Object>();

	/**
	 * Return the metadata for the named DataSource
	 * 
	 * @param dsName
	 *            Name of DataSource
	 * @return Metadata gathered from a connection from this dataSource
	 */
	public static DataSourceMetadata getDataSourceMetadata(String dsName)
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
	 * @param dsName
	 * @return DataSource retrieved from cache or JNDI registry.
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
	 * @return String combining name, table, dataSource for use as a key
	 */
	private static String tableMapKey(Class<?> javaClass, String tableName,
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
	public static void registerTableMap(TableMap<? extends Object> tableMap) {
		tableMapCache.put(tableMapKey(tableMap.getClass(), tableMap
				.getTableName(), tableMap.getDataSourceName()), tableMap);
	}

	/**
	 * Register a DataSource.
	 * 
	 * @param dataSourceName
	 * @param dataSource
	 */
	public static void registerDataSource(String dataSourceName,
			DataSource dataSource) {
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
	@SuppressWarnings("unchecked")
	public static <T> TableMap<T> getTableMap(Class<T> javaClass, String tableName,
			String dataSourceName) {
		String key = tableMapKey(javaClass, tableName, dataSourceName);
		// Use cached version if it exists
		if (tableMapCache.containsKey(key)) {
			return (TableMap<T>) tableMapCache.get(key);
		}
		Object lock;
		// Acquire a lock unique to the key
		synchronized (dataSourceLocks) {
			lock = dataSourceLocks.get(key);
			if (lock == null) {
				lock = new Object();
				dataSourceLocks.put(key, lock);
			}
		}
		// The lock allows one thread per unique key
		TableMap<T> tableMap;
		synchronized (lock) {
			tableMap = (TableMap<T>) tableMapCache.get(key);
			if (tableMap == null) {
				tableMap = (TableMap<T>) SqlTool.autoGenerateTableMap(javaClass, tableName,
						dataSourceName);
				tableMapCache.put(key, tableMap);
			}
		}
		return tableMap;
	}

}
