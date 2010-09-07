package org.pojava.persistence.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.pojava.exception.PersistenceException;

/**
 * A DatabaseTransaction object coordinates the transactions of multiple connections so that all
 * of them commit or roll back as a collective.
 * 
 * @author John Pile
 * 
 */
public class DatabaseTransaction implements Transaction, ConnectionSource {

    /**
     * Connections mapped by DataSource name.
     */
    Map<String, Connection> connections = new HashMap<String, Connection>();

    Logger log = Logger.getLogger("org.pojava.database");

    /**
     * Obtain a connection, reusing an existing transaction if possible. This is done to reduce
     * the risk of deadlock between two connections within the same transaction.
     */
    public Connection getConnection(String dataSourceName) throws PersistenceException {
        Connection conn;
        DataSource ds = DatabaseCache.getDataSource(dataSourceName);
        if (ds == null) {
            throw new IllegalStateException("DataSource " + dataSourceName + " not found.");
        }
        if (connections.containsKey(dataSourceName)) {
            conn = (Connection) connections.get(dataSourceName);
        } else {
            try {
                conn = new TransConnection(ds.getConnection());
                connections.put(dataSourceName, conn);
                setDefaults(conn);
            } catch (SQLException ex) {
                throw new PersistenceException(ex.getMessage(), ex);
            }
        }
        return conn;
    }

    /**
     * Defines the applied defaults for all new connections.
     * 
     * @param conn
     * @throws SQLException
     */
    private void setDefaults(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    /**
     * Rollback all transactions on all managed connections.
     */
    public void rollback() {
        for (Iterator<Connection> it = connections.values().iterator(); it.hasNext();) {
            Connection conn = it.next();
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
        for (Iterator<Connection> it = connections.values().iterator(); it.hasNext();) {
            Connection conn = it.next();
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
        for (Iterator<Connection> it = connections.values().iterator(); it.hasNext();) {
            TransConnection conn = (TransConnection) it.next();
            try {
                conn.setCloseAllowed(true);
                conn.close();
            } catch (SQLException ex) {
                log.severe("Connection close failure: " + ex.getMessage());
            }
        }
        connections.clear();
    }

}
