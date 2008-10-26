package org.pojava.persistence.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface describes expected functionality from a Transaction object
 * specific to JDBC.
 * 
 * @author John Pile
 */
public interface DatabaseTransaction {

	/**
	 * Commit all managed connections
	 */
	void commit();

	/**
	 * Roll back all managed connections.
	 */
	void rollback();

	/**
	 * Same as rollback.
	 */
	void clear();

	/**
	 * Retrieve connection (will return an existing connection for the same
	 * dataSourceName if one is available).
	 * 
	 * @param dataSourceName
	 * @return Connection
	 * @throws SQLException
	 */
	Connection getConnection(String dataSourceName) throws SQLException;

	/**
	 * Close connection (usually does nothing).  We keep the connection open
	 * for reuse until the Transaction is either committed or rolled back.
	 * 
	 * @param conn
	 *            Connection to "close"
	 * @throws SQLException
	 */
	void close(Connection conn) throws SQLException;

	/**
	 * Close ResultSet
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	void close(ResultSet rs) throws SQLException;

	/**
	 * Close both the Connection and ResultSet.
	 * 
	 * @param conn
	 * @param rs
	 * @throws SQLException
	 */
	void close(Connection conn, ResultSet rs) throws SQLException;

}
