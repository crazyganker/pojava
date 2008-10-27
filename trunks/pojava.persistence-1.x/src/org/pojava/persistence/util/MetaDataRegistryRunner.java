package org.pojava.persistence.util;

/*
 Copyright 2008 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.sql.SQLException;

import javax.sql.DataSource;

import org.pojava.exception.PersistenceException;
import org.pojava.persistence.sql.DatabaseCache;

/**
 * Perform MetaData gathering using a background thread. The MetaData is used
 * to cache mappings between tables and POJOs.
 * 
 * @author John Pile
 * 
 */
public class MetaDataRegistryRunner implements Runnable {

	private String name;

	public MetaDataRegistryRunner(String name) {
		this.name = name;
	}

	/**
	 * Register MetaData in the DatabaseCache.
	 */
	public void run() {
		DataSource ds = DatabaseCache.getDataSource(name);
		try {
			DatabaseCache.registerMetaData(name, ds.getConnection()
					.getMetaData());
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Spawn a background thread to do the job.
	 * 
	 * @param name
	 */
	public static void register(String name) {
		Thread runner=new Thread(new MetaDataRegistryRunner(name));
		runner.setPriority(java.lang.Thread.MIN_PRIORITY);
		runner.start();
	}
}
