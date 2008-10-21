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

	public PreparedSql(BoundString sql) {
		this.sql = sql;
	}

	public PreparedSql(BoundString sql, int maxRows) {
		this.sql = sql;
		this.maxRows = maxRows;
	}

	public BoundString getSql() {
		return sql;
	}

	public int getMaxRows() {
		return maxRows;
	}

}
