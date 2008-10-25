package org.pojava.persistence.sql;

import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.pojava.datetime.CalendarUnit;
import org.pojava.datetime.DateTime;
import org.pojava.persistence.examples.Mock;
import org.pojava.persistence.examples.PrimitiveTest;
import org.pojava.persistence.examples.PrimitiveTestDao;
import org.pojava.persistence.examples.TypeTestDao;
import org.pojava.persistence.examples.TypeTestQuery;
import org.pojava.testing.DriverManagerDataSource;
import org.pojava.testing.JNDIRegistry;
import org.pojava.util.StringTool;

public class DaoTesterPrimitives extends TestCase {

	private DatabaseTransaction trans = null;

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
	}

	protected void tearDown() throws Exception {
		// trans.commit();
		trans.rollback();
	}

	public void testInsertPrimitive() throws Exception {
		PrimitiveTest obj = Mock.newPrimitiveTest(1);
		int ct = PrimitiveTestDao.insert(trans, obj);
		assertEquals(1, ct);
		PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
		assertEquals(obj.isTestBoolean(), dbo.isTestBoolean());
		assertEquals(obj.getTestBigint(), dbo.getTestBigint());
		assertEquals(obj.getTestDouble(), dbo.getTestDouble(), 0);
		assertEquals(obj.getTestNumeric_10x4(), dbo.getTestNumeric_10x4(), 0.0001);
		assertEquals(obj.getTestCharacter1(), dbo.getTestCharacter1());
		assertEquals(StringTool.pad(obj.getTestCharacter5(),5), dbo.getTestCharacter5());
		assertEquals(new DateTime(obj.getTestDate().getTime()).truncate(CalendarUnit.DAY), dbo.getTestDate());
		assertEquals(obj.getTestId(), dbo.getTestId());
		assertEquals(obj.getTestReal(), dbo.getTestReal(), 0);
		assertEquals(obj.getTestSmallint(), dbo.getTestSmallint());
		assertEquals(obj.getTestTimestampWithoutTz(), dbo.getTestTimestampWithoutTz());
		assertEquals(obj.getTestTimestampWithTz(), dbo.getTestTimestampWithTz());
		assertEquals(obj.getTestTimeWithoutTz(), dbo.getTestTimeWithoutTz());
		assertEquals(obj.getTestTimeWithTz(), dbo.getTestTimeWithTz());
		assertEquals(obj.getTestVarchar1(), dbo.getTestVarchar1());
		assertEquals(obj.getTestVarchar5(), dbo.getTestVarchar5());
	}

}
