package org.pojava.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

public class DateTimeFormatTester extends TestCase {
	
	TimeZone origTimeZone;
	
	public void setUp() {
		origTimeZone=TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
		DateTimeConfig config=DateTimeConfig.getGlobalDefault();
		config.setInputTimeZone(TimeZone.getDefault());
		config.setOutputTimeZone(TimeZone.getDefault());
	}
	
	public void tearDown() {
		TimeZone.setDefault(origTimeZone);
	}
	
    public void testCommonFormats() {
        DateTime dt = new DateTime("1/23/45 6:7:8.9101112");
        compareStatic("y/M/d", dt);
        compareStatic("yyyyy/MMMMM/ddddd", dt);
        compareStatic("MM/dd/yyyy h:m:s.SSS", dt);
        compareStatic("h 'o''clock' a", dt);
        compareStatic("'o''xx' ''''", dt);
    }

    public void testUniqueFormats() {
        DateTime dt = new DateTime("1/23/2045 6:7:8.9101112");
        String fmt = "yyyy-MM-dd hh:mm:ss.SSSSSSSSS";
        assertEquals("2045-01-23 06:07:08.910111200", DateTimeFormat.format(fmt, dt));
    }
    
    public void testTimeZone() {
    	
        DateTime dt = new DateTime("2008-01-09 GMT-04:00");
        assertEquals("-0400", DateTimeFormat.format("Z", dt, TimeZone.getTimeZone(dt.getTimeZoneID())));
        assertEquals("-04:00", DateTimeFormat.format("ZZ", dt, TimeZone.getTimeZone(dt.getTimeZoneID())));
    }
    
    public void testLocale() {
    	TimeZone.setDefault(TimeZone.getTimeZone("PST"));
    	DateTimeConfig.getGlobalDefault().setOutputTimeZone(TimeZone.getDefault());
        DateTime dt = new DateTime("2008-01-09 PST");
        compareStatic("zzzz", dt, Locale.FRENCH);
    }

    private void compareStatic(String fmt, DateTime dt) {
        Date date = dt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        assertEquals(sdf.format(date), DateTimeFormat.format(fmt, dt));
    }

    private void compareStatic(String fmt, DateTime dt, Locale loc) {
        Date date = dt.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt, loc);
        assertEquals(sdf.format(date), DateTimeFormat.format(fmt, dt, TimeZone.getDefault(), loc));
    }

}
