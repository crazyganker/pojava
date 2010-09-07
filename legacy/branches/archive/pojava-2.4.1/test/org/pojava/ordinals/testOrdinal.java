package org.pojava.ordinals;

import junit.framework.TestCase;

import org.pojava.datetime.CalendarUnit;

public class testOrdinal extends TestCase {

    public void testOrdinalComparisons() {
        assertTrue(CalendarUnit.DAY.compareTo(CalendarUnit.YEAR) < 0);
        assertTrue(CalendarUnit.CENTURY.compareTo(CalendarUnit.NANOSECOND) > 0);
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
            assertEquals("No enum const class org.pojava.datetime.CalendarUnit.NOTHING", ex
                    .getMessage());
        }
    }

}
