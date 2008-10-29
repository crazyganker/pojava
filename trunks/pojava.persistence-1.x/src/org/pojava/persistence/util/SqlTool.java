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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;

import org.pojava.datetime.DateTime;
import org.pojava.exception.InitializationException;
import org.pojava.lang.Binding;
import org.pojava.lang.BoundString;
import org.pojava.persistence.processor.ResultSetProcessor;
import org.pojava.persistence.query.PreparedSql;
import org.pojava.persistence.sql.TableMap;

/**
 * Utilities for performing SQL tasks. POJava keeps things simple by using
 * prepared statements. This reduces your application's footprint on the cache
 * your database uses to store unique SQL statements. It bypasses issues arising
 * from variations in supported date formats between different databases.
 * 
 * @author John Pile
 * 
 */
public class SqlTool {

	/**
	 * Execute an insert/update/delete query returning row count.
	 * 
	 * @param query
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static int executeUpdate(PreparedSql query, Connection conn)
			throws SQLException {
		PreparedStatement pstmt = generatePreparedStatement(query, conn);
		int result = pstmt.executeUpdate();
		pstmt.close();
		return result;
	}

	/**
	 * Execute a select query, processing its ResultSet.
	 * 
	 * @param query
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static int executeQuery(PreparedSql query, Connection conn,
			ResultSetProcessor processor) throws SQLException {
		PreparedStatement pstmt = generatePreparedStatement(query, conn);
		ResultSet rs = pstmt.executeQuery();
		int result = processor.process(rs);
		if (rs != null) {
			rs.close();
		}
		if (pstmt != null) {
			pstmt.close();
		}
		return result;

	}

	/**
	 * Generate a prepared statement using bindings.
	 * 
	 * @param preparedSql
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private static PreparedStatement generatePreparedStatement(
			PreparedSql preparedSql, Connection conn) throws SQLException {
		BoundString bs = preparedSql.getSql();
		PreparedStatement pstmt = conn.prepareStatement(bs.getString());
		int i = 0;
		for (Iterator it = bs.getBindings().iterator(); it.hasNext();) {
			i++;
			Binding binding = (Binding) it.next();
			int sqlType = sqlTypeFromClass(binding.getType());
			Object o = binding.getObj();
			if (o == null) {
				pstmt.setNull(i, sqlType);
			} else {
				pstmt.setObject(i, o, sqlType);
			}
		}
		pstmt.setMaxRows(preparedSql.getMaxRows());
		return pstmt;
	}

	/**
	 * Multi-purpose class to SQL type translation
	 * 
	 * @param c
	 *            Class of java object to persist
	 * @return
	 */
	private static int sqlTypeFromClass(Class c) {
		int type = 0;
		if (c.equals(Integer.class) || c.equals(int.class)) {
			type = Types.INTEGER;
		} else if (c.equals(Long.class) || c.equals(long.class)) {
			type = Types.BIGINT;
		} else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
			type = Types.BIT;
		} else if (c.equals(Character.class) || c.equals(char.class)) {
			type = Types.CHAR;
		} else if (c.equals(java.sql.Date.class)) {
			type = Types.DATE;
		} else if (c.equals(DateTime.class) || c.equals(java.util.Date.class)
				|| c.equals(Timestamp.class)) {
			type = Types.TIMESTAMP;
		} else if (c.equals(Double.class) || c.equals(double.class)
				|| c.equals(Float.class) || c.equals(float.class)
				|| c.equals(BigDecimal.class)) {
			type = Types.NUMERIC;
		} else if (c.equals(Time.class)) {
			type = Types.TIME;
		} else if (c.equals(Byte.class) || c.equals(byte.class)) {
			type = Types.TINYINT;
		} else {
			type = Types.VARCHAR;
		}
		return type;
	}

	/**
	 * Blow up if we can't initialize properly.
	 * 
	 * @param ex
	 * @param javaClass
	 * @param tableName
	 * @param dsName
	 * @return InitializationException
	 */
	private static InitializationException initializationException(
			SQLException ex, Class javaClass, String tableName, String dsName) {
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

	/**
	 * Generate TableMap from database.
	 * 
	 * @param javaClass
	 * @param tableName
	 * @param dsName
	 * @return TableMap retrieved from database MetaData
	 */
	public static TableMap autoGenerateTableMap(Class javaClass,
			String tableName, String dsName) {
		try {
			TableMap tableMap = new TableMap(javaClass, tableName, dsName);
			tableMap.autoBind();
			return tableMap;
		} catch (SQLException ex) {
			throw initializationException(ex, javaClass, tableName, dsName);
		}
	}

}
