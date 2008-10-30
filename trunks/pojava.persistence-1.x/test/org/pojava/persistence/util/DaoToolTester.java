package org.pojava.persistence.util;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.pojava.persistence.examples.TypeTest;
import org.pojava.persistence.examples.TypeTestDao;
import org.pojava.persistence.examples.TypeTestQuery;
import org.pojava.persistence.examples.Mock;
import org.pojava.persistence.sql.DatabaseCache;
import org.pojava.persistence.sql.DatabaseTransaction;
import org.pojava.persistence.sql.DistributedTransaction;
import org.pojava.persistence.sql.TableMap;
import org.pojava.persistence.sql.TestHelper;
import org.pojava.testing.DriverManagerDataSource;
import org.pojava.testing.JNDIRegistry;

public class DaoToolTester extends TestCase {

	private static final Class JAVA_CLASS = TypeTest.class;
	private static final String TABLE_NAME = "dao_test";
	private static final String DS_NAME = "pojava_test";
	private DatabaseTransaction trans = null;
	private TableMap MAP = null;

	protected void setUp() throws Exception {
		JNDIRegistry.getInitialContext();
		Properties dsp = TestHelper.fetchDataSourceProperties();
		if (dsp != null) {
			Class.forName(dsp.getProperty("driver"));
			DataSource ds = new DriverManagerDataSource(dsp.getProperty("url"),
					dsp.getProperty("user"), dsp.getProperty("password"));
			DatabaseCache.registerDataSource(dsp.getProperty("name"), ds);
			trans = new DistributedTransaction();
			TypeTestDao.deleteByQuery(trans, new TypeTestQuery().forAll());
		}		
		MAP = DatabaseCache.getTableMap(JAVA_CLASS, TABLE_NAME, DS_NAME);
	}

	protected void tearDown() throws Exception {
		trans.rollback();
		super.tearDown();
	}

	public void testNullParams() throws SQLException {
		TypeTest obj = Mock.newTypeTest(1);
		// Null Transaction
		try {
			DaoTool.update(null, MAP, obj);
			fail("Expecting IllegalArgumentException");
		} catch (NullPointerException ex) {
			// Expected. It is the Dao's responsibility to catch this.
		}
		// Null MAP
		try {
			DaoTool.update(trans.getConnection(DS_NAME), null, obj);
			fail("Expecting IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			// Expected.
		}
		// Null object
		try {
			DaoTool.update(trans.getConnection(DS_NAME), MAP, null);
			fail("Expecting IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			// Expected.
		}
	}

}
