package org.pojava.datetime;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class TmTester extends TestCase {

    private static final boolean DEBUG = false;

    /**
     * Test that Tm returns same values produced by Calendar.
     */
    public void compareCalcs(long pointInTime) {
        // Use calendar to gather control values
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(pointInTime);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int millisecond = cal.get(Calendar.MILLISECOND);
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        // Verify that our values match.
        Tm tm = new Tm(pointInTime);
        assertEquals(year, tm.getYear());
        assertEquals(month, tm.getMonth() - 1);
        assertEquals(day, tm.getDay());
        assertEquals(hour, tm.getHour());
        assertEquals(minute, tm.getMinute());
        assertEquals(second, tm.getSecond());
        assertEquals(millisecond, tm.getMillisecond());
        assertEquals(millisecond * 1000000, tm.getNanosecond());
        assertEquals(dow, tm.getWeekday());
    }

    public void testCalcs() {
        compareCalcs(-12345);
        compareCalcs(0);
        compareCalcs(123456789);
    }

    public void testSpeed() {
        if (DEBUG) {
            long timer = System.currentTimeMillis();
            int iterations = 100000;
            for (int i = 0; i < iterations; i++) {
                Tm tm = new Tm(1234567890 + i * 100000000);
                int year = tm.getYear();
                int month = tm.getMonth();
                int day = tm.getDay();
                int hour = tm.getHour();
                int minute = tm.getMinute();
                int second = tm.getSecond();
                int millisecond = tm.getMillisecond();
                int nanosecond = tm.getNanosecond();
                int dow = tm.getWeekday();
            }
            long time1 = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                // The Calendar object is faster when reused, but
                // we're trying to simulate typical one-off usage.
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(1234567890 + i * 100000000);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);
                int millisecond = cal.get(Calendar.MILLISECOND);
                // We can only approximate this unsupported value
                int nanosecond = cal.get(Calendar.MILLISECOND) * 1000;
                int dow = cal.get(Calendar.DAY_OF_WEEK);
            }
            long time2 = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            sb.append("Speed test: Pojava=");
            sb.append(time1 - timer);
            sb.append("ms, Calendar=");
            sb.append(time2 - time1);
            sb.append("ms.");
            System.out.println(sb.toString());
        }
    }

    /**
     * This should cover a broad spectrum of potential issues.
     */
    public void testFourYearsDaily() {
        DateTime dt = new DateTime("2008-01-01");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dt.toMillis());
        for (int i = 0; i < 365 * 4 + 1; i++) {
            Tm tm = new Tm(cal.getTimeInMillis());
            assertEquals(cal.get(Calendar.DATE), tm.getDay());
            assertEquals(1 + cal.get(Calendar.MONTH), tm.getMonth());
            assertEquals(cal.get(Calendar.YEAR), tm.getYear());
            assertEquals(cal.get(Calendar.HOUR), tm.getHour());
            assertEquals(cal.get(Calendar.MINUTE), tm.getMinute());
            assertEquals(cal.get(Calendar.SECOND), tm.getSecond());
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.SECOND, 1);
        }
    }

    public void testOldLeapDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1204272059000L); // Feb 29
        System.out.println(new Date(cal.getTimeInMillis()));
        Tm tm = new Tm(cal.getTimeInMillis());
        assertEquals(cal.get(Calendar.DATE), tm.getDay());
        assertEquals(1 + cal.get(Calendar.MONTH), tm.getMonth());
        assertEquals(cal.get(Calendar.YEAR), tm.getYear());
        assertEquals(cal.get(Calendar.HOUR), tm.getHour());
        assertEquals(cal.get(Calendar.MINUTE), tm.getMinute());
        assertEquals(cal.get(Calendar.SECOND), tm.getSecond());

        cal.setTimeInMillis(1204358460000L); // Mar 1
        // System.out.println(cal.getTime().toString());
        tm = new Tm(cal.getTimeInMillis());
        assertEquals(cal.get(Calendar.DATE), tm.getDay());
        assertEquals(1 + cal.get(Calendar.MONTH), tm.getMonth());
        assertEquals(cal.get(Calendar.YEAR), tm.getYear());
        assertEquals(cal.get(Calendar.HOUR), tm.getHour());
        assertEquals(cal.get(Calendar.MINUTE), tm.getMinute());
        assertEquals(cal.get(Calendar.SECOND), tm.getSecond());

    }

    public void testCalcTime() {
        int yr = 0;
        StringBuffer sb = new StringBuffer();
        for (yr = 1000; yr <= 2110; yr++) {
            sb.setLength(0);
            sb.append(yr);
            sb.append("-01-01");
            long dtMillis = new DateTime(sb.toString()).toMillis();
            long tmMillis = Tm.calcTime(yr, 1, 1);
            if (dtMillis != tmMillis) {
                System.out.println(new Date(dtMillis).toString());
                System.out.println(new Date(tmMillis).toString());
            }
            assertEquals(dtMillis, tmMillis);
        }
    }

}
