package org.pojava.datetime;

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

	public void testChart() {
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
		System.out.println(dt.toString());
		assertEquals(millis, dt.getMillis());

		// A year after 9999
		millis = 263402260044000L;
		d = new Date(millis);
		dt = new DateTime(d.toString());
		assertEquals(millis, dt.getMillis());

		// A year before 0001
		millis = -63135700000000L;
		System.out.println(new Date(millis).toString());
		System.out.println(new DateTime(millis).toString());

		
	}

}
