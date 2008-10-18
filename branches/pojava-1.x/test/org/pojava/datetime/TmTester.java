package org.pojava.datetime;

import java.util.Calendar;

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

}
