package org.pojava.persistence.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Process a result set.
 * 
 * @author John Pile
 */
public interface ResultSetProcessor {

	/**
	 * Used as a callback to perform an action on a ResultSet.
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	int process(ResultSet rs) throws SQLException;

}
