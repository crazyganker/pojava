package org.pojava.persistence.util;

/*
 Copyright 2008 John Pile

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.pojava.datetime.DateTime;
import org.pojava.exception.InitializationException;
import org.pojava.exception.PersistenceException;
import org.pojava.lang.BoundString;
import org.pojava.lang.UncheckedBinding;
import org.pojava.persistence.processor.ResultSetProcessor;
import org.pojava.persistence.processor.ResultSetToInt;
import org.pojava.persistence.query.PreparedSql;
import org.pojava.persistence.sql.TableMap;
import org.pojava.testing.DriverManagerDataSource;

/**
 * Utilities for performing SQL tasks. POJava keeps things simple by using prepared statements.
 * This reduces your application's footprint on the cache your database uses to store unique SQL
 * statements. It bypasses issues arising from variations in supported date formats between
 * different databases. It also reduces your exposure to SQL injection vulnerabilities.
 * 
 * @author John Pile
 * 
 */
public class SqlTool {

    private static Logger logger = Logger.getLogger("org.pojava.persistence.util.SqlTool");
    
    /**
     * Execute an insert/update/delete query returning row count.
     * 
     * @param query
     * @param conn
     * @return row count applicable to statement executed
     * @throws SQLException
     */
    public static int executeUpdate(PreparedSql query, Connection conn) throws SQLException {
        PreparedStatement pstmt = generatePreparedStatement(query, conn);
        int result = pstmt.executeUpdate();
        pstmt.close();
        return result;
    }

