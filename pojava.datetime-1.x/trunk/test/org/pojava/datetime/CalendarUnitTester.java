package org.pojava.datetime;

import junit.framework.TestCase;

public class CalendarUnitTester extends TestCase {

	public void testOrdering() {
		assertEquals(CalendarUnit.WEEK, CalendarUnit.valueOf("WEEK"));
	}

}
