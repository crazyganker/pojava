package org.pojava.persistence.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.pojava.datetime.CalendarUnit;
import org.pojava.lang.Processor;
import org.pojava.persistence.examples.Mock;
import org.pojava.persistence.examples.TypeTest;
import org.pojava.persistence.examples.TypeTestDao;
import org.pojava.persistence.examples.TypeTestQuery;
import org.pojava.testing.DriverManagerDataSource;
import org.pojava.testing.JNDIRegistry;
import org.pojava.util.StringTool;

public class DaoTester extends TestCase {

	private DatabaseTransaction trans = null;

	protected void setUp() throws Exception {
		JNDIRegistry.getInitialContext();
		Properties dsp = TestHelper.fetchDataSourceProperties();
		if (dsp != null) {
			Class.forName(dsp.getProperty("driver"));
			DataSource ds = new DriverManagerDataSource(dsp.getProperty("url"),
					dsp.getProperty("user"), dsp.getProperty("password"));
			DatabaseCache.registerDataSource(dsp.getProperty("name"), ds);
			trans = new DatabaseTransaction();
			TypeTestDao.deleteByQuery(trans, new TypeTestQuery().forAll());
		}
	}

	protected void tearDown() throws Exception {
		// trans.commit();
		trans.rollback();
	}

	public void compare(TypeTest expected, TypeTest got) {
		assertEquals(expected.getTestBoolean(), got.getTestBoolean());
		assertEquals(expected.getTestBigint(), got.getTestBigint());
		assertEquals(expected.getTestDouble(), got.getTestDouble());
		assertEquals(expected.getTestNumeric_10x4(), got.getTestNumeric_10x4());
		assertEquals(expected.getTestCharacter1(), got.getTestCharacter1());
		assertEquals(StringTool.pad(expected.getTestCharacter5(), 5), got
				.getTestCharacter5());
		assertEquals(new org.pojava.datetime.DateTime(expected.getTestDate()
				.toMillis()).truncate(CalendarUnit.DAY), got.getTestDate());
		assertEquals(expected.getTestId(), got.getTestId());
		assertEquals(expected.getTestReal(), got.getTestReal());
		assertEquals(expected.getTestSmallint(), got.getTestSmallint());
		assertEquals(expected.getTestTimestampWithoutTz(), got
				.getTestTimestampWithoutTz());
		assertEquals(expected.getTestTimestampWithTz(), got
				.getTestTimestampWithTz());
		assertEquals(expected.getTestTimeWithoutTz(), got
				.getTestTimeWithoutTz());
		assertEquals(expected.getTestTimeWithTz(), got.getTestTimeWithTz());
		assertEquals(expected.getTestVarchar1(), got.getTestVarchar1());
		assertEquals(expected.getTestVarchar5(), got.getTestVarchar5());
	}

	public void insertSampleDataForAll() throws SQLException {
		TypeTest obj1 = Mock.newTypeTest(1);
		TypeTest obj2 = Mock.newTypeTest(2);
		TypeTest obj3 = Mock.newTypeTest(3);
		assertEquals(1, TypeTestDao.insert(trans, obj1));
		assertEquals(1, TypeTestDao.insert(trans, obj2));
		assertEquals(1, TypeTestDao.insert(trans, obj3));
	}

	public void insertSampleDataForSome() throws SQLException {
		TypeTest obj1 = Mock.newTypeTest(1);
		TypeTest obj2 = Mock.newTypeTest(2);
		TypeTest obj11 = Mock.newTypeTest(11);
		TypeTest obj22 = Mock.newTypeTest(22);
		assertEquals(1, TypeTestDao.insert(trans, obj1));
		assertEquals(1, TypeTestDao.insert(trans, obj2));
		assertEquals(1, TypeTestDao.insert(trans, obj11));
		assertEquals(1, TypeTestDao.insert(trans, obj22));
	}

	public void testInsert() throws Exception {
		TypeTest obj = Mock.newTypeTest(1);
		int ct = TypeTestDao.insert(trans, obj);
		assertEquals(1, ct);
		TypeTest dbo = TypeTestDao.find(trans, obj);
		compare(obj, dbo);
	}

	public void testUpdate() throws Exception {
		TypeTest initial = Mock.newTypeTest(1);
		int ct = TypeTestDao.insert(trans, initial);
		assertEquals(1, ct);
		// Fetch a different object
		TypeTest obj = Mock.newTypeTest(2);
		// Set the key fields the same as the first.
		obj.setTestId(initial.getTestId());
		// Perform the update.
		ct = TypeTestDao.update(trans, obj);
		// Retrieve the results.
		TypeTest dbo = TypeTestDao.find(trans, obj);
		compare(obj, dbo);
	}

	public void testUpdateInsertNew() throws Exception {
		TypeTest obj = Mock.newTypeTest(1);
		int ct = TypeTestDao.updateInsert(trans, obj);
		assertEquals(1, ct);
		// Retrieve the results.
		TypeTest dbo = TypeTestDao.find(trans, obj);
		compare(obj, dbo);
	}

	public void testUpdateInsertExisting() throws Exception {
		TypeTest initial = Mock.newTypeTest(1);
		int ct = TypeTestDao.insert(trans, initial);
		assertEquals(1, ct);
		// Fetch a different object
		TypeTest obj = Mock.newTypeTest(2);
		// Set the key fields the same as the first.
		obj.setTestId(initial.getTestId());
		// Perform the update.
		ct = TypeTestDao.updateInsert(trans, obj);
		// Retrieve the results.
		TypeTest dbo = TypeTestDao.find(trans, obj);
		compare(obj, dbo);
	}

