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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pojava.exception.PersistenceException;
import org.pojava.lang.BoundString;
import org.pojava.lang.Processor;
import org.pojava.persistence.processor.ResultSetToInt;
import org.pojava.persistence.processor.ResultSetToList;
import org.pojava.persistence.processor.ResultSetToProcessor;
import org.pojava.persistence.query.PreparedSql;
import org.pojava.persistence.query.SqlQuery;
import org.pojava.persistence.sql.FieldMap;
import org.pojava.persistence.sql.TableMap;

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
	private static final <T> void validateParamsQuery(TableMap<T> map, SqlQuery query,
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
	private static final <T> void validateParamsList(TableMap<T> map, List<T> obj,
			String action) {
		if (obj == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("Cannot perform ");
			msg.append(action);
			msg.append(" on null list.");
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
	private static final <T> void validateParams(TableMap<T> map, T obj,
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
		if (!(obj instanceof List) && !obj.getClass().isAssignableFrom(map.getJavaClass())) {
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
	 * Perform a single insert, throwing an exception if the attempted insert
	 * fails. This will throw an SQLException if an existing record matches the
	 * primary key defined in the TableMap.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return number of rows inserted (should always be 1)
	 */
	public static final <T> int insert(Connection conn, TableMap<T> map, T obj) {
		validateParams(map, obj, "insert");
		try {
			PreparedSql query = new PreparedSql(map.sqlInsert(obj),
					DEFAULT_MAXROWS);
			return SqlTool.executeUpdate(query, conn);
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Perform a batch insert, throwing an exception if the attempted insert
	 * fails.  Pair this with processByQuery for fast high-volume transfers.
	 * 
	 * @param conn Open connection to a databases
	 * @param map
	 *            TableMap describing bean to table mappings
	 * @param list
	 *            List of objects of the class specified in map
	 * @return array of success values in same order as list
	 */
	public static final <T> int[] batchInsert(Connection conn, TableMap<T> map,
			List<T> list) {
		PreparedStatement pstmt = null;
		validateParamsList(map, list, "batchInsert");
		if (list == null || list.size() == 0) {
			return new int[0];
		}
		T obj = list.get(0);
		try {
			BoundString sql = map.sqlInsert(obj);
			pstmt = conn.prepareStatement(sql.getString());
			for (Iterator<T> it = list.iterator(); it.hasNext();) {
				obj = it.next();
				sql = map.sqlInsert(obj);
				SqlTool.prepareBindings(pstmt, sql.getBindings());
				pstmt.addBatch();
			}
			int[] statuses=pstmt.executeBatch();
			for (int i=0; i<statuses.length; i++) {
				if (statuses[i]==-2) {
					statuses[i]=1;
				}
			}
			return statuses;
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		} finally {
			SqlTool.close(pstmt);
		}
	}

	/**
	 * Update an existing object in the table according to the primary key, and
	 * insert a new record if one does not already exist.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return number of rows updated or inserted (should always be 1)
	 */
	public static final <T> int updateInsert(Connection conn, TableMap<T> map,
			T obj) {
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
	 */
	public static final <T> int passiveInsert(Connection conn, TableMap<T> map,
			T obj) {
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
	 */
	public static final <T> int update(Connection conn, TableMap<T> map, T obj) {
		validateParams(map, obj, "update");
		try {
			PreparedSql query = new PreparedSql(map.sqlUpdate(obj),
					DEFAULT_MAXROWS);
			return SqlTool.executeUpdate(query, conn);
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Delete an existing record from a table according to its primary key.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return Number of rows deleted.
	 */
	public static final <T> int delete(Connection conn, TableMap<T> map, T obj) {
		validateParams(map, obj, "delete");
		try {
			PreparedSql query = new PreparedSql(map.sqlDelete(obj),
					DEFAULT_MAXROWS);
			return SqlTool.executeUpdate(query, conn);
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}

	}

	/**
	 * Retrieve a single record according to its primary key.
	 * 
	 * @param conn
	 * @param map
	 * @param obj
	 * @return the result of the query packaged into a mapped bean.
	 */
	public static final <T> T find(Connection conn, TableMap<T> map, T obj) {
		validateParams(map, obj, "find");
		try {
			PreparedSql query = new PreparedSql(map.sqlSelect(obj),
					DEFAULT_MAXROWS);
			List<T> list = new ArrayList<T>();
			ResultSetToList<T> processor = new ResultSetToList<T>(map, list);
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
			for (Iterator<FieldMap<T,?,?>> it = map.getKeyFields().iterator(); it.hasNext();) {
				String key = (String) it.next().getColumnName();
				msg.append(key);
				msg.append(", ");
			}
			msg.setLength(msg.length() - 2);
			msg.append(") but duplicates exist in database.");
			throw new IllegalStateException(msg.toString());
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Return a packaged list of objects matching the query.
	 * 
	 * @param conn
	 * @param map
	 * @param query
	 * @return a List of objects matching the query.
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> listByQuery(Connection conn, TableMap<T> map,
			SqlQuery query) {
		validateParamsQuery(map, query, "listByQuery");
		try {
			PreparedSql sql = query.generatePreparedSql(map.sqlSelect());
			List<T> list = new ArrayList();
			ResultSetToList<T> processor = new ResultSetToList(map, list);
			SqlTool.executeQuery(sql, conn, processor);
			return list;
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}

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
	 */
	public static final<T> int processByQuery(Connection conn, TableMap<T> map,
			SqlQuery query, Processor objProcessor) {
		validateParamsQuery(map, query, "processByQuery");
		try {
			ResultSetToProcessor<T> processor = new ResultSetToProcessor<T>(map,
					objProcessor);
			return SqlTool.executeQuery(query.generatePreparedSql(map
					.sqlSelect()), conn, processor);
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Return a count of rows matching your query.
	 * 
	 * @param conn
	 * @param query
	 * @return intValue of first column of first row of ResultSet
	 */
	public static final <T> int countByQuery(Connection conn, TableMap<T> map,
			SqlQuery query) {
		try {
			if (query == null) {
				StringBuffer msg = new StringBuffer();
				msg.append("Cannot perform countByQuery using a null query.");
				throw new IllegalArgumentException(msg.toString());
			}
			PreparedSql sql = query.generatePreparedSql("SELECT COUNT(*) FROM "
					+ map.getTableName());
			ResultSetToInt processor = new ResultSetToInt();
			return SqlTool.executeQuery(sql, conn, processor);
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

	/**
	 * Execute a delete query, returning number of rows.
	 * 
	 * @param conn
	 * @param query
	 * @return number of rows deleted
	 */
	public static final <T> int deleteByQuery(Connection conn, TableMap<T> map,
			SqlQuery query) {
		try {
			if (query == null) {
				StringBuffer msg = new StringBuffer();
				msg.append("Cannot perform deleteQuery using a null query.");
				throw new IllegalArgumentException(msg.toString());
			}
			PreparedSql sql = query.generatePreparedSql("DELETE FROM "
					+ map.getTableName());
			return SqlTool.executeUpdate(sql, conn);
		} catch (SQLException ex) {
			throw new PersistenceException(ex.getMessage(), ex);
		}
	}

}
