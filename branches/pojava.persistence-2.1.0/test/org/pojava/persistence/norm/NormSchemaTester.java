package org.pojava.persistence.norm;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.DatabaseTransaction;
import org.pojava.persistence.sql.TestHelper;
import org.pojava.persistence.util.SqlTool;
import org.pojava.testing.DriverManagerDataSource;
import org.pojava.testing.JNDIRegistry;

import junit.framework.TestCase;

public class NormSchemaTester extends TestCase {

    private DatabaseTransaction trans = null;

    protected void setUp() throws Exception {
        super.setUp();
        JNDIRegistry.getInitialContext();
        Properties dsp = TestHelper.fetchDataSourceProperties();
        if (dsp != null) {
            Class.forName(dsp.getProperty("driver"));
            DataSource ds = new DriverManagerDataSource(dsp.getProperty("url"), dsp
                    .getProperty("user"), dsp.getProperty("password"));
            DatabaseCache.registerDataSource(dsp.getProperty("name"), ds);
            trans = new DatabaseTransaction();
        }
    }

    protected void tearDown() throws Exception {
        trans.rollback();
        super.tearDown();
    }

    public void testScanTable() throws Exception {
        DataSource ds = DatabaseCache.getDataSource("pojava_test");
        Connection conn = null;
        try {
            conn = ds.getConnection();
            Norm.scanTable(conn, "test");
        } finally {
            SqlTool.close(conn);
        }

    }
}
