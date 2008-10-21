package org.pojava.persistence.sql;

import java.security.InvalidParameterException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.pojava.persistence.util.MetaDataRegistryRunner;

/**
 * This singleton caches object properties that facilitate interchange
 * between Java and external data stores.
 * 
 * @author John Pile
 */
public class DatabaseCache {

	/**
	 * Holds DataSourceMetadata objects by DataSource name.
	 */
	private static Map dataSourceMetaDataCache = new HashMap();

	/**
	 * Holds DataSource objects by DataSource name.
	 */
	private static Map dataSourceCache = new HashMap();
	
	/**
	 * Holds TableMap objects by Java class + table name.
	 */
	private static Map tableMapCache = new HashMap();

	/**
	 * Return 
	 * @param name
	 * @return
	 */
	public static DataSourceMetadata metaData(String name) {
		return (DataSourceMetadata) dataSourceMetaDataCache.get(name);
	}

	/**
	 * Fetch a DataSource from the registry.
	 * 
	 * @param name
	 * @return
	 */
	public static DataSource getDataSource(String name) {
		Context ctx;
		DataSource ds;
		try {
			ctx = new InitialContext();
		} catch (NamingException ex) {
			throw new IllegalStateException(ex.getMessage()
					+ "See org.pojava.testing.");
		}
		try {
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/" + name.trim());
		} catch (NamingException ex) {
			throw new InvalidParameterException(name.trim()
					+ " triggered NamingException " + ex.getMessage());
		}
		if (ds == null) {
			throw new IllegalStateException(
					"Could not find java:comp/env/jdbc/" + name.trim()
							+ " in registry.");
		}
		try {
			if (dataSourceMetaDataCache.get(name) == null) {
				registerMetaData(name);
			}
		} catch (SQLException ex) {
			// Since the caller didn't really ask for this, we'll mute it.
		}
		return ds;
	}

	/**
	 * Spawn a background thread to capture some info about the database
	 * 
	 * @param name
	 * @throws SQLException
	 */
	public static void registerMetaData(String name) throws SQLException {
		// This task runs in the background, filling MetaData values.
		MetaDataRegistryRunner.register(name);
	}

	/**
	 * This is protected to encourage the use of registerMetaData(String name)
	 * 
	 * @param name
	 * @param dbmeta
	 * @throws SQLException
	 */
	public static void registerMetaData(String name, DatabaseMetaData dbmeta)
			throws SQLException {
		dataSourceMetaDataCache.put(name, dbmeta);
	}

	/**
	 * 
	 * @param name Simple short name for DataSource
	 * @param ds DataSource
	 */
	public static void registerDataSource(String name, DataSource ds) {
		Context ctx;
		try {
			ctx = new InitialContext();
		} catch (NamingException ex) {
			throw new IllegalStateException(ex.getMessage()
					+ "See org.pojava.testing for an InitialContext suitable for testing.");
		}
		try {
			ctx.rebind("java:comp/env/jdbc/" + name.trim(), ds);
		} catch (NamingException ex) {
			throw new InvalidParameterException(name.trim()
					+ " triggered NamingException " + ex.getMessage());
		}
		dataSourceCache.put(name, ds);
	}

	/**
	 * If the metadata is not yet registered, then you have to wait for it. This
	 * sidesteps a potential race condition between this request and the
	 * registerMetaData(name) background process.
	 * 
	 * @param name
	 * @return
	 */
	public static DataSourceMetadata getDataSourceMetadata(String name) {
		DataSourceMetadata meta = (DataSourceMetadata) dataSourceMetaDataCache
				.get(name);
		if (meta == null) {
			DataSource ds = getDataSource(name);
			try {
				registerMetaData(name, ds.getConnection().getMetaData());
			} catch (SQLException ex) {
				throw new IllegalStateException(
						"Attempt to registerMetaData failed: "
								+ ex.getMessage(), ex);
			}
			meta = (DataSourceMetadata) dataSourceMetaDataCache.get(name);
		}
		return meta;
	}

	/**
	 * Build a key combining a java class with a table name.
	 * @param javaClass
	 * @param tableName
	 * @param dataSourceName
	 * @return
	 */
	private static String tableMapKey(Class javaClass, String tableName, String dataSourceName) {
		StringBuffer key=new StringBuffer();
		key.append(javaClass.getName());
		key.append(":");
		key.append(tableName);
		key.append(":");
		key.append(dataSourceName);		
		return key.toString();
	}
	
	/**
	 * Register a tableMap
	 * @param tableMap
	 */
	public static void registerTableMap(TableMap tableMap) {
		tableMapCache.put(tableMapKey(tableMap.getClass(), tableMap.getTableName(), tableMap.getDataSourceName()), tableMap);
	}
	
	/**
	 * Retrieve a tableMap
	 * @param javaClass
	 * @param tableName
	 * @param dataSourceName
	 * @return TableMap or null if not found.
	 */
	public static TableMap getTableMap(Class javaClass, String tableName, String dataSourceName) {
		return (TableMap) tableMapCache.get(tableMapKey(javaClass, tableName, dataSourceName));
	}


}
