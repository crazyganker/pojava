package org.pojava.persistence.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.pojava.persistence.examples.Mock;
import org.pojava.persistence.examples.PrimitiveTest;
import org.pojava.persistence.examples.PrimitiveTestDao;
import org.pojava.persistence.examples.TypeTest;
import org.pojava.persistence.examples.TypeTestDao;
import org.pojava.persistence.examples.TypeTestQuery;
import org.pojava.testing.DriverManagerDataSource;
import org.pojava.testing.JNDIRegistry;

public class DaoTester extends TestCase {

	private DatabaseTransaction trans = null;

	protected void setUp() throws Exception {
		JNDIRegistry.getInitialContext();
		Properties dsp = fetchDataSourceProperties();
		if (dsp != null) {
			Class.forName(dsp.getProperty("driver"));
			DataSource ds = new DriverManagerDataSource(dsp.getProperty("url"),
					dsp.getProperty("user"), dsp.getProperty("password"));
			DatabaseCache.registerDataSource(dsp.getProperty("name"), ds);
			trans = new DistributedTransaction();
			TypeTestDao.deleteByQuery(trans, new TypeTestQuery().forAll());
		}
	}

	protected void tearDown() throws Exception {
		// trans.commit();
		trans.rollback();
	}

	protected Properties fetchDataSourceProperties() {
		// default properties
		Properties defaultProps = new Properties();
		Properties dataSourceProps = null;
		// override properties
		try {
			dataSourceProps = new Properties(defaultProps);
			FileInputStream in = new FileInputStream(
					"config/datasource.properties");
			dataSourceProps.load(in);
			in.close();
		} catch (FileNotFoundException ex) {
			instructions();
		} catch (IOException ex) {
			System.out
					.println("IOException occurred trying to read config/datastore.properties.\n");
			ex.printStackTrace();
		}
		return dataSourceProps;
	}

	private void instructions() {
		String path = new File(".").getAbsolutePath();
		StringBuffer sb = new StringBuffer();
		sb.append("INSTRUCTIONS:  Your system must be configured for these");
		sb.append(" unit tests.\nCreate a \"config\" folder under ");
		sb.append(path);
		sb.append("\nand create in it a property file named");
		sb.append(" datasource.properties.\n");
		sb.append("That file must specify where to find a database");
		sb.append(" table for testing.\nIt will provide a");
		sb.append(" driver, url, user, password, and datasource name.\n");
		sb.append("These must match a table and driver you create on");
		sb.append(" your own database.\n");
		sb.append("Here's an example.  Please customize the file to");
		sb.append(" fit your environment\n");
		sb.append("(not the other way around).\n\n");
		sb.append("driver = org.postgresql.Driver\n");
		sb.append("url = jdbc:postgresql://localhost:5432/postgres\n");
		sb.append("user = pojava\n");
		sb.append("password = popojava\n");
		sb.append("name = pojava_pg\n");
		System.out.println(sb.toString());
		System.exit(0);
	}

	public void testInsert() throws Exception {
		TypeTest obj = Mock.newTypeTest(1);
		int ct = TypeTestDao.insert(trans, obj);
		assertEquals(1, ct);
	}

	public void testInsertPrimitive() throws Exception {
		PrimitiveTest obj = Mock.newPrimitiveTest(1);
		int ct = PrimitiveTestDao.insert(trans, obj);
		assertEquals(1, ct);
	}

}
