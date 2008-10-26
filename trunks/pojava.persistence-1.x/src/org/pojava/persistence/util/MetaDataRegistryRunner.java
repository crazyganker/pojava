package org.pojava.persistence.util;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.pojava.exception.PersistenceException;
import org.pojava.persistence.sql.DatabaseCache;

/**
 * Perform MetaData gathering uusing a background thread.  The MetaData
 * is used to cache mappings between tables and POJOs.
 *  
 * @author John Pile
 *
 */
public class MetaDataRegistryRunner implements Runnable {
	
	private String name;
	
	public MetaDataRegistryRunner(String name) {
		this.name=name;
	}
	
	/**
	 * Register MetaData in the DatabaseCache.
	 */
	public void run() {
		DataSource ds=DatabaseCache.getDataSource(name);
		try {
			DatabaseCache.registerMetaData(name, ds.getConnection().getMetaData());
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Spawn a background thread to do the job.
	 * @param name
	 */
	public static void register(String name) {
		(new Thread(new MetaDataRegistryRunner(name))).start();
	}
}
