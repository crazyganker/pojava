package org.pojava.persistence.query;

import org.pojava.lang.BoundString;

/**
 * PreparedSqlProvider describes the SQL DAO-facing interface of an object extending
 * AbstractQuery.
 *
 * @author John Pile
 */
public interface PreparedSqlProvider {

    /**
     * This method distills the specified criteria of a query into a SQL-specific object.
     *
     * @return a a SQL statement ready for processing.
     */
    public PreparedSql generatePreparedSql(String prefix);

    /**
     * This method distills the specified criteria of a query into a SQL-specific object.
     *
     * @return a a SQL statement ready for processing.
     */
    public PreparedSql generatePreparedSql(BoundString prefix);

}
