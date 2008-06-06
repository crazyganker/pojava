package org.pojava.datetime;

/*
 Copyright 2008 John Pile

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Establish global defaults for shaping DateTime behavior.
 * 
 * @author John Pile
 * 
 */
public class DateTimeConfig implements IDateTimeConfig {

	/** A representation of Unix epoch as millisecond offset of calculated 1 CE */
	public static final long UNIX_EPOCH = (1969 * 365 + 1969 / 4) * 86400000;

	/**
	 * Singleton pattern. The globalDefault variable is referenced by DateTime,
	 * so changes you make here affect new calls to DateTime.
	 */
	private static DateTimeConfig globalDefault = new DateTimeConfig();

	/**
	 * This determines the default interpretation of a ##/##/#### date, whether
	 * Day precedes Month or vice versa.
	 */
	private boolean isDmyOrder = false;

	/**
	 * The 1/1/1970 epoch started on a Thursday. If Sunday is the start of a
	 * week, then this number is 4. If Monday is the start, then set to 3.
	 */
	private int epochDOW = 4;

	/**
	 * The default date format used for DateTime.toString();
	 */
	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * <p>
	 * Support parsing of zones unlisted in TimeZone by translating to known
	 * zones. Got a zone that's not supported or should be overridden? Fix it
	 * locally by updating your custom tzMap!
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
	private static Map tzMap = new HashMap();

	private static final String[] MONTHS_EN_ENG = { "JAN", "FEB", "MAR", "APR",
			"MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	private static final String[] MONTHS_DE_GER = { "JAN", "FEB", "MAR", "APR",
			"MAI", "JUN", "JUL", "AUG", "SEP", "OKT", "NOV", "DEZ" };
	private static final String[] MONTHS_FR_FRE = { "JAN", "FEV", "MAR", "AVR",
			"MAI", "JUIN", "JUIL", "AOU", "SEP", "OCT", "NOV", "DEC" };
	private static final String[] MONTHS_ES_SPA = { "ENE", "FEB", "MAR", "ABR",
			"MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC" };

	public static final Map LANGUAGE_MONTHS = new HashMap();
	static {
		LANGUAGE_MONTHS.put("DE", MONTHS_DE_GER);
		LANGUAGE_MONTHS.put("EN", MONTHS_EN_ENG);
		LANGUAGE_MONTHS.put("FR", MONTHS_FR_FRE);
		LANGUAGE_MONTHS.put("ES", MONTHS_ES_SPA);
	}

	public static final List SUPPORTED_LANGUAGES = new ArrayList();
	static {
		SUPPORTED_LANGUAGES.add("EN");
		SUPPORTED_LANGUAGES.add("ES");
		SUPPORTED_LANGUAGES.add("FR");
		SUPPORTED_LANGUAGES.add("DE");
	}

	public boolean isDmyOrder() {
		return this.isDmyOrder;
	}

	public void setDmyOrder(boolean isDmyOrder) {
		this.isDmyOrder = isDmyOrder;
	}

	public Map getTzMap() {
		return tzMap;
	}

	public void setTzMap(Map tzMap) {
		DateTimeConfig.tzMap = tzMap;
	}

	public static DateTimeConfig getGlobalDefault() {
		return globalDefault;
	}

	public static void setGlobalDefault(DateTimeConfig globalDefault) {
		DateTimeConfig.globalDefault = globalDefault;
	}

	public int getEpochDOW() {
		return epochDOW;
	}

	public void setEpochDOW(int epochDOW) {
		this.epochDOW = epochDOW;
	}

	public static void globalAmericanDateFormat() {
		globalDefault.setDmyOrder(false);
	}

	public static void globalEuropeanDateFormat() {
		globalDefault.setDmyOrder(true);
	}

	public String getDefaultDateFormat() {
		return defaultDateFormat;
	}

	public void setDefaultDateFormat(String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
	}

	public Object[] getSupportedLanguages() {
		return SUPPORTED_LANGUAGES.toArray();
	}
}
