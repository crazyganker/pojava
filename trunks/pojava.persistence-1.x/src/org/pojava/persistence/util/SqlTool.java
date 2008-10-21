package org.pojava.persistence.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;

import org.pojava.exception.InitializationException;
import org.pojava.lang.Binding;
import org.pojava.lang.BoundString;
import org.pojava.persistence.query.PreparedSql;
import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.TableMap;
import org.projava.persistence.processor.ResultSetProcessor;

public class SqlTool {
	
	/**
	 * Execute an insert/update/delete query returning row count.
	 * @param query
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static int executeUpdate(PreparedSql query, Connection conn) throws SQLException {
		PreparedStatement pstmt=generatePreparedStatement(query.getSql(), conn);
		pstmt.setMaxRows(query.getMaxRows());
		int result=pstmt.executeUpdate();
		pstmt.close();
		return result;
	}
	
	/**
	 * Execute a select query, processing its ResultSet.
	 * @param query
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static int executeQuery(PreparedSql query, Connection conn, ResultSetProcessor processor) throws SQLException {
		PreparedStatement pstmt=generatePreparedStatement(query.getSql(), conn);
		pstmt.setMaxRows(query.getMaxRows());
		ResultSet rs=pstmt.executeQuery(query.getSql().getString());
		int result=processor.process(rs);
		if (rs!=null) {
			rs.close();
		}
		if (pstmt!=null) {
			pstmt.close();
		}
		return result;
		
	}
	
	/**
	 * Generate a prepared statement using bindings.
	 * @param boundSql
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private static PreparedStatement generatePreparedStatement(BoundString boundSql, Connection conn) throws SQLException {
		PreparedStatement pstmt=conn.prepareStatement(boundSql.getString());
		int i=0;
		for (Iterator it=boundSql.getBindings().iterator(); it.hasNext();) {
			i++;
			Binding binding=(Binding)it.next();
			int sqlType=sqlTypeFromClass(binding.getType());
			Object o=binding.getObj();
			if (o==null) {
				pstmt.setNull(i, sqlType);
			} else {
				pstmt.setObject(i, o, sqlType);
			}
		}
		return pstmt;
	}

	/**
	 * Multi-purpose class to SQL type translation
	 * @param c Class of java object to persist
	 * @return
	 */
	private static int sqlTypeFromClass(Class c) {
		int type=0;
		if (c.equals(Integer.class) || c.equals(int.class)) {
			type=Types.INTEGER;
		} else if (c.equals(Long.class) || c.equals(long.class)) {
			type=Types.BIGINT;
		} else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
			type=Types.BIT;
		} else if (c.equals(Character.class) || c.equals(char.class)) {
			type=Types.CHAR;
		} else if (c.equals(java.util.Date.class) || c.equals(Timestamp.class) || c.equals(java.sql.Date.class)) {
			type=Types.TIMESTAMP;
		} else if (c.equals(Double.class) || c.equals(double.class) || c.equals(Float.class) || c.equals(float.class) || c.equals(BigDecimal.class)) {
			type=Types.NUMERIC;
		} else if (c.equals(Time.class)) {
			type=Types.TIME;
		} else if (c.equals(Byte.class) || c.equals(byte.class)) {
			type=Types.TINYINT;
		} else {
			type=Types.VARCHAR;
		}
		return type;
	}
	
	private static InitializationException initializationException(SQLException ex, Class javaClass, String tableName, String dsName) {
		StringBuffer msg = new StringBuffer();
		msg.append("Cannot auto-initialize TableMap(");
		msg.append(javaClass.toString());
		msg.append(",");
		msg.append(tableName);
		msg.append(",");
		msg.append(dsName);
		msg.append("): ");
		msg.append(ex.getMessage());
		return new InitializationException(msg.toString(), ex);
	}
	
	private static TableMap autoGenerateTableMap(Class javaClass, String tableName, String dsName) {
		try {
			TableMap tableMap = new TableMap(javaClass, tableName, dsName);
			tableMap.autoBind();
			return tableMap;
		} catch (SQLException ex) {
			throw initializationException(ex, javaClass, tableName, dsName);
		}
	}

	/**
	 * Fetch a TableMap specific to a javaClass:tableName:dataSourceName combination.
	 * Auto-generate a new map if necessary.
	 * @param javaClass
	 * @param tableName
	 * @param dataSourceName
	 * @return
	 */
	public static TableMap fetchTableMap(Class javaClass, String tableName, String dataSourceName) {
		TableMap tableMap=DatabaseCache.getTableMap(javaClass, tableName, dataSourceName);
		if (tableMap==null) {
			tableMap=autoGenerateTableMap(javaClass, tableName, dataSourceName);
			DatabaseCache.registerTableMap(tableMap);
		}
		return tableMap;
	}

	
}
