package org.pojava.datetime;

/*
 Copyright 2008-09 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Establish global defaults for shaping DateTime behavior. This version supports English,
 * German, French and Spanish month names in the date parser, and can be customized by your
 * applications to interpret other languages.
 * 
 * @author John Pile
 * 
 */
public class DateTimeConfig implements IDateTimeConfig, Serializable {

    /**
     * Compulsory serial ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Singleton pattern. The globalDefault variable is referenced by DateTime, so changes you
     * make here affect new calls to DateTime.
     */
    private static DateTimeConfig globalDefault = new DateTimeConfig();

    /**
     * This determines the default interpretation of a ##/##/#### date, whether Day precedes
     * Month or vice versa.
     */
    private boolean isDmyOrder = false;
    
    private TimeZone inputTimeZone = TimeZone.getDefault();

    private TimeZone outputTimeZone = TimeZone.getDefault();

	/**
     * The 1970-01-01 epoch started on a Thursday. If Sunday is the start of a week, then this
     * number is 4. If Monday is the start, then set to 3.
     */
    private int epochDOW = 4;

    /**
     * The default date format used for DateTime.toString();
     */
    private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss z";

    private String defaultJdbcFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * <p>
     * Support parsing of zones unlisted in TimeZone by translating to known zones. Got a zone
     * that's not supported or should be overridden? Fix it locally by updating your custom
     * tzMap!
     * </p>
     * 
     * <pre>
     * // Example change CST from U.S. Central to Chinese.
     * class CustomTzMap {
     * 	private static Map&lt;String, String&amp;gt tzMap = DateTimeConfig.getTzMap();
     * 	static {
     * 		tzMap.put(&quot;CST&quot;, &quot;Asia/Hong_Kong&quot;);
     * 	}
     * }
     * </pre>
     */
    private Map<String, String> tzMap = new HashMap<String, String>();
    static {
        globalDefault.tzMap.put("Z", "UTC");
        globalDefault.tzMap.put("PDT", "America/Los_Angeles");
    }

    private final Map<String, TimeZone> tzCache = new HashMap<String, TimeZone>();
    static {
        TimeZone tz = TimeZone.getDefault();
        globalDefault.tzCache.put(tz.getID(), tz);
    }

    private static final String[] MONTHS_EN_ENG = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
    private static final String[] MONTHS_DE_GER = { "JAN", "FEB", "MAR", "APR", "MAI", "JUN",
            "JUL", "AUG", "SEP", "OKT", "NOV", "DEZ" };
    private static final String[] MONTHS_FR_FRE = { "JAN", "FEV", "MAR", "AVR", "MAI", "JUIN",
            "JUIL", "AOU", "SEP", "OCT", "NOV", "DEC" };
    private static final String[] MONTHS_ES_SPA = { "ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
            "JUL", "AGO", "SEP", "OCT", "NOV", "DIC" };

    /**
     * LANGUAGE_MONTHS maps a language to a string array of 12 calendar month prefixes.
     */
    private final Map<String, String[]> LANGUAGE_MONTHS = new HashMap<String, String[]>();
    static {
        globalDefault.LANGUAGE_MONTHS.put("DE", MONTHS_DE_GER);
        globalDefault.LANGUAGE_MONTHS.put("EN", MONTHS_EN_ENG);
        globalDefault.LANGUAGE_MONTHS.put("FR", MONTHS_FR_FRE);
        globalDefault.LANGUAGE_MONTHS.put("ES", MONTHS_ES_SPA);
    }

    /**
     * SUPPORTED_LANGUAGES determines the order and selection in which different languages are
     * checked against calendar names. You can increase performance of the parser by ordering or
     * removing entries.
     */
    private final List<String> SUPPORTED_LANGUAGES = new ArrayList<String>();
    static {
    	globalDefault.SUPPORTED_LANGUAGES.add("EN");
    	globalDefault.SUPPORTED_LANGUAGES.add("ES");
    	globalDefault.SUPPORTED_LANGUAGES.add("FR");
    	globalDefault.SUPPORTED_LANGUAGES.add("DE");
    }
    
    public String[] getLanguageMonths(String language) {
    	return LANGUAGE_MONTHS.get(language);
    }

    /**
     * Returns true if 01/02/1970 is interpreted as 1970-02-01, returns false if 01/02/1970 is
     * interpreted as 1970-01-02.
     * 
     * @return True if DD/MM/YYYY is recognized by parser over MM/DD/YYYY.
     */
    public boolean isDmyOrder() {
        return this.isDmyOrder;
    }

    /**
     * Set true if parser should interpret dates as DD/MM/YYYY instead of MM/DD/YYYY.
     * 
     * @param isDmyOrder
     */
    public void setDmyOrder(boolean isDmyOrder) {
        this.isDmyOrder = isDmyOrder;
    }

