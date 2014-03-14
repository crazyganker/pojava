package org.pojava.persistence.jndi;

/*
 Copyright 2008-09 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * DriverManagerDataSource provides a simple DataSource object useful for unit testing.
 *
 * @author John Pile
 */
public class DriverManagerDataSource implements DataSource {

    private static Logger LOGGER = Logger.getLogger("org.pojava.persistence.jndi.DriverManagerDataSource");

    /**
     * Database url
     */
    String url;
    /**
     * Database user account
     */
    String user;
    /**
     * Database password
     */
    String password;
    /**
     * Unused compulsory loginTimeout
     */
    int loginTimeout = 0;
    /**
     * This logWriter starts out as the DriverManager's logWriter, but can be overridden.
     */
    PrintWriter logWriter;

    /**
     * Create a DataSource that uses the DriverManager to provide the connection.
     *
     * @param url      URL of data source
     * @param user     User authenticating connection
     * @param password Password authenticating connection
     */
    public DriverManagerDataSource(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Retrieve a new connection from the DriverManager
     */
    public Connection getConnection() throws SQLException {
        Connection conn;
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    /**
     * Retrieve a new connection for a specific userName and password.
     */
    public Connection getConnection(String userName, String password) throws SQLException {
        Connection conn;
        conn = DriverManager.getConnection(this.url, userName, password);
        return conn;
    }

    /**
     * Return the loginTimeout value that really isn't used.
     */
    public int getLoginTimeout() {
        return this.loginTimeout;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }

    /**
     * Compulsory unwrap method. Unsupported.
     */
    public <T> T unwrap(Class<T> c) {
        return null;
    }

    /**
     * Set the loginTimeout value that really isn't used.
     */
    public void setLoginTimeout(int time) {
        this.loginTimeout = time;
    }

    /**
     * Return the logWriter that is initially set to DriverManager's logWriter.
     */
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    /**
     * Set your own custom logWriter.
     */
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Unsupported.
     */
    public boolean isWrapperFor(Class<?> type) {
        return false;
    }

}