	public void testPassiveInsertNew() throws Exception {
		TypeTest obj = Mock.newTypeTest(1);
		int ct = TypeTestDao.passiveInsert(trans, obj);
		assertEquals(1, ct);
		// Retrieve the results.
		TypeTest dbo = TypeTestDao.find(trans, obj);
		compare(obj, dbo);
	}

	public void testPassiveInsertExisting() throws Exception {
		TypeTest initial = Mock.newTypeTest(1);
		int ct = TypeTestDao.insert(trans, initial);
		assertEquals(1, ct);
		// Fetch a different object
		TypeTest obj = Mock.newTypeTest(2);
		// Set the key fields the same as the first.
		obj.setTestId(initial.getTestId());
		// Perform the insert.
		ct = TypeTestDao.passiveInsert(trans, obj);
		// Retrieve the results.
		TypeTest dbo = TypeTestDao.find(trans, obj);
		assertTrue(dbo.getTestBigint() != obj.getTestBigint());
		assertEquals(initial.getTestBigint(), dbo.getTestBigint());
	}

	public void testDelete() throws Exception {
		TypeTest obj = Mock.newTypeTest(1);
		int ct = TypeTestDao.insert(trans, obj);
		assertEquals(1, ct);
		TypeTest dbo = TypeTestDao.find(trans, obj);
		assertNotNull(dbo);
		ct = TypeTestDao.delete(trans, obj);
		assertEquals(1, ct);
		dbo = TypeTestDao.find(trans, obj);
		assertNull(dbo);
	}

	public void testDeleteNothing() throws Exception {
		TypeTest obj = Mock.newTypeTest(123);
		int ct = TypeTestDao.delete(trans, obj);
		assertEquals(0, ct);
	}

	public void testListByQueryForAll() throws Exception {
		insertSampleDataForAll();
		TypeTestQuery ptq = new TypeTestQuery().forAll();
		List<TypeTest> objs = TypeTestDao.listByQuery(trans, ptq);
		assertEquals(3, objs.size());
	}

	public void testListByQueryForSome() throws Exception {
		insertSampleDataForSome();
		TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
		List<TypeTest> objs = TypeTestDao.listByQuery(trans, ptq);
		assertEquals(2, objs.size());
	}

	public void testDeleteByQueryForAll() throws Exception {
		insertSampleDataForAll();
		TypeTestQuery ptq = new TypeTestQuery().forAll();
		int ct = TypeTestDao.deleteByQuery(trans, ptq);
		assertEquals(3, ct);
	}

	public void testDeleteByQueryForSome() throws Exception {
		insertSampleDataForSome();
		TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
		int ct = TypeTestDao.deleteByQuery(trans, ptq);
		assertEquals(2, ct);
	}

	public void testCountByQueryForAll() throws Exception {
		insertSampleDataForAll();
		TypeTestQuery ptq = new TypeTestQuery().forAll();
		int ct = TypeTestDao.countByQuery(trans, ptq);
		assertEquals(3, ct);
	}

	public void testCountByQueryForSome() throws Exception {
		insertSampleDataForSome();
		TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
		int ct = TypeTestDao.countByQuery(trans, ptq);
		assertEquals(2, ct);
	}

	public void testProcessByQueryForAll() throws Exception {
		TypeTest obj1 = Mock.newTypeTest(1);
		TypeTest obj2 = Mock.newTypeTest(2);
		TypeTest obj3 = Mock.newTypeTest(3);
		assertEquals(1, TypeTestDao.insert(trans, obj1));
		assertEquals(1, TypeTestDao.insert(trans, obj2));
		assertEquals(1, TypeTestDao.insert(trans, obj3));
		TypeTestQuery ptq = new TypeTestQuery().forAll();
		TypeTestProcessor proc = new TypeTestProcessor();
		int ct = TypeTestDao.processByQuery(trans, ptq, proc);
		assertEquals(3, ct);
		assertEquals(obj1.getTestDouble().doubleValue()
				+ obj2.getTestDouble().doubleValue()
				+ obj3.getTestDouble().doubleValue(), proc.getSum(), 0);
	}

	public void testProcessByQueryForSome() throws Exception {
		TypeTest obj1 = Mock.newTypeTest(1);
		TypeTest obj2 = Mock.newTypeTest(2);
		TypeTest obj11 = Mock.newTypeTest(11);
		TypeTest obj22 = Mock.newTypeTest(22);
		assertEquals(1, TypeTestDao.insert(trans, obj1));
		assertEquals(1, TypeTestDao.insert(trans, obj2));
		assertEquals(1, TypeTestDao.insert(trans, obj11));
		assertEquals(1, TypeTestDao.insert(trans, obj22));
		TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
		TypeTestProcessor proc = new TypeTestProcessor();
		int ct = TypeTestDao.processByQuery(trans, ptq, proc);
		assertEquals(2, ct);
		assertEquals(obj11.getTestDouble().doubleValue()
				+ obj22.getTestDouble().doubleValue(), proc.getSum(), 0);
	}

	private class TypeTestProcessor implements Processor {
		private double sum = 0.0;

		public int process(Object obj) {
			TypeTest bean = (TypeTest) obj;
			if (bean != null) {
				sum += bean.getTestDouble().doubleValue();
			}
			return 1;
		}

		public double getSum() {
			return sum;
		}
	}

}
