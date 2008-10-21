package org.pojava.persistence.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Process a result set.
 * @author John Pile
 */
public interface ResultSetProcessor {

	int process(ResultSet rs) throws SQLException ;
	
}
