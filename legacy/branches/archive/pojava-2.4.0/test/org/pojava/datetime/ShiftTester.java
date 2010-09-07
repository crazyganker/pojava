package org.pojava.datetime;

import junit.framework.TestCase;

public class ShiftTester extends TestCase {

    public void testToString24hr() {
        Shift shift=new Shift();
        shift.setDay(5);
        shift.setHour(24);
        shift.setMinute(5);
        // Expect 24h to be preserved
        assertEquals("P5DT24H5M", shift.toString());
    }
    
    public void testToStringOverflows() {
        Shift shift=new Shift();
        shift.setHour(24);
        shift.setMinute(60);
        shift.setSecond(60);
        shift.setNanosec(1000000000);
        assertEquals("PT25H1M1S", shift.toString());
    }

    public void testToStringNanos() {
        Shift shift=new Shift();
        shift.setNanosec(1234567);
        assertEquals("PT0.001234567S", shift.toString());
    }
    
    public void testToStringSecNanos() {
        Shift shift=new Shift();
        shift.setSecond(12);
        shift.setNanosec(1234567);
        assertEquals("PT12.001234567S", shift.toString());
    }

    public void testConstructor() {
        String[] samples={"PT-1.2345S","P1M2DT3M4S","PT12.001234567S","P1DT24H13S","P130Y7M6W2DT5H6M7.3S",
                "PT1.1S","PT1.999999999S"}; 
        for (int i = 0; i < samples.length; i++) {
            assertEquals(samples[i], new Shift(samples[i]).toString());
        }
    }

}
