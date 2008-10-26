package org.pojava.persistence.query;

import org.pojava.lang.BoundString;

/**
 * Prepared Sql holds a normalized SQL statement with positional binding
 * references. This object should be used for SQL that is ready to execute.
 * 
 * Note that timeout may be unsupported by a JDBC driver, so it may be ignored
 * by other parts of POJava framework unless it is known to be safe to use.
 * 
 * @author John Pile
 * 
 */
public class PreparedSql {

	/**
	 * sql holds a SQL statement along with its Binding references.
	 */
	BoundString sql;
	/**
	 * maxRows limits the number of rows returned by the JDBC driver.
	 * It may also be used to alter the SQL statement under certain conditions.
	 */
	int maxRows = 0;	

	/**
	 * Create PreparedSql from SQL stored in a BoundString with unlimited maxRows.
	 * @param sql
	 */
	public PreparedSql(BoundString sql) {
		this.sql = sql;
	}

	/**
	 * Create PreparedSql from SQL stored in a BoundString with specified maxRows.
	 * @param sql
	 * @param maxRows
	 */
	public PreparedSql(BoundString sql, int maxRows) {
		this.sql = sql;
		this.maxRows = maxRows;
	}

	/**
	 * Get SQL.
	 * @return
	 */
	public BoundString getSql() {
		return sql;
	}

	/**
	 * Get Max number of rows returnable by query.
	 * @return
	 */
	public int getMaxRows() {
		return maxRows;
	}

}
