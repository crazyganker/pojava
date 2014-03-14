package org.pojava.persistence.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * This class keeps some useful information about a DataSource without holding open a
 * Connection.
 *
 * @author John Pile
 */
public class DataSourceMetadata {

    /**
     * Platform is probably of the most interest of these fields.
     */
    private String platform;
    /**
     * Class loaded for this driver.
     */
    private String driverName;
    /**
     * Keywords supported by driver.
     */
    private String sqlKeywords;
    /**
     * Username under which driver is authenticated
     */
    private String userName;
    /**
     * Major version
     */
    private int majorVersion;
    /**
     * Minor version.
     */
    private int minorVersion;

    /**
     * Default constructor.
     */
    public DataSourceMetadata() {
    }

    /**
     * Construct a
     *
     * @param conn Connection
     * @throws java.sql.SQLException
     */
    public DataSourceMetadata(Connection conn) throws SQLException {
        populateFrom(conn);
    }

    /**
     * Mock up a new connection
     *
     * @param conn Connection
     * @throws java.sql.SQLException
     */
    public void populateFrom(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        platform = meta.getDatabaseProductName();
        majorVersion = meta.getDatabaseMajorVersion();
        minorVersion = meta.getDatabaseMinorVersion();
        driverName = meta.getDriverName();
        sqlKeywords = meta.getSQLKeywords();
        userName = meta.getUserName();
    }

    /**
     * @return Platform describes the database platform.
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Platform retrieved from MetaData
     *
     * @param platform Database platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getSqlKeywords() {
        return sqlKeywords;
    }

    public void setSqlKeywords(String sqlKeywords) {
        this.sqlKeywords = sqlKeywords;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

}
