package org.projava.persistence.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.pojava.persistence.sql.TableMap;

/**
 * This class processes a data set into a list of objects.
 * 
 * @author John Pile
 *
 */
public class ResultSetToList implements ResultSetProcessor {

	private List list = null;

	private TableMap map = null;

	/**
	 * This processor populates a list from the result set.
	 * @param map
	 * @param list
	 */
	public ResultSetToList(TableMap map, List list) {
		if (map == null) {
			throw new IllegalArgumentException(
					"Cannot construct a ResultSetToList with a null map.");
		}
		if (list == null) {
			throw new IllegalArgumentException(
					"Cannot construct a ResultSetToList with a null list.");
		}
		this.list = list;
		this.map = map;
	}

	/**
	 * Populate each row into a mapped bean, added to a list.
	 */
	public int process(ResultSet rs) throws SQLException {
		int rows = 0;
		while (rs.next()) {
			rows++;
			list.add(map.extractObject(rs));
		}
		return rows;
	}

}
