package org.pojava.persistence.sql;

import java.sql.Connection;

public interface ConnectionSource {

	/**
	 * Retrieve connection (will return an existing connection for the same
	 * dataSourceName if one is available).
	 * 
	 * @param dataSourceName
	 * @return Connection
	 */
	Connection getConnection(String dataSourceName);

}
