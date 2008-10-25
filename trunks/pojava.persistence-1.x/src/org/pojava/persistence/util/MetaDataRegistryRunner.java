package org.pojava.persistence.util;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.pojava.exception.PersistenceException;
import org.pojava.persistence.sql.DatabaseCache;



public class MetaDataRegistryRunner implements Runnable {
	
	private String name;
	
	public MetaDataRegistryRunner(String name) {
		this.name=name;
	}
	
	public void run() {
		DataSource ds=DatabaseCache.getDataSource(name);
		try {
			DatabaseCache.registerMetaData(name, ds.getConnection().getMetaData());
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	public static void register(String name) {
		(new Thread(new MetaDataRegistryRunner(name))).start();
	}
}
