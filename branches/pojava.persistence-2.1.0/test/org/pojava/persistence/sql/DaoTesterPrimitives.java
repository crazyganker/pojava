package org.pojava.persistence.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.pojava.datetime.CalendarUnit;
import org.pojava.datetime.DateTime;
import org.pojava.lang.Processor;
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
            DataSource ds = new DriverManagerDataSource(dsp.getProperty("url"), dsp
                    .getProperty("user"), dsp.getProperty("password"));
            DatabaseCache.registerDataSource(dsp.getProperty("name"), ds);
            trans = new DatabaseTransaction();
            TypeTestDao.deleteByQuery(trans, new TypeTestQuery().forAll());
        }
    }

    protected void tearDown() throws Exception {
        // trans.commit();
        trans.rollback();
    }

    public void compare(PrimitiveTest obj, PrimitiveTest dbo) {
        assertEquals(obj.isTestBoolean(), dbo.isTestBoolean());
        assertEquals(obj.getTestBigint(), dbo.getTestBigint());
        assertEquals(obj.getTestDouble(), dbo.getTestDouble(), 0);
        assertEquals(obj.getTestNumeric_10x4(), dbo.getTestNumeric_10x4(), 0.0001);
        assertEquals(obj.getTestCharacter1(), dbo.getTestCharacter1());
        assertEquals(StringTool.pad(obj.getTestCharacter5(), 5), dbo.getTestCharacter5());
        assertEquals(new DateTime(obj.getTestDate().getTime()).truncate(CalendarUnit.DAY)
                .toDate(), dbo.getTestDate());
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

    public void insertSampleDataForAll() throws SQLException {
        PrimitiveTest obj1 = Mock.newPrimitiveTest(1);
        PrimitiveTest obj2 = Mock.newPrimitiveTest(2);
        PrimitiveTest obj3 = Mock.newPrimitiveTest(3);
        assertEquals(1, PrimitiveTestDao.insert(trans, obj1));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj2));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj3));
    }

    public void insertSampleDataForSome() throws SQLException {
        PrimitiveTest obj1 = Mock.newPrimitiveTest(1);
        PrimitiveTest obj2 = Mock.newPrimitiveTest(2);
        PrimitiveTest obj11 = Mock.newPrimitiveTest(11);
        PrimitiveTest obj22 = Mock.newPrimitiveTest(22);
        assertEquals(1, PrimitiveTestDao.insert(trans, obj1));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj2));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj11));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj22));
    }

    public void testInsert() throws Exception {
        PrimitiveTest obj = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.insert(trans, obj);
        assertEquals(1, ct);
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        compare(obj, dbo);
    }

    public void testBatchInsert() throws Exception {
        List<PrimitiveTest> list = new ArrayList<PrimitiveTest>();
        list.add(Mock.newPrimitiveTest(1));
        list.add(Mock.newPrimitiveTest(2));
        list.add(Mock.newPrimitiveTest(3));
        int[] result = PrimitiveTestDao.batchInsert(trans, list);
        assertEquals(3, result.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(1, result[i]);
        }
        PrimitiveTest obj = Mock.newPrimitiveTest(2);
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        compare(obj, dbo);
    }

    public void testUpdate() throws Exception {
        PrimitiveTest initial = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.insert(trans, initial);
        assertEquals(1, ct);
        // Fetch a different object
        PrimitiveTest obj = Mock.newPrimitiveTest(2);
        // Set the key fields the same as the first.
        obj.setTestId(initial.getTestId());
        // Perform the update.
        ct = PrimitiveTestDao.update(trans, obj);
        // Retrieve the results.
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        compare(obj, dbo);
    }

    public void testUpdateInsertNew() throws Exception {
        PrimitiveTest obj = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.updateInsert(trans, obj);
        assertEquals(1, ct);
        // Retrieve the results.
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        compare(obj, dbo);
    }

    public void testUpdateInsertExisting() throws Exception {
        PrimitiveTest initial = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.insert(trans, initial);
        assertEquals(1, ct);
        // Fetch a different object
        PrimitiveTest obj = Mock.newPrimitiveTest(2);
        // Set the key fields the same as the first.
        obj.setTestId(initial.getTestId());
        // Perform the update.
        ct = PrimitiveTestDao.updateInsert(trans, obj);
        // Retrieve the results.
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        compare(obj, dbo);
    }

    public void testPassiveInsertNew() throws Exception {
        PrimitiveTest obj = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.passiveInsert(trans, obj);
        assertEquals(1, ct);
        // Retrieve the results.
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        compare(obj, dbo);
    }

    public void testPassiveInsertExisting() throws Exception {
        PrimitiveTest initial = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.insert(trans, initial);
        assertEquals(1, ct);
        // Fetch a different object
        PrimitiveTest obj = Mock.newPrimitiveTest(2);
        // Set the key fields the same as the first.
        obj.setTestId(initial.getTestId());
        // Perform the insert.
        ct = PrimitiveTestDao.passiveInsert(trans, obj);
        // Retrieve the results.
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        assertTrue(dbo.getTestBigint() != obj.getTestBigint());
        assertEquals(initial.getTestBigint(), dbo.getTestBigint());
    }

    public void testDelete() throws Exception {
        PrimitiveTest obj = Mock.newPrimitiveTest(1);
        int ct = PrimitiveTestDao.insert(trans, obj);
        assertEquals(1, ct);
        PrimitiveTest dbo = PrimitiveTestDao.find(trans, obj);
        assertNotNull(dbo);
        ct = PrimitiveTestDao.delete(trans, obj);
        assertEquals(1, ct);
        dbo = PrimitiveTestDao.find(trans, obj);
        assertNull(dbo);
    }

    public void testDeleteNothing() throws Exception {
        PrimitiveTest obj = Mock.newPrimitiveTest(123);
        int ct = PrimitiveTestDao.delete(trans, obj);
        assertEquals(0, ct);
    }

    public void testListByQueryForAll() throws Exception {
        insertSampleDataForAll();
        TypeTestQuery ptq = new TypeTestQuery().forAll();
        List<PrimitiveTest> objs = PrimitiveTestDao.listByQuery(trans, ptq);
        assertEquals(3, objs.size());
    }

    public void testListByQueryForSome() throws Exception {
        insertSampleDataForSome();
        TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
        List<PrimitiveTest> objs = PrimitiveTestDao.listByQuery(trans, ptq);
        assertEquals(2, objs.size());
    }

    public void testDeleteByQueryForAll() throws Exception {
        insertSampleDataForAll();
        TypeTestQuery ptq = new TypeTestQuery().forAll();
        int ct = PrimitiveTestDao.deleteByQuery(trans, ptq);
        assertEquals(3, ct);
    }

    public void testDeleteByQueryForSome() throws Exception {
        insertSampleDataForSome();
        TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
        int ct = PrimitiveTestDao.deleteByQuery(trans, ptq);
        assertEquals(2, ct);
    }

    public void testCountByQueryForAll() throws Exception {
        insertSampleDataForAll();
        TypeTestQuery ptq = new TypeTestQuery().forAll();
        int ct = PrimitiveTestDao.countByQuery(trans, ptq);
        assertEquals(3, ct);
    }

    public void testCountByQueryForSome() throws Exception {
        insertSampleDataForSome();
        TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
        int ct = PrimitiveTestDao.countByQuery(trans, ptq);
        assertEquals(2, ct);
    }

    public void testProcessByQueryForAll() throws Exception {
        PrimitiveTest obj1 = Mock.newPrimitiveTest(1);
        PrimitiveTest obj2 = Mock.newPrimitiveTest(2);
        PrimitiveTest obj3 = Mock.newPrimitiveTest(3);
        assertEquals(1, PrimitiveTestDao.insert(trans, obj1));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj2));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj3));
        TypeTestQuery ptq = new TypeTestQuery().forAll();
        PrimitiveTestProcessor proc = new PrimitiveTestProcessor();
        int ct = PrimitiveTestDao.processByQuery(trans, ptq, proc);
        assertEquals(3, ct);
        assertEquals(obj1.getTestDouble() + obj2.getTestDouble() + obj3.getTestDouble(), proc
                .getSum(), 0);
    }

    public void testProcessByQueryForSome() throws Exception {
        PrimitiveTest obj1 = Mock.newPrimitiveTest(1);
        PrimitiveTest obj2 = Mock.newPrimitiveTest(2);
        PrimitiveTest obj11 = Mock.newPrimitiveTest(11);
        PrimitiveTest obj22 = Mock.newPrimitiveTest(22);
        assertEquals(1, PrimitiveTestDao.insert(trans, obj1));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj2));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj11));
        assertEquals(1, PrimitiveTestDao.insert(trans, obj22));
        TypeTestQuery ptq = new TypeTestQuery().forIdGreaterThan(10);
        PrimitiveTestProcessor proc = new PrimitiveTestProcessor();
        int ct = PrimitiveTestDao.processByQuery(trans, ptq, proc);
        assertEquals(2, ct);
        assertEquals(obj11.getTestDouble() + obj22.getTestDouble(), proc.getSum(), 0);
    }

    private class PrimitiveTestProcessor implements Processor<PrimitiveTest> {
        private double sum = 0.0;

        public int process(PrimitiveTest obj) {
            PrimitiveTest bean = obj;
            if (bean != null) {
                sum += bean.getTestDouble();
            }
            return 1;
        }

        public double getSum() {
            return sum;
        }
    }

}
