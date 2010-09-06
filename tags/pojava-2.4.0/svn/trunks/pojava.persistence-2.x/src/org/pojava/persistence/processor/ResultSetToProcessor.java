package org.pojava.persistence.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pojava.lang.Processor;
import org.pojava.persistence.sql.TableMap;

/**
 * Process a ResultSet into a method implementing the Processor interface.
 * 
 * @author John Pile
 * 
 */
public class ResultSetToProcessor<T> implements ResultSetProcessor {

    private TableMap<T> map = null;
    private Processor<T> processor = null;

    /**
     * Constructor mapping a TableMap to a Processor.
     * 
     * @param map
     * @param processor
     */
    public ResultSetToProcessor(TableMap<T> map, Processor<T> processor) {
        if (map == null) {
            throw new IllegalArgumentException(
                    "Cannot construct a ResultSetToList with a null map.");
        }
        if (processor == null) {
            throw new IllegalArgumentException(
                    "Cannot construct a ResultSetToList with a null processor.");
        }
        this.map = map;
        this.processor = processor;
    }

    /**
     * Invoke the processor.process callback, passing each retrieved object.
     */
    public int process(ResultSet rs) throws SQLException {
        int rows = 0;
        while (rs.next()) {
            rows++;
            processor.process(map.extractObject(rs));
        }
        return rows;
    }

}
