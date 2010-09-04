package org.pojava.persistence.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 * This connection object provides a layer allowing a Transaction visibility to
 * intervene (interfere?) when a user of the connection performs transaction
 * actions outside of those managed by the Transaction object itself.
 * 
 * @author John Pile
 * 
 */
public class TransConnection implements Connection {

	private boolean closeAllowed = false;
	private Connection conn = null;

	public TransConnection(Connection conn) {
		super();
		this.conn = conn;
	}

	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}

	public void close() throws SQLException {
		if (closeAllowed) {
			conn.close();
		}
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return conn.createStatement(resultSetType, resultSetConcurrency);
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public boolean getAutoCommit() throws SQLException {
		return conn.getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		return conn.getCatalog();
	}

	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	public int getTransactionIsolation() throws SQLException {
		return conn.getTransactionIsolation();
	}

	public Map getTypeMap() throws SQLException {
		return conn.getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		return conn.getWarnings();
	}

	public boolean isClosed() throws SQLException {
		return conn.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}

	public String nativeSQL(String sql) throws SQLException {
		return conn.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return conn.prepareCall(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return conn.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		return conn.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		return conn.prepareStatement(sql, columnNames);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		conn.releaseSavepoint(savepoint);
	}

	public void rollback() throws SQLException {
		conn.rollback();
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		conn.rollback(savepoint);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		conn.setAutoCommit(autoCommit);
	}

	public void setCatalog(String catalog) throws SQLException {
		conn.setCatalog(catalog);
	}

	public void setHoldability(int holdability) throws SQLException {
		conn.setHoldability(holdability);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		conn.setReadOnly(readOnly);
	}

	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return conn.setSavepoint(name);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		conn.setTransactionIsolation(level);
	}

	public void setTypeMap(Map arg0) throws SQLException {
		conn.setTypeMap(arg0);
	}

	public boolean equals(Object obj) {
		return conn.equals(obj) || super.equals(obj);
	}

	public int hashCode() {
		return conn.hashCode();
	}

	public String toString() {
		return conn.toString();
	}

	public boolean isCloseAllowed() {
		return closeAllowed;
	}

	public void setCloseAllowed(boolean closeAllowed) {
		this.closeAllowed = closeAllowed;
	}

}
