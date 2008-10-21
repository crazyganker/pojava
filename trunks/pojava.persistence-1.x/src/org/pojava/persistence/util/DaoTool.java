package org.pojava.persistence.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pojava.lang.Processor;
import org.pojava.persistence.query.PreparedSql;
import org.pojava.persistence.sql.TableMap;
import org.projava.persistence.processor.ResultSetToInt;
import org.projava.persistence.processor.ResultSetToList;
import org.projava.persistence.processor.ResultSetToProcessor;

/**
 * DaoTool provides a consistent assortment of database operations for your DAO
 * objects to provide. It takes much of the grunt-work out of the typical CRUD
 * operations often copy-pasted and altered from one DAO to another.
 * 
 * @author John Pile
 * 
 */
public class DaoTool {

	/**
	 * A default of zero indicates unlimited rows may be returned.
	 */
	private static int DEFAULT_MAXROWS = 0;

	/**
	 * Validate parameters common to various operations.
	 * 
	 * @param map
	 * @param query
	 * @param action
	 */
	private static final void validateParams(TableMap map, PreparedSql query,
			String action) {
		if (query == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform ");
			msg.append(action);
			msg.append(" using a null query.");
			throw new IllegalArgumentException(msg.toString());
		}
		if (map == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform ");
			msg.append(action);
			msg.append(" because TableMap is null.");
			throw new IllegalArgumentException(msg.toString());
		}
	}

