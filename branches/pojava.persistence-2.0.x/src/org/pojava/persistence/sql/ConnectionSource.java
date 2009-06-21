package org.pojava.persistence.sql;

import java.sql.Connection;

/**
 * A connection source provides an interface allowing one to request a connection from a
 * repository by a name.
 * 
 * @author John Pile
 * 
 */
public interface ConnectionSource {

    /**
     * Retrieve connection (will return an existing connection for the same dataSourceName if
     * one is available).
     * 
     * @param dataSourceName
     * @return Connection
     */
    Connection getConnection(String dataSourceName);

}
