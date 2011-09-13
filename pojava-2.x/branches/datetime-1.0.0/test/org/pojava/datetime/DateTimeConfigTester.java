package org.pojava.datetime;

import java.util.TimeZone;

import junit.framework.TestCase;

public class DateTimeConfigTester extends TestCase {

    DateTimeConfig config=DateTimeConfig.getGlobalDefault().clone();
    TimeZone localTz=TimeZone.getDefault();

    protected void setUp() throws Exception {
        super.setUp();
        config.setDmyOrder(false);
        // UTC + 5:30 Always
        config.setInputTimeZone(TimeZone.getTimeZone("IST"));
        // UTC - 4:00 EST (1st Sun in Nov to 2nd Sun in Mar)
        // UTC - 5:00 EDT (2nd Sun in Mar to 1st Sun in Nov)
        config.setOutputTimeZone(TimeZone.getTimeZone("America/New_York"));
        config.setFormat("yyyy-MM-dd HH:mm:ss z");
        // Time Zone of the JVM.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TimeZone.setDefault(localTz);
    }

    /**
     * Test equivalence with UTC.
     */
    public void testUTCeqIST() {
    	DateTime utc=new DateTime("2011-08-31 UTC", config);
    	// InputTimeZone defines zone where local time was captured.
    	DateTime ist=new DateTime("2011-08-31 05:30:00", config);
        // OutputTimeZone determines zone for which local time is displayed.
    	DateTime utc2=new DateTime("2011-08-31 UTC");
    	assertEquals(utc2.getSeconds(), utc.getSeconds());
    	assertEquals(utc.getSeconds(), ist.getSeconds());
    }

    /**
     * Test date during Daylight Saving with multiple time zones involved.
     */
    public void testZoneDifference() {
    	// InputTimeZone defines zone where local time was captured.
    	DateTime dtc=new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
    	assertEquals("2011-08-30 19:01:32 EDT", dtc.toString());
    }

    /**
     * Test override of Time Zone on output.
     */
    public void testZoneOverride() {
    	// InputTimeZone defines zone where local time was captured.
    	DateTime dtc=new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
    	assertEquals("2011-08-30 16:01:32 PDT", dtc.toString(config.getFormat(), TimeZone.getTimeZone("PST")));
    }

    /**
     * Override the default format
     */
    public void testFormatOverride() {
    	// InputTimeZone defines zone where local time was captured.
    	DateTime dtc=new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
    	assertEquals("2011-08-30 07:01:32 PM EDT", dtc.toString("yyyy-MM-dd hh:mm:ss a z"));
    }

    /**
     * Override both the time zone and the format.
     */
    public void testZoneAndFormatOverride() {
    	// InputTimeZone defines zone where local time was captured.
    	DateTime dtc=new DateTime("2011-08-31 04:31:32", config);
        // OutputTimeZone determines zone for which local time is displayed.
    	assertEquals("[2011-08-30 4:01:32 PM]", dtc.toString("[yyyy-MM-dd h:mm:ss a]", TimeZone.getTimeZone("PST")));
    }
}