	/**
	 * Validate parameters common to various operations.
	 * 
	 * @param map
	 * @param obj
	 * @param action
	 */
	private static final void validateParams(TableMap map, Object obj,
			String action) {
		if (obj == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform ");
			msg.append(action);
			msg.append(" on null Object.");
			throw new IllegalArgumentException(msg.toString());
		}
		if (map == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform ");
			msg.append(action);
			msg.append(" because TableMap is null.");
			throw new IllegalArgumentException(msg.toString());
		}
		if (!obj.getClass().isAssignableFrom(map.getJavaClass())) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform ");
			msg.append(action);
			msg.append(" because TableMap.javaClass=");
			msg.append(map.getJavaClass().getName());
			msg.append(" is not equivalent to Object.class=");
			msg.append(obj.getClass().getName());
			throw new IllegalArgumentException(msg.toString());
		}
	}

	/**
	 * Perform an insert, throwing an exception if the attempted insert fails.
	 * This will throw an SQLException if an existing record matches the primary
	 * key defined in the TableMap.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return number of rows inserted (should always be 1)
	 * @throws SQLException
	 */
	public static final int insert(Connection conn, TableMap map,
			Object obj) throws SQLException {
		validateParams(map, obj, "insert");
		PreparedSql query = new PreparedSql(map.sqlInsert(obj), DEFAULT_MAXROWS);
		return SqlTool.executeUpdate(query, conn);
	}

	/**
	 * Update an existing object in the table according to the primary key, and
	 * insert a new record if one does not already exist.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return number of rows updated or inserted (should always be 1)
	 * @throws SQLException
	 */
	public static final int updateInsert(Connection conn,
			TableMap map, Object obj) throws SQLException {
		validateParams(map, obj, "updateInsert");
		int ct = update(conn, map, obj);
		if (ct == 0) {
			ct = insert(conn, map, obj);
		}
		return ct;
	}

	/**
	 * Insert a new record into a table, but only if that record is not already
	 * present according to its primary key.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return Number of records inserted (should be 1 or 0).
	 * @throws SQLException
	 */
	public static final int passiveInsert(Connection conn,
			TableMap map, Object obj) throws SQLException {
		validateParams(map, obj, "passiveInsert");
		if (null == find(conn, map, obj)) {
			return insert(conn, map, obj);
		}
		return 0;
	}

	/**
	 * Update an existing record in a table. The record is selected by its key
	 * fields and its non-key fields are updated. This does not throw an
	 * exception if the record does not already exist.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return Number of rows updated.
	 * @throws SQLException
	 */
	public static final int update(Connection conn, TableMap map,
			Object obj) throws SQLException {
		validateParams(map, obj, "update");
		PreparedSql query = new PreparedSql(map.sqlUpdate(obj), DEFAULT_MAXROWS);
		return SqlTool.executeUpdate(query, conn);
	}

	/**
	 * Delete an existing record from a table according to its primary key.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return Number of rows deleted.
	 * @throws SQLException
	 */
	public static final int delete(Connection conn, TableMap map,
			Object obj) throws SQLException {
		validateParams(map, obj, "delete");
		PreparedSql query = new PreparedSql(map.sqlDelete(obj), DEFAULT_MAXROWS);
		return SqlTool.executeUpdate(query, conn);
	}

	/**
	 * Retrieve a single record according to its primary key.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return the result of the query packaged into a mapped bean.
	 * @throws SQLException
	 */
	public static final Object find(Connection conn, TableMap map,
			Object obj) throws SQLException {
		validateParams(map, obj, "find");
		PreparedSql query = new PreparedSql(map.sqlSelect(obj), DEFAULT_MAXROWS);
		List list = new ArrayList();
		ResultSetToList processor = new ResultSetToList(map, list);
		int ct = SqlTool.executeQuery(query, conn, processor);
		if (ct == 0) {
			return null;
		}
		if (ct == 1) {
			return list.get(0);
		}
		StringBuffer msg = new StringBuffer();
		msg.append("Data integrity violation.  TableMap for class=");
		msg.append(map.getJavaClass().getName());
		msg.append(", table=");
		msg.append(map.getTableName());
		msg.append(" specifies key fields of (");
		for (Iterator it = map.getKeyFields().iterator(); it.hasNext();) {
			String key = (String) it.next();
			msg.append(key);
			msg.append(", ");
		}
		msg.setLength(msg.length() - 2);
		msg.append(") but duplicates exist in database.");
		throw new IllegalStateException(msg.toString());
	}

	/**
	 * Return a packaged list of objects matching the query.
	 * 
	 * @param trans
	 * @param map
	 * @param query
	 * @return a List of objects matching the query.
	 * @throws SQLException
	 */
	public static final List listByQuery(Connection conn,
			TableMap map, PreparedSql query) throws SQLException {
		validateParams(map, query, "listByQuery");
		List list = new ArrayList();
		ResultSetToList processor = new ResultSetToList(map, list);
		SqlTool.executeQuery(query, conn, processor);
		return list;
	}

	/**
	 * Process a list of objects matching the query using the user-defined class
	 * extending Processor. Each row is packaged into a bean according to the
	 * TableMap, and then individually sent to the "process" method of the
	 * provided Processor object. In this way, even very large query results can
	 * be processed with very low memory consumption within your Java
	 * application.
	 * 
	 * @param conn
	 * @param map
	 * @param query
	 * @param objProcessor
	 * @return number of rows processed
	 * @throws SQLException
	 */
	public static final int processByQuery(Connection conn,
			TableMap map, PreparedSql query, Processor objProcessor)
			throws SQLException {
		validateParams(map, query, "processByQuery");
		ResultSetToProcessor processor = new ResultSetToProcessor(map,
				objProcessor);
		return SqlTool.executeQuery(query, conn, processor);
	}

	/**
	 * Return an integer value (such as a count) from your query.
	 * This assumes your query yields a single integer result and
	 * returns the intValue of the first column of the ResultSet.
	 * 
	 * @param conn
	 * @param query
	 * @return intValue of first column of first row of ResultSet
	 * @throws SQLException
	 */
	public static final int intQuery(Connection conn,
			PreparedSql query) throws SQLException {
		if (query == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform intQuery using a null query.");
			throw new IllegalArgumentException(msg.toString());
		}
		ResultSetToInt processor = new ResultSetToInt();
		return SqlTool.executeQuery(query, conn, processor);
	}

	/**
	 * Execute a delete query, returning number of rows.
	 * 
	 * @param conn
	 * @param query
	 * @return number of rows deleted
	 * @throws SQLException
	 */
	public static final int deleteByQuery(Connection conn,
			PreparedSql query) throws SQLException {
		if (query == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform deleteQuery using a null query.");
			throw new IllegalArgumentException(msg.toString());
		}
		if (!query.getSql().getString().toUpperCase().replace("\t", " ").trim().startsWith("DELETE ")) {
			throw new IllegalArgumentException("The deleteQuery query should start with the word 'DELETE'.");
		}
		return SqlTool.executeUpdate(query, conn);
	}

}
