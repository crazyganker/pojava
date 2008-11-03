package org.pojava.datetime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

public class DateTimeTester extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test for the parsed ".987" in both millis and nanos.
	 */
	public void testDateToMillis() {
		DateTime dt = DateTime.parse("2008-05-16 01:23:45.987 PST");
		assertEquals(987, dt.getMillis() % 1000);
		assertEquals(987000000, dt.getNanos());
	}

	/**
	 * Truncate to any precision defined in CalendarUnit
	 */
	public void testTruncate() {
		DateTime dt = DateTime.parse("2008-08-08 03:02:01.123456789");
		String format = "yyyy-MM-dd HH:mm:ss.SSS";
		assertEquals("2008-08-08 03:02:01.123", dt.truncate(
				CalendarUnit.NANOSECOND).toString(format));
		assertEquals("2008-08-08 03:02:01.123", dt.truncate(
				CalendarUnit.MICROSECOND).toString(format));
		assertEquals("2008-08-08 03:02:01.123", dt.truncate(
				CalendarUnit.MILLISECOND).toString(format));
		assertEquals("2008-08-08 03:02:01.000", dt
				.truncate(CalendarUnit.SECOND).toString(format));
		assertEquals("2008-08-08 03:02:00.000", dt
				.truncate(CalendarUnit.MINUTE).toString(format));
		assertEquals("2008-08-08 03:00:00.000", dt.truncate(CalendarUnit.HOUR)
				.toString(format));
		assertEquals("2008-08-08 00:00:00.000", dt.truncate(CalendarUnit.DAY)
				.toString(format));
		assertEquals("2008-08-03 00:00:00.000", dt.truncate(CalendarUnit.WEEK)
				.toString(format));
		assertEquals("2008-08-01 00:00:00.000", dt.truncate(CalendarUnit.MONTH)
				.toString(format));
		assertEquals("2008-07-01 00:00:00.000", dt.truncate(
				CalendarUnit.QUARTER).toString(format));
		assertEquals("2008-01-01 00:00:00.000", dt.truncate(CalendarUnit.YEAR)
				.toString(format));
		assertEquals("2000-01-01 00:00:00.000", dt.truncate(
				CalendarUnit.CENTURY).toString(format));
		dt=new DateTime(-22222222);  // Eight twos, Brutus? 1969-12-31 09:49:37.778
		assertEquals("1969-12-31 09:49:37.778", dt.truncate(
				CalendarUnit.NANOSECOND).toString(format));
		assertEquals("1969-12-31 09:49:37.778", dt.truncate(
				CalendarUnit.MICROSECOND).toString(format));
		assertEquals("1969-12-31 09:49:37.778", dt.truncate(
				CalendarUnit.MILLISECOND).toString(format));
		assertEquals("1969-12-31 09:49:37.000", dt
				.truncate(CalendarUnit.SECOND).toString(format));
		assertEquals("1969-12-31 09:49:00.000", dt
				.truncate(CalendarUnit.MINUTE).toString(format));
		assertEquals("1969-12-31 09:00:00.000", dt.truncate(CalendarUnit.HOUR)
				.toString(format));
		assertEquals("1969-12-31 00:00:00.000", dt.truncate(
				CalendarUnit.DAY).toString(format));
		assertEquals("1969-12-28 00:00:00.000", dt.truncate(CalendarUnit.WEEK)
				.toString(format));
		assertEquals("1969-12-01 00:00:00.000", dt.truncate(CalendarUnit.MONTH)
				.toString(format));
		assertEquals("1969-10-01 00:00:00.000", dt.truncate(
				CalendarUnit.QUARTER).toString(format));
		assertEquals("1969-01-01 00:00:00.000", dt.truncate(CalendarUnit.YEAR)
				.toString(format));
		assertEquals("1900-01-01 00:00:00.000", dt.truncate(
				CalendarUnit.CENTURY).toString(format));
	}

	public void testToString() {
		String format = "yyyy-MM-dd HH:mm:ss.SSS";
		DateTime dt;
		// Date before epoch
		dt = DateTime.parse("1968-12-31 23:59:59.123456789");
		assertEquals("1968-12-31 23:59:59.123", dt.toString(format));
		dt = DateTime.parse("1945-03-09 23:42:59.123456789");
		assertEquals("1945-03-09 23:42:59.123", dt.toString(format));
	}

	public void spit(String label, long value) {
		System.out.print(label + "=");
		System.out.println(value);
	}

	public void testTm() {
		Tm tm = new Tm(new DateTime("1965-06-30 03:04:05.6789"));
		assertEquals(1965, tm.getYear());
		assertEquals(6, tm.getMonth());
		assertEquals(30, tm.getDay());
		assertEquals(3, tm.getHour());
		assertEquals(4, tm.getMinute());
		assertEquals(5, tm.getSecond());
		assertEquals(678900000, tm.getNanosecond());

		tm = new Tm(new DateTime("2008-02-29 03:04:05.6789"));
		assertEquals(2008, tm.getYear());
		assertEquals(2, tm.getMonth());
		assertEquals(29, tm.getDay());
		assertEquals(3, tm.getHour());
		assertEquals(4, tm.getMinute());
		assertEquals(5, tm.getSecond());
		assertEquals(678900000, tm.getNanosecond());
	}

	/**
	 * Adding Calendar Units shifts a date part, leaving others the same.
	 * Notably, it should adjust to compensate for Daylight Saving Time.
	 */
	public void testAddCalendarUnits() {
		String format = "yyyy-MM-dd HH:mm:ss.SSS";
		DateTime dt = DateTime.parse("1955-06-14 02:03:04.123456789");
		DateTime dtNew;
		dtNew = dt.add(CalendarUnit.NANOSECOND, 1);
		assertEquals(1, dtNew.getNanos() - dt.getNanos());
		dtNew = dt.add(CalendarUnit.MICROSECOND, -2);
		assertEquals(-2000, dtNew.getNanos() - dt.getNanos());
		dtNew = dt.add(CalendarUnit.MILLISECOND, 3);
		assertEquals(3000000, dtNew.getNanos() - dt.getNanos());
		dtNew = dt.add(CalendarUnit.SECOND, -4);
		assertEquals(-4 * Duration.SECOND, dtNew.getMillis() - dt.getMillis());
		dtNew = dt.add(CalendarUnit.MINUTE, 5);
		assertEquals(5 * Duration.MINUTE, dtNew.getMillis() - dt.getMillis());
		dtNew = dt.add(CalendarUnit.HOUR, -6);
		assertEquals(-6 * Duration.HOUR, dtNew.getMillis() - dt.getMillis());
		dtNew = dt.add(CalendarUnit.DAY, 7);
		assertEquals(7 * Duration.DAY, dtNew.getMillis() - dt.getMillis());
		dtNew = dt.add(CalendarUnit.WEEK, -8);
		assertEquals(-8 * Duration.WEEK, dtNew.getMillis() - dt.getMillis()
				- (TimeZone.getDefault().getDSTSavings()));
		dtNew = dt.add(CalendarUnit.MONTH, 9);
		assertEquals("1956-03-14 02:03:04.123", dtNew.toString(format));
		dtNew = dt.add(CalendarUnit.YEAR, -10);
		assertEquals("1945-06-14 02:03:04.123", dtNew.toString(format));
		dtNew = dt.add(CalendarUnit.CENTURY, 11);
		assertEquals("3055-06-14 02:03:04.123", dtNew.toString(format));
	}

	public void XtestChart() {
		// Known leap seconds
		System.out.println(DateTime.parse("1972-07-01").getMillis());
		System.out.println(DateTime.parse("1973-01-01").getMillis());
		System.out.println(DateTime.parse("1974-01-01").getMillis());
		System.out.println(DateTime.parse("1975-01-01").getMillis());
		System.out.println(DateTime.parse("1976-01-01").getMillis());
		System.out.println(DateTime.parse("1977-01-01").getMillis());
		System.out.println(DateTime.parse("1978-01-01").getMillis());
		System.out.println(DateTime.parse("1979-01-01").getMillis());
		System.out.println(DateTime.parse("1980-01-01").getMillis());
		System.out.println(DateTime.parse("1981-07-01").getMillis());
		System.out.println(DateTime.parse("1982-07-01").getMillis());
		System.out.println(DateTime.parse("1983-07-01").getMillis());
		System.out.println(DateTime.parse("1985-07-01").getMillis());
		System.out.println(DateTime.parse("1988-01-01").getMillis());
		System.out.println(DateTime.parse("1990-01-01").getMillis());
		System.out.println(DateTime.parse("1991-01-01").getMillis());
		System.out.println(DateTime.parse("1992-07-01").getMillis());
		System.out.println(DateTime.parse("1993-07-01").getMillis());
		System.out.println(DateTime.parse("1994-07-01").getMillis());
		System.out.println(DateTime.parse("1996-01-01").getMillis());
		System.out.println(DateTime.parse("1997-07-01").getMillis());
		System.out.println(DateTime.parse("1999-01-01").getMillis());
		System.out.println(DateTime.parse("2006-01-01").getMillis());

		// Supported time zones
		String ids[] = TimeZone.getAvailableIDs();
		for (int i = 0; i < ids.length; i++) {
			System.out.println(ids[i]);
		}
	}

	public void testLanguages() {
		// English
		assertEquals("1969-01-26 00:00:00", new DateTime("1969,1,26")
				.toString());
		assertEquals("1945-03-09 00:00:00", new DateTime("March 9, 1945")
				.toString());
		assertEquals("1996-02-03 00:00:00", new DateTime("03-feb-1996")
				.toString());
		assertEquals("1776-07-04 00:00:00", new DateTime(
				"This 4th day of July, 1776").toString());
		// French
		assertEquals("1789-06-20 00:00:00", new DateTime("20 juin, 1789")
				.toString());
		assertEquals("1789-07-09 00:00:00", new DateTime("9 juillet, 1789")
				.toString());
		// German
		assertEquals("1871-01-18 00:00:00", new DateTime("18, Januar 1871")
				.toString());
		assertEquals("2008-12-25 00:00:00", new DateTime("25-Dez-2008")
				.toString());
		// Spanish
		assertEquals("1821-08-24 00:00:00", new DateTime("24 agosto, 1821")
				.toString());
		assertEquals("2000-02-29 00:00:00", new DateTime(
				"el 29 de febrero de 2000").toString());
	}

	/**
	 * These tests show the consistency of the interpretation of millisecond
	 * values between the native Date object and the DateTime object.
	 * 
	 * Dates represented in BCE (before 0001) by Date.toString() cannot be
	 * distinguished from similar dates in CE. Dates after 9999 are addressable,
	 * but not interpreted as a year by DateTime.
	 */
	public void testConsistency() {
		// The year 0001
		long millis = -62135700000000L;
		Date d = new Date(millis);
		DateTime dt = new DateTime(d.toString());
		assertEquals(millis, dt.getMillis());

		// The year 9999
		millis = 253402260044000L;
		d = new Date(millis);
		dt = new DateTime(d.toString());
		assertEquals(millis, dt.getMillis());

		// A year after 9999
		millis = 263402260044000L;
		d = new Date(millis);
		dt = new DateTime(d.toString());
		assertEquals(millis, dt.getMillis());

		// A millisecond prior to the year 0001
		millis = -62135740800001L;
		assertEquals(millis, new DateTime("0001-01-01").getMillis() - 1);
		assertEquals("0001-12-31 23:59:59 BC", new DateTime(millis).toString());

	}

	/**
	 * This tests behavior for leap year calculations.
	 */
	public void testLeapYears() {
		// 100 year leap year exception
		assertEquals("1900-02-28 23:59:59", new DateTime("1900-03-01").add(-1)
				.toString());
		// Regular year
		assertEquals("1902-02-28 23:59:59", new DateTime("1902-03-01").add(-1)
				.toString());
		// Regular leap year
		assertEquals("1904-02-29 23:59:59", new DateTime("1904-03-01").add(-1)
				.toString());
		// 1000 year leap year inclusion
		assertEquals("1000-02-29 23:59:59", new DateTime("1000-03-01").add(-1)
				.toString());
	}

	public void testEdgeCases() {
		// Add Month to January 30
		assertEquals("2008-02-29 00:00:00", new DateTime("2008-01-30").add(
				CalendarUnit.MONTH, 1).toString());
	}

	public void testDow() {
		long timer = System.currentTimeMillis();
		Tm tm = new Tm(timer);
		Calendar cal = Calendar.getInstance();
		int dow = cal.get(Calendar.DAY_OF_WEEK);
		assertEquals(dow, tm.getWeekday());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		assertEquals(Calendar.SUNDAY, new DateTime(cal.getTimeInMillis())
				.getWeekday());
		TimeZone tz = TimeZone.getTimeZone("PST");
		assertEquals(Calendar.WEDNESDAY, new DateTime(0, tz).getWeekday());
		assertEquals(Calendar.THURSDAY, new DateTime(-40 * Duration.WEEK
				+ Duration.DAY, tz).getWeekday());
	}

	public void testCompareTo() {
		DateTime dt1 = new DateTime(12345);
		DateTime dt2 = new DateTime(12346);
		assertTrue(dt1.compareTo(dt2) < 0);
		try {
			dt1.compareTo(new Integer(5));
		} catch (IllegalArgumentException ex) {
			assertEquals("Cannot compare DateTime to java.lang.Integer.", ex
					.getMessage());
		}
	}

	public void testRelativeDateMinus() {
		long start = System.currentTimeMillis();
		DateTime dt1 = new DateTime();
		DateTime dt2 = new DateTime("-1");
		long dur = System.currentTimeMillis() - start;
		long diff = dt2.add(Duration.DAY).getMillis() - dt1.getMillis();
		// The relative date "-1" represents 24hrs in past.
		// The values for dt1 and dt2 will be one day apart, plus some
		// small bit of extra time that elapsed between the two calculations.
		assertTrue(diff <= dur);
	}

	public void testRelativeDatePlusD() {
		long start = System.currentTimeMillis();
		DateTime dt1 = new DateTime();
		DateTime dt2 = new DateTime("+1D");
		long dur = System.currentTimeMillis() - start;
		long diff = dt2.add(-Duration.DAY).getMillis() - dt1.getMillis();
		// The relative date "-1" represents 24hrs in past.
		// The values for dt1 and dt2 will be one day apart, plus some
		// small bit of extra time that elapsed between the two calculations.
		assertTrue(diff <= dur);
	}
	
	public void testToLocalString() {
		DateTime dt=new DateTime("Jan 26, 1969");
		assertEquals("1969-01-26 00:00:00", dt.toLocalString());
	}
	
	/**
	 * It is always prudent to verify that your examples actually work :)
	 */
	public void testJavaDocClaims() {
		DateTime dt1=new DateTime("3:21pm on January 26, 1969");
		DateTime dt2=new DateTime("26-Jan-1969 03:21 PM");
		DateTime dt3=new DateTime("1/26/69 15:21");
		DateTime dt4=new DateTime("1969.01.26 15.21");
		DateTime dt5=new DateTime("el 26 de enero de 1969 15.21");
		assertEquals(dt1,dt2);
		assertEquals(dt1,dt3);
		assertEquals(dt1,dt4);
		assertEquals(dt1,dt5);		
	}
	
	/**
	 * This looks for a broad spectrum of issues, spanning different
	 * times of day, days of the month, leap and non-leap years.
	 */
	public void testFourYearsDaily() {
		DateTime dt=new DateTime("2008-01-01");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(dt.getMillis());
		for (int i=0; i<365*4; i++) {
			assertEquals(cal.getTimeInMillis(), dt.getMillis());
			cal.add(Calendar.DATE, 1);
			cal.add(Calendar.SECOND, 61);
			dt=dt.add(CalendarUnit.DAY, 1).add(CalendarUnit.SECOND,61);
		}
	}

}
