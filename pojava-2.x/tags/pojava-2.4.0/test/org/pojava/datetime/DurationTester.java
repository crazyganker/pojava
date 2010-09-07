package org.pojava.datetime;

import junit.framework.TestCase;

public class DurationTester extends TestCase {

    public void testCompareTo() {
        Duration d1 = new Duration(-123);
        Duration d2 = new Duration(456);
        Duration d3 = new Duration(456);
        assertTrue(d1.compareTo(d2) < 0);
        assertTrue(d2.compareTo(d1) > 0);
        assertTrue(d2.compareTo(d3) == 0);
        d3 = d3.add(0, 123);
        assertEquals(d2.toMillis(), d3.toMillis());
        assertTrue(d2.compareTo(d3) < 0);
    }

    public void testCompareToInvalid() {
        Duration d1 = new Duration(-123);
        try {
            d1.compareTo(null);
        } catch (NullPointerException ex) {
            assertEquals("Cannot compare Duration to null.", ex.getMessage());
        }
    }

    public void testAdd() {
        Duration d1 = new Duration(1);
        Duration d2 = new Duration(2);
        assertEquals(3, d1.add(d2).toMillis());
    }

    public void testDefaultConstructor() {
        Duration d1 = new Duration(0);
        Duration d2 = new Duration();
        assertTrue(d1.equals(d2));
    }
    
    public void testParseCramped() {
        Duration d1 = new Duration("5h6m7s");
        Duration d2 = new Duration().add(Duration.HOUR*5+Duration.MINUTE*6+Duration.SECOND*7);
        assertTrue(d1.equals(d2));
    }

    public void testParseVerbose() {
        Duration d1 = new Duration("1 week, 12 hours, 15 minutes, and 16.4459993 seconds");
        Duration d2 = new Duration().add((Duration.WEEK+12*Duration.HOUR +15*Duration.MINUTE+16*Duration.SECOND)/1000,445999300);
        assertTrue(d1.equals(d2));
    }
    
    public void testParseNegative() {
        Duration d1 = new Duration("1 day - 2 hour2");
        Duration d2 = new Duration("21 hours + 1 hour");
        assertTrue(d1.equals(d2));
        assertEquals(1000*60*60*22, d1.millis);
    }
    
    public void testParseNanos() {
        Duration d1 = new Duration("1234567891011 nanoseconds");
        Duration d2 = new Duration().add(1234,567891011);
        assertTrue(d1.equals(d2));
    }

    public void testToString() {
        Duration d1 = new Duration("1 week, 12 hours, 15 minutes, and 16.4459993 seconds");
        Duration d2 = new Duration("1 day, 5 seconds");
        assertEquals("7d12h15m16s445999300n", d1.toString());
        assertEquals("1d5s", d2.toString());
    }
}
