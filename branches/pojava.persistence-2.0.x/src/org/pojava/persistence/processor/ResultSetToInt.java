package org.pojava.persistence.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Process a single int value from the first column of a ResultSet.
 * 
 * @author John Pile
 * 
 */
public class ResultSetToInt implements ResultSetProcessor {

    /**
     * Process the first element of the first row of a ResultSet, returning as an integer.
     */
    public int process(ResultSet rs) throws SQLException {
        int result = 0;
        if (rs.next()) {
            result = rs.getInt(1);
        }
        return result;
    }

}