    /**
     * The TimeZone under input should be interpreted when TZ is unspecified.
     * @return
     */
    public TimeZone getInputTimeZone() {
		return inputTimeZone;
	}

    /**
     * The TimeZone under input should be interpreted when TZ is unspecified.
     * @param inputTimeZone
     */
	public void setInputTimeZone(TimeZone inputTimeZone) {
		this.inputTimeZone = inputTimeZone;
	}

    
    
    /**
     * The TimeZone under which displayed output should be presented
     * @return
     */
    public TimeZone getOutputTimeZone() {
		return outputTimeZone;
	}

    /**
     * The TimeZone under which displayed output should be presented
     * @param localTimeZone
     */
	public void setOutputTimeZone(TimeZone outputTimeZone) {
		this.outputTimeZone = outputTimeZone;
	}

	/**
     * @return a Map of time zones recognized by DateTime.
     */
    public Map<String, String> getTzMap() {
        return tzMap;
    }

    /**
     * Merge a Map of time zones recognized by DateTime
     * 
     * @param tzMap
     */
    public void addTzMap(Map<String, String> tzMap) {
        tzMap.putAll(tzMap);
    }

    /**
     * 
     * @return The singleton used as the default DateTimeConfig.
     */
    public static DateTimeConfig getGlobalDefault() {
        return globalDefault;
    }

    /**
     * Reset the global default to a different DateTimeConfig object.
     * 
     * @param globalDefault
     */
    public static void setGlobalDefault(DateTimeConfig globalDefault) {
        DateTimeConfig.globalDefault = globalDefault;
    }

    /**
     * Get the day of week offset on the epoch date. This is used to calculate the day of week
     * for all other dates.
     * 
     * @return Day of week offset of the epoch date.
     */
    public int getEpochDOW() {
        return epochDOW;
    }

    /**
     * Set the day of week offset on the epoch date.
     */
    public void setEpochDOW(int epochDOW) {
        this.epochDOW = epochDOW;
    }

    /**
     * Set the default for the parser to interpret MM/DD/YYYY
     */
    public void globalAmericanDateFormat() {
        this.setDmyOrder(false);
    }

    /**
     * Set the default for the parser to interpret DD/MM/YYYY
     */
    public void globalEuropeanDateFormat() {
        this.setDmyOrder(true);
    }

    /**
     * Get the default date format.
     * 
     * @return A format string for dates.
     */
    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    /**
     * Set the default date format.
     * 
     * @param defaultDateFormat
     */
    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }

    /**
     * Get the default JDBC date format.
     * 
     * @return the default format desired for JDBC.
     */
    public String getDefaultJdbcFormat() {
        return defaultJdbcFormat;
    }

    /**
     * Set the default JDBC date format.
     * 
     * @param defaultJdbcFormat
     */
    public void setDefaultJdbcFormat(String defaultJdbcFormat) {
        this.defaultJdbcFormat = defaultJdbcFormat;
    }

    /**
     * Get an array of supported languages.
     */
    public String[] getSupportedLanguages() {
        return Arrays.copyOf(SUPPORTED_LANGUAGES.toArray(), SUPPORTED_LANGUAGES.size(), String[].class);
    }

    /**
     * Add your own uniquely named time zone to the list of interpreted zones.
     * 
     * @param id
     *            the name identifying your time zone
     * @param tz
     *            a TimeZone object
     */
    public void addTimeZone(String id, TimeZone tz) {
    	tzCache.put(id, tz);
    }

    /**
     * Fetch a registered time zone by name
     * 
     * @param id
     *            name of time zone to fetch
     * @return TimeZone object
     */
    public TimeZone getTimeZone(String id) {
        TimeZone tz;
        if (id==null) {
            tz=TimeZone.getDefault();
        } else if (!tzCache.containsKey(id)) {
            tz = TimeZone.getTimeZone(id);
            tzCache.put(id, tz);
        } else {
            tz = (TimeZone) tzCache.get(id);
        }
        return tz;
    }
    
    public DateTimeConfig clone() {
    	DateTimeConfig dtc=new DateTimeConfig();
    	dtc.defaultDateFormat=this.defaultDateFormat;
    	dtc.defaultJdbcFormat=this.defaultJdbcFormat;
    	dtc.epochDOW=this.epochDOW;
    	dtc.inputTimeZone=this.inputTimeZone;
    	dtc.isDmyOrder=this.isDmyOrder;
    	dtc.outputTimeZone=this.outputTimeZone;
    	dtc.LANGUAGE_MONTHS.putAll(this.LANGUAGE_MONTHS);
    	dtc.SUPPORTED_LANGUAGES.addAll(this.SUPPORTED_LANGUAGES);
    	dtc.tzCache.putAll(this.tzCache);
    	dtc.tzMap.putAll(this.tzMap);
    	return dtc;
    }

}
