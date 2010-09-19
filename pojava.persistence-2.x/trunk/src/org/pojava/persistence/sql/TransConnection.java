package org.pojava.persistence.sql;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * This connection object provides a layer allowing a Transaction visibility to intervene (or
 * interfere :) when a user of the connection performs transaction actions outside of those
 * managed by the Transaction object itself.
 * 
 * It mostly exists to allow a client to "close" a managed connection, and still have the
 * connection participate in a transaction rollback.
 * 
 * @author John Pile
 * 
 */
public class TransConnection implements Connection {

    /**
     * This allows a managed connection to be "closed", but still participate in a Transaction
     * rollback.
     */
    private boolean closeAllowed = false;

    /**
     * This holds the inner connection that this connection represents.
     */
    private Connection conn = null;

    /**
     * Create a new TransConnection from this connection.
     * 
     * @param conn
     */
    public TransConnection(Connection conn) {
        super();
        this.conn = conn;
    }

    /**
     * Clear connection warnings.
     */
    public void clearWarnings() throws SQLException {
        conn.clearWarnings();
    }

    /**
     * Close connection (ignored if closeAllowed is false).
     */
    public void close() throws SQLException {
        if (closeAllowed) {
            conn.close();
        }
    }

    /**
     * Commit transaction.
     */
    public void commit() throws SQLException {
        conn.commit();
    }

    /**
     * Create a statement
     */
    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }

    /**
     * Create a statement
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return conn.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * Create a statement
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Return true if autocommit is enabled.
     */
    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }

    /**
     * Retrieve the connection catalog.
     */
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

    /**
     * Get connection's holdability
     */
    public int getHoldability() throws SQLException {
        return conn.getHoldability();
    }

    /**
     * Get useful connection metadata
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

    /**
     * Get transaction isolation of connection.
     */
    public int getTransactionIsolation() throws SQLException {
        return conn.getTransactionIsolation();
    }

    /**
     * Get typeMap for connection.
     */
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return conn.getTypeMap();
    }

    /**
     * Get warnings from connection driver, which might not throw as Exceptions.
     */
    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }

    /**
     * Return true if connection is closed.
     */
    public boolean isClosed() throws SQLException {
        return conn.isClosed();
    }

    /**
     * Return true if connection is read only.
     */
    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }

    /**
     * Return the native equivalent of the given SQL statement.
     */
    public String nativeSQL(String sql) throws SQLException {
        return conn.nativeSQL(sql);
    }

    /**
     * Prepare a callable statement.
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        return conn.prepareCall(sql);
    }

    /**
     * Prepare a callable statement.
     */
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Prepare a callable statement.
     */
    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * Prepare a prepared statement.
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    /**
     * Prepare a prepared statement.
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        return conn.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * Prepare a prepared statement.
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        return conn.prepareStatement(sql, columnIndexes);
    }

    /**
     * Prepare a prepared statement.
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        return conn.prepareStatement(sql, columnNames);
    }

    /**
     * Prepare a prepared statement.
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * Prepare a prepared statement.
     */
    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency,
                resultSetHoldability);
    }

    /**
     * Release savepoint
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        conn.releaseSavepoint(savepoint);
    }

    /**
     * Perform rollback
     */
    public void rollback() throws SQLException {
        conn.rollback();
    }

    /**
     * Rollback to savepoint
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        conn.rollback(savepoint);
    }

    /**
     * Set AutoCommit
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }

    /**
     * Set catalog
     */
    public void setCatalog(String catalog) throws SQLException {
        conn.setCatalog(catalog);
    }

    /**
     * Set the connection's holdability
     */
    public void setHoldability(int holdability) throws SQLException {
        conn.setHoldability(holdability);
    }

    /**
     * Set read only attribute for the connection.
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        conn.setReadOnly(readOnly);
    }

    /**
     * Set a transaction savepoint for this connection.
     */
    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }

    /**
     * Set a transaction savepoint for this connection.
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        return conn.setSavepoint(name);
    }

    /**
     * Set transaction Isolation defined in Connection object.
     */
    public void setTransactionIsolation(int level) throws SQLException {
        conn.setTransactionIsolation(level);
    }

    /**
     * Set typeMap to new Map.
     */
    public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
        conn.setTypeMap(arg0);
    }

    /**
     * Compare two connections for equality.
     */
    public boolean equals(Object obj) {
        return conn.equals(obj) || super.equals(obj);
    }

    /**
     * HashCode of the connection.
     */
    public int hashCode() {
        return conn.hashCode();
    }

    /**
     * Worthless conn.toString() output.
     */
    public String toString() {
        return conn.toString();
    }

    /**
     * True if close will actually close the connection.
     * 
     * @return true if close will actually close the underlying Connection
     */
    public boolean isCloseAllowed() {
        return closeAllowed;
    }

    /**
     * Set whether the underlying Connection is immediately closed by its close method.
     * @param closeAllowed
     */
    public void setCloseAllowed(boolean closeAllowed) {
        this.closeAllowed = closeAllowed;
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public void setClientInfo(Properties properties) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public String getClientInfo(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public Array createArrayOf(String typeName, Object[] elements) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public boolean isValid(int timeout) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
    public Struct createStruct(String str, Object[] objArray) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported. Included to compile under Java 1.6.
     */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/*
	public NClob createNClob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public SQLXML createSQLXML() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		throw new UnsupportedOperationException();
	}
	*/

}
