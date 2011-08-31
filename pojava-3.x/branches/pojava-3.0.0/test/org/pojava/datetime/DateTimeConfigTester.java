package org.pojava.datetime;

import java.util.TimeZone;

import junit.framework.TestCase;

public class DateTimeConfigTester extends TestCase {
	
	private DateTimeConfig config=DateTimeConfig.getGlobalDefault();
	
	protected void setUp() throws Exception {
        super.setUp();
        TimeZone tz=TimeZone.getTimeZone("America/New_York");
        TimeZone.setDefault(tz);
        config.globalAmericanDateFormat();
        config.setInputTimeZone(tz);
        config.setOutputTimeZone(tz);
	}

	public void testCustomSettings() throws Exception {
		DateTimeConfig dtc=config.clone();
		dtc.setDefaultDateFormat("yyyy-MM-dd HH:mm:ss z Z");
		dtc.setDmyOrder(true);
		dtc.setInputTimeZone(TimeZone.getTimeZone("America/Phoenix"));
		dtc.setOutputTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		
		// Input time zone determined by global config
		DateTime dtGlobal=new DateTime("2011-08-28 06:00");
		// Input time zone determined by time zone specified in string
		DateTime dtPhoenix=new DateTime("2011-08-28 06:00 America/Phoenix");
		// Input time zone determined by global config
		DateTime dtConfig=new DateTime("2011-08-28 06:00", dtc);
		
		// Output determined by global config
		assertEquals("2011-08-28 06:00:00 EDT", dtGlobal.toString());
		// Output determined by global config
		assertEquals("2011-08-28 09:00:00 EDT", dtPhoenix.toString());
		// Output determined by local dtc config
		assertEquals("2011-08-28 06:00:00 PDT -0700", dtConfig.toString());
	}

	
}
