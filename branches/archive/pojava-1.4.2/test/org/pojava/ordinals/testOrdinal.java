package org.pojava.ordinals;

import junit.framework.TestCase;

import org.pojava.datetime.CalendarUnit;

public class testOrdinal extends TestCase {

    public void testOrdinalComparisons() {
        assertTrue(CalendarUnit.DAY.isLessThan(CalendarUnit.YEAR));
        assertTrue(CalendarUnit.CENTURY.isGreaterThan(CalendarUnit.NANOSECOND));
        assertTrue(CalendarUnit.WEEK.equals(CalendarUnit.valueOf("WEEK")));
        assertFalse(CalendarUnit.WEEK.equals("WEEK"));
        assertFalse(CalendarUnit.WEEK.equals(CalendarUnit.MONTH));
    }

    public void testOrdinalParsing() {
        assertEquals(CalendarUnit.MICROSECOND, CalendarUnit.valueOf("MICROSECOND"));
        try {
            assertEquals(CalendarUnit.CENTURY, CalendarUnit.valueOf("NOTHING"));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("No ordinal class org.pojava.datetime.CalendarUnit.NOTHING", ex
                    .getMessage());
        }
    }

}
