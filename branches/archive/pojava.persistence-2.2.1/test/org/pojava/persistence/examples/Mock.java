package org.pojava.persistence.examples;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import org.pojava.datetime.DateTime;

public class Mock {

    public static TypeTest newTypeTest(int seed) {
        TypeTest obj = new TypeTest();
        String str = new Integer(seed).toString();
        obj.setTestBigint(new Long(seed * Integer.MAX_VALUE));
        obj.setTestBoolean(new Boolean(seed % 2 == 0));
        obj.setTestCharacter1(new Character((char) ('A' + seed % 26)));
        obj.setTestCharacter5(str.substring(0, Math.min(5, str.length())));
        obj.setTestDate(new DateTime(seed * 7777777));
        obj.setTestDouble(new Double((1.0 * seed % 13) / 7));
        obj.setTestId(new Integer(seed));
        BigDecimal bigD = new BigDecimal(new Double(obj.getTestDouble().doubleValue())
                .toString().substring(0, 6));
        obj.setTestNumeric_10x4(bigD);

        obj.setTestReal(new Float(seed + 0.111));
        obj.setTestSmallint(new Integer(seed));
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
        obj.setTestVarchar1(new Character(obj.getTestCharacter1()).toString());
        obj.setTestVarchar5(obj.getTestCharacter5());
        return obj;
    }
}
