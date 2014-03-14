package org.pojava.persistence.examples;

import org.pojava.datetime.DateTime;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class Mock {

    public static TypeTest newTypeTest(int seed) {
        TypeTest obj = new TypeTest();
        String str = Integer.toString(seed);
        obj.setTestBigint((long) (seed * Integer.MAX_VALUE));
        obj.setTestBoolean(seed % 2 == 0);
        obj.setTestCharacter1((char) ('A' + seed % 26));
        obj.setTestCharacter5(str.substring(0, Math.min(5, str.length())));
        obj.setTestDate(new DateTime(seed * 7777777));
        obj.setTestDouble((1.0 * seed % 13) / 7);
        obj.setTestId(seed);
        BigDecimal bigD = new BigDecimal(Double.toString(obj.getTestDouble()).substring(0, 6));
        obj.setTestNumeric_10x4(bigD);

        obj.setTestReal(new Float(seed + 0.111));
        obj.setTestSmallint(seed);
        obj.setTestTimestampWithoutTz(obj.getTestDate());
        obj.setTestTimestampWithTz(obj.getTestDate());
        obj.setTestTimeWithTz(new Time(seed));
        obj.setTestVarchar1(obj.getTestCharacter1().toString());
        obj.setTestVarchar5(obj.getTestCharacter5());
        return obj;
    }

    public static PrimitiveTest newPrimitiveTest(int seed) {
        PrimitiveTest obj = new PrimitiveTest();
        String str = new Integer(seed).toString();
        obj.setTestBigint(seed * Integer.MAX_VALUE);
        obj.setTestBoolean(seed % 2 == 0);
        obj.setTestCharacter1((char) ('A' + seed % 26));
        obj.setTestCharacter5(str.substring(0, Math.min(5, str.length())));
        obj.setTestDate(new Date(seed * 7777777));
        obj.setTestDouble((1.0 * seed) / 7);
        obj.setTestId(seed);
        obj.setTestNumeric_10x4(obj.getTestDouble());
        obj.setTestReal((float) (seed + 0.111));
        obj.setTestSmallint((byte) (seed % 256));
        obj.setTestTimestampWithoutTz(new DateTime(seed * 7777777));
        obj.setTestTimestampWithTz(new DateTime(seed * 7777777));
        obj.setTestTimeWithTz(new Time(seed));
        obj.setTestVarchar1(Character.toString(obj.getTestCharacter1()));
        obj.setTestVarchar5(obj.getTestCharacter5());
        return obj;
    }
}
