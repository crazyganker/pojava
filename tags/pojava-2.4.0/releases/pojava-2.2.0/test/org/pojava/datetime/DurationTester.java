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
}
