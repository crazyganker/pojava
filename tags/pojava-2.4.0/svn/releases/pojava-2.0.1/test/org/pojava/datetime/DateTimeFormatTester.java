package org.pojava.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class DateTimeFormatTester extends TestCase {

    public void testCommonFormats() {
        DateTime dt = new DateTime("1/23/45 6:7:8.9101112");
        compareStatic("y/M/d", dt);
        compareStatic("yyyyy/MMMMM/ddddd", dt);
        compareStatic("MM/dd/yyyy h:m:s.SSS", dt);
    }

    public void testUniqueFormats() {
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112");
        String fmt = "yyyy-MM-dd hh:mm:ss.SSSSSSSSS";
        assertEquals("2045-01-23 06:07:08.910111200", DateTimeFormat.format(fmt, dt));
    }

    private void compareStatic(String fmt, DateTime dt) {
        Date date = dt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        assertEquals(sdf.format(date), DateTimeFormat.format(fmt, dt));
    }

}
