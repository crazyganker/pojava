package org.pojava.persistence.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;


/**
 * A DistributedTransaction object coordinates the transactions of
 * multiple connections so that all of them commit or roll back as
 * a collective.
 * 
 * @author John Pile
 *
 */
public class DistributedTransaction implements DatabaseTransaction {

	/**
	 * Connections mapped by DataSource name.
	 */
	Map connections=new HashMap();
	
	Logger log=Logger.getLogger("org.pojava.database");
	
	/**
	 * Obtain a connection, reusing an existing transaction if possible.
	 * This is done to reduce the risk of deadlock between two connections
	 * within the same transaction.
	 */
	public Connection getConnection(String dataSourceName) throws SQLException {
		Connection conn;
		DataSource ds=DatabaseCache.getDataSource(dataSourceName);
		if (ds==null) {
			throw new IllegalStateException("DataSource " + dataSourceName + " not found.");
		}
		if (connections.containsKey(dataSourceName)) {
			conn=(Connection) connections.get(dataSourceName);
		} else {
			conn=new TransConnection(ds.getConnection());
			connections.put(dataSourceName, conn);
			setDefaults(conn);
		}
		return conn;
	}
	
	/**
	 * Defines the applied defaults for all new connections.
	 * @param conn
	 * @throws SQLException
	 */
    private void setDefaults(Connection conn) throws SQLException {
    	conn.setAutoCommit(false);
    }
	
    /**
     * Ensure that Transaction has no unfinished business
     */
	public void clear() {
		rollback();		
	}
	
	/**
	 * Rollback all transactions on all managed connections.
	 */
	public void rollback() {
		for (Iterator it=connections.values().iterator(); it.hasNext();) {
			Connection conn=(Connection)it.next();
			try {
				conn.rollback();
			} catch (SQLException ex) {
				log.severe("Partial rollback failure: " + ex.getMessage());
			}
		}
		closeConnections();
	}
	
	/**
	 * Commit all transactions on all managed connections. 
	 */
	public void commit() {
		for (Iterator it=connections.values().iterator(); it.hasNext();) {
			Connection conn=(Connection)it.next();
			try {
				conn.commit();
			} catch (SQLException ex) {
				log.severe("Partial commit failure: " + ex.getMessage());
			}
		}		
		closeConnections();
	}
	
	/**
	 * This actually does close the connections (or returns them to a pool).
	 */
	private void closeConnections() {
		for (Iterator it=connections.values().iterator(); it.hasNext();) {
			TransConnection conn=(TransConnection)it.next();
			try {
				conn.setCloseAllowed(true);
				conn.close();
			} catch (SQLException ex) {
				log.severe("Connection close failure: " + ex.getMessage());
			}
		}
		connections.clear();
	}
	
	/**
	 * Close any connection, but if the connection is participating in a
	 * Transaction, then it's not really being closed (or returned to the pool)
	 * just yet.
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public void close(Connection conn) throws SQLException {
		if (conn!=null) {
			conn.close();
		}
	}
	
	/**
	 * Close a ResultSet.
	 */
	public void close(ResultSet rs) throws SQLException {
		if (rs!=null) {
			rs.close();
		}
	}
	
	/**
	 * Close both the Connection and ResultSet.
	 */
	public void close(Connection conn, ResultSet rs) throws SQLException {
		close(conn);
		close(rs);
	}
	
}
