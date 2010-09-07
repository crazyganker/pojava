package org.pojava.persistence.sql;

/**
 * This interface describes expected functionality from a Transaction object
 * specific to JDBC.
 * 
 * @author John Pile
 */
public interface Transaction {

	/**
	 * Commit all managed connections
	 */
	void commit();

	/**
	 * Roll back all managed connections.
	 */
	void rollback();

}