    /**
     * Execute a select query, processing its ResultSet.
     * 
     * @param query
     * @param conn
     * @return ResultSetProcessor return value, typically number of rows processed
     * @throws SQLException
     */
    public static int executeQuery(PreparedSql query, Connection conn,
            ResultSetProcessor processor) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try {
            pstmt = generatePreparedStatement(query, conn);
            rs = pstmt.executeQuery();
            int result = processor.process(rs);
            return result;
        } finally {
            close(rs, pstmt);
        }
    }

    /**
     * Return an integer value (such as a count) from your query. This assumes your query yields
     * a single integer result and returns the intValue of the first column of the ResultSet.
     * 
     * @param conn
     * @param query
     * @return intValue of first column of first row of ResultSet
     */
    public static final int intQuery(Connection conn, PreparedSql query) {
        try {
            if (query == null) {
                StringBuffer msg = new StringBuffer();
                msg.append("Cannot perform intQuery using a null query.");
                throw new IllegalArgumentException(msg.toString());
            }
            ResultSetToInt processor = new ResultSetToInt();
            return SqlTool.executeQuery(query, conn, processor);
        } catch (SQLException ex) {
            throw new PersistenceException(ex.getMessage(), ex);
        }
    }

    /**
     * Generate a prepared statement using bindings.
     * 
     * @param preparedSql
     * @param conn
     * @return
     * @throws SQLException
     */
    private static PreparedStatement generatePreparedStatement(PreparedSql preparedSql,
            Connection conn) throws SQLException {
        BoundString bs = preparedSql.getSql();
        PreparedStatement pstmt = conn.prepareStatement(bs.getString());
        prepareBindings(pstmt, bs.getBindings());
        pstmt.setMaxRows(preparedSql.getMaxRows());
        return pstmt;
    }

    /**
     * Bind bindings to a prepared statement
     * 
     * @param pstmt
     *            PreparedStatement needing bindings
     * @param list
     *            Bindings to bind to the prepared statement
     * @throws SQLException
     */
    public static void prepareBindings(PreparedStatement pstmt, List<UncheckedBinding> list)
            throws SQLException {
        int i = 0;
        for (Iterator<UncheckedBinding> it = list.iterator(); it.hasNext();) {
            i++;
            UncheckedBinding binding = it.next();
            int sqlType = sqlTypeFromClass(binding.getType());
            Object o = binding.getObj();
            if (o == null) {
                pstmt.setNull(i, sqlType);
            } else {
                pstmt.setObject(i, o, sqlType);
            }
        }
    }

    /**
     * Multi-purpose class to SQL type translation
     * 
     * @param c
     *            Class of java object to persist
     * @return
     */
    private static int sqlTypeFromClass(Class<?> c) {
        int type = 0;
        if (c.equals(Integer.class) || c.equals(int.class)) {
            type = Types.INTEGER;
        } else if (c.equals(Long.class) || c.equals(long.class)) {
            type = Types.BIGINT;
        } else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
            type = Types.BIT;
        } else if (c.equals(Character.class) || c.equals(char.class)) {
            type = Types.CHAR;
        } else if (c.equals(java.sql.Date.class)) {
            type = Types.DATE;
        } else if (c.equals(DateTime.class) || c.equals(java.util.Date.class)
                || c.equals(Timestamp.class)) {
            type = Types.TIMESTAMP;
        } else if (c.equals(Double.class) || c.equals(double.class) || c.equals(Float.class)
                || c.equals(float.class) || c.equals(BigDecimal.class)) {
            type = Types.NUMERIC;
        } else if (c.equals(Time.class)) {
            type = Types.TIME;
        } else if (c.equals(Byte.class) || c.equals(byte.class)) {
            type = Types.TINYINT;
        } else {
            type = Types.VARCHAR;
        }
        return type;
    }

    /**
     * Blow up if we can't initialize properly.
     * 
     * @param ex
     * @param javaClass
     * @param tableName
     * @param dsName
     * @return InitializationException
     */
    private static InitializationException initializationException(SQLException ex,
            Class<?> javaClass, String tableName, String dsName) {
        StringBuffer msg = new StringBuffer();
        msg.append("Cannot auto-initialize TableMap(");
        msg.append(javaClass.toString());
        msg.append(",");
        msg.append(tableName);
        msg.append(",");
        msg.append(dsName);
        msg.append("): ");
        msg.append(ex.getMessage());
        return new InitializationException(msg.toString(), ex);
    }

    /**
     * Generate TableMap from database.
     * 
     * @param javaClass
     * @param tableName
     * @param dsName
     * @return TableMap retrieved from database MetaData
     */
    @SuppressWarnings("unchecked")
    public static TableMap<?> autoGenerateTableMap(Class<?> javaClass, String tableName,
            String dsName) {
        try {
            TableMap<?> tableMap = new TableMap(javaClass, tableName, dsName);
            tableMap.autoBind();
            return tableMap;
        } catch (SQLException ex) {
            throw initializationException(ex, javaClass, tableName, dsName);
        }
    }

    /**
     * Close a statement or prepared statement.
     * 
     * @param stmt
     *            Statement or Prepared statement ready to be closed
     */
    public static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            throw new PersistenceException(ex.getMessage(), ex);
        }
    }

    /**
     * Close spent JDBC resources. Ensures call to close on all three even if one throws an
     * exception during its closing.
     * 
     * @param rs
     *            ResultSet ready to be closed
     * @param stmt
     *            Statement ready to be closed
     * @param conn
     *            Connection ready to be closed
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        PersistenceException pex = null;
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            pex = new PersistenceException(ex.getMessage(), ex);
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            pex = new PersistenceException(ex.getMessage(), ex);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            pex = new PersistenceException(ex.getMessage(), ex);
        }
        if (pex != null) {
            throw pex;
        }
    }

    /**
     * Close a result set and prepared statement.
     * 
     * @param rs
     *            ResultSet ready to be closed
     * @param stmt
     *            Statement ready to be closed
     */
    public static void close(ResultSet rs, Statement stmt) {
        PersistenceException pex = null;
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            pex = new PersistenceException(ex.getMessage(), ex);
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            pex = new PersistenceException(ex.getMessage(), ex);
        }
        if (pex != null) {
            throw pex;
        }
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public static Properties fetchProperties(String propertyFile) {
        Properties dataSourceProps = new Properties();
        // override properties
        FileInputStream in = null;
        try {
            in = new FileInputStream(propertyFile);
            dataSourceProps.load(in);
        } catch (FileNotFoundException ex) {
            logger.log(Level.WARNING, "Could not find a property file named " + propertyFile, ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IOException occurred trying to read config/datastore.properties.", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.log(Level.WARNING, "IOException occurred trying to close config/datastore.properties.", ex);
            }
        }
        return dataSourceProps;
    }

    /**
     * Registers a DataSource into the InitialContext.
     * 
     * @param props
     *            A Properties object pre-populated with data
     * @param dsName
     *            The name of the DataSource to register in JNDI.
     * @throws NamingException
     * @throws ClassNotFoundException
     */
    public static void registerDataSource(Properties props, String dsName)
            throws NamingException, ClassNotFoundException {
        Context ctx = new InitialContext();
        try {
            boolean isRegistered = false;
            String driverClass = dsName + ".driver";
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                String listedClassName = driver.getClass().getName();
                if (driverClass.equals(listedClassName)
                        || "org.pojava.persistence.sql.DatabaseDriver".equals(listedClassName)) {
                    isRegistered = true;
                    break;
                }
            }
            if (!isRegistered) {
                Class.forName(props.getProperty(dsName + ".driver"));
            }
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "Could not register " + ex.getMessage(), ex);
        }
        DataSource ds = new DriverManagerDataSource(props.getProperty(dsName + ".url"), props
                .getProperty(dsName + ".user"), props.getProperty(dsName + ".password"));
        ctx.bind("java:/comp/env/jdbc/" + dsName.trim(), ds);

    }

    /**
     * Read DataSource from a properties object
     * @param props Properties collected from file
     * @param dsName Name identifying DataSource
     * @return DataSource
     * @throws NamingException
     * @throws ClassNotFoundException
     */
    public static DataSource readDataSource(Properties props, String dsName)
            throws NamingException, ClassNotFoundException {
        Class.forName(props.getProperty(dsName + ".driver"));
        DataSource ds = new DriverManagerDataSource(props.getProperty(dsName + ".url"), props
                .getProperty(dsName + ".user"), props.getProperty(dsName + ".password"));
        return ds;
    }

}
