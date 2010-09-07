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
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.pojava.util.StringTool;

/**
 * <p>
 * DateTime provides an immutable representation of Date and Time to the nearest
 * nanosecond. You can access DateTime properties either in milliseconds or in
 * seconds and nanoseconds. Both the seconds and milliseconds values can be
 * understood as being truncated to their respective precisions. Nanos holds the
 * fractional portion of a second in the range 0-999999999. Note that whether
 * seconds is positive or negative, the internal values will be adjusted if
 * necessary to support a positive value for nanos.
 * </p>
 * <p>
 * You may think of a DateTime object as a fixed offset of System time measured
 * from the Unix epoch in non-leap milliseconds or non-leap seconds and
 * nanoseconds. Leap years are calculated according to the Gregorian Calendar,
 * matching the same interpretation as the java.util.Date object (every 4th year
 * is a leap year, except for years divisible by 100 but not divisible by 400).
 * The times are stored according to the UTC (aka GMT) time zone, and a TimeZone
 * object is referenced to translate to a local time zone.
 * </p>
 * <p>
 * DateTime includes a robust parser for interpreting date and time from a
 * String. It parses a date and time using heuristics rather than comparing
 * against preset formats, so it is point-and-shoot simple. The following, for
 * example, are interpreted the same:
 * <ul>
 * <li>3:21pm on January 26, 1969</li>
 * <li>26-Jan-1969 03:21 PM</li>
 * <li>1/26/69 15:21</li>
 * <li>1969.01.26 15.21</li>
 * <li>el 26 de enero de 1969 15.21</li>
 * </ul>
 * <p>
 * Some notes on the date interpretations:
 * </p>
 * 
 * <p>
 * All dates are interpreted in your local time zone, unless a time zone is
 * specified in the String. Time zones are configurable in the DateTimeConfig
 * object, so you can determine for your own application whether CST, for
 * example, would adjust to Central Standard Time or Chinese Standard Time.
 * </p>
 * 
 * <p>
 * A two-digit year will assume up to 80 years in the past and 20 years in the
 * future. It is prudent in many cases to follow this with a check based on
 * whether you know the date to represent a past or future date. If you know you
 * parsed a birthday, you can compare with today's date and subtract 100 yrs if
 * needed (references to birthdays 20 years in the future are rare). Similarly,
 * if you're dealing with an annuity date, you can add 100 years if the parsed
 * date occurred in the past.
 * </p>
 * 
 * <p>
 * If you're parsing European dates expecting DD/MM/YYYY instead of MM/DD/YYYY,
 * then you can alter the global DateTimeConfig setting by first calling, "<code>DateTimeConfig.globalEuropeanDateFormat();</code>".
 * </p>
 * 
 * @author John Pile
 * 
 */
public class DateTime implements Serializable, Comparable {

	private static final long serialVersionUID = 1L;

	/**
	 * These months have less than 31 days
	 */
	private static final int FEB = 1;
	private static final int APR = 3;
	private static final int JUN = 5;
	private static final int SEP = 8;
	private static final int NOV = 10;

	/**
	 * CE is Common Era, Current Era, or Christian Era, a.k.a. AD.
	 */
	private static final long CE = -62135740800000L; // 0001-01-01;

	/**
	 * Default time zone is determined by system
	 */
	protected TimeZone timeZone = TimeZone.getDefault();

	/**
	 * Config contains info specific to zoning and formatting.
	 */
	protected DateTimeConfig config;

	/**
	 * System time is a lazy calculation of milliseconds from Unix epoch
	 * 1970-01-01 00:00:00, assuming no leap seconds and a leap year every year
	 * evenly divisible by 4, except for years divisible by 100 but not
	 * divisible by 400.
	 */
	protected Duration systemDur = null;

	private static final Pattern partsPattern = Pattern.compile("[^A-Z0-9]+");

	/**
	 * Default constructor gives current time to millisecond.
	 */
	public DateTime() {
		this.systemDur = new Duration(System.currentTimeMillis());
	}

	/**
	 * DateTime constructed from time in milliseconds since epoch.
	 * 
	 * @param millis
	 *            time
	 */
	public DateTime(long millis) {
		this.systemDur = new Duration(millis);
	}

	/**
	 * DateTime constructed from time in milliseconds since epoch.
	 * 
	 * @param millis
	 *            Number of milliseconds since epoch
	 * @param tz
	 *            Time Zone held by DateTime
	 */
	public DateTime(long millis, TimeZone tz) {
		this.systemDur = new Duration(millis);
		if (tz != null) {
			this.timeZone = tz;
		}
	}

	/**
	 * Construct a DateTime from seconds and fractional seconds.
	 * 
	 * @param seconds
	 *            Number of seconds since epoch (typically 1970-01-01)
	 * @param nanos
	 *            Nanosecond offset in range +/- 999999999
	 */
	public DateTime(long seconds, int nanos) {
		this.systemDur = new Duration(seconds, nanos);
	}

	/**
	 * Construct a DateTime from seconds and fractional seconds.
	 * 
	 * @param seconds
	 *            Number of seconds since epoch (typically 1970-01-01)
	 * @param nanos
	 *            Nanosecond offset in range +/- 999999999
	 * @param tz
	 *            TimeZone
	 */
	public DateTime(long seconds, int nanos, TimeZone tz) {
		this.systemDur = new Duration(seconds, nanos);
		if (tz != null) {
			this.timeZone = tz;
		}
	}

	/**
	 * DateTime constructed from a string using global defaults.
	 * 
	 * @param str
	 */
	public DateTime(String str) {
		if (str == null) {
			this.systemDur = new Duration(System.currentTimeMillis());
			return;
		}
		DateTime dt = parse(str);
		this.systemDur = dt.systemDur;
	}

	/**
	 * DateTime constructed from a Timestamp includes nanos.
	 * 
	 * @param ts
	 */
	public DateTime(Timestamp ts) {
		this.systemDur = new Duration(ts.getTime() / 1000, ts.getNanos());
	}

	/**
	 * Derive a time zone descriptor from the right side of the date/time
	 * string.
	 * 
	 * @param str
	 * @return
	 */
	private static final String tzParse(String str) {
		char[] chars = str.toCharArray();
		int min = 7;
		int max = str.length() - 1;
		int idx = max;
		char c;
		while (idx > min) {
			c = chars[idx];
			if (c >= '0' && c <= '9' || c == ':' || c == '+' || c == '-') {
				idx--;
			} else {
				break;
			}
		}
		while (idx >= min) {
			c = chars[idx];
			if (c >= 'A' && c <= 'Z' || c == '/' || c >= '0' && c <= '9') {
				idx--;
			} else {
				++idx;
				while (idx < max && chars[idx] >= '0' && chars[idx] <= '9') {
					if (++idx == max) {
						break;
					}
				}
				break;
			}
		}
		if (idx < min) {
			return null;
		}
		c = chars[idx];
		if ((c == '-' || c == '+') && idx > 0) {
			if (chars[idx] == ' ' || chars[idx] == '\t') {
				return str.substring(idx);
			}
			return null;
		}
		if (c >= 'A' && c <= 'Z') {
			return str.substring(idx);
		}
		return null;
	}

	/**
	 * Compare two DateTime objects to determine ordering.
	 * 
	 * @return -1, 0, or 1 based on comparison to another DateTime.
	 */
	public int compareTo(DateTime other) {
		if (other == null) {
			throw new NullPointerException("Cannot compare DateTime to null.");
		}
		return this.systemDur.compareTo(other.systemDur);
	}

	/**
	 * Compare to another Date + Time object to determine ordering
	 * 
	 * @return -1, 0, or 1 based on comparison to another DateTime, Date, or
	 *         Timestamp object.
	 */
	public int compareTo(Object other) {
		if (other.getClass() == DateTime.class) {
			return compareTo((DateTime) other);
		}
		if (other.getClass() == Timestamp.class) {
			return this.getTimestamp().compareTo((Timestamp) other);
		}
		if (Date.class.isAssignableFrom(other.getClass())) {
			return this.compareTo(new DateTime(((Date) other).getTime()));
		}
		throw new IllegalArgumentException("Cannot compare DateTime to "
				+ other.getClass().getName() + ".");
	}

	/**
	 * Get a timestamp useful for JDBC
	 * 
	 * @return This DateTime as a Timestamp object.
	 */
	public Timestamp getTimestamp() {
		Timestamp ts = new Timestamp(this.systemDur.getMillis());
		if (this.systemDur.getNanos() > 0) {
			ts.setNanos(this.systemDur.getNanos());
		}
		return ts;
	}

	/**
	 * Get Date/Time as a Java Date object.
	 * 
	 * @return this DateTime truncated and converted to a java.util.Date object.
	 */
	public Date getDate() {
		return new Date(this.systemDur.getMillis());
	}

	/**
	 * Get the TimeZone
	 * 
	 * @return this TimeZone.
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * The toString method gives a sortable ISO 8601 date and time to nearest
	 * second in the same timezone as the system.
	 */
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getConfig()
				.getDefaultDateFormat());
		if (this.systemDur.millis < DateTime.CE) {
			return simpleDateFormat
					.format(new Date(this.systemDur.getMillis()))
					+ " BC";
		}
		return simpleDateFormat.format(new Date(this.systemDur.getMillis()));
	}

	/**
	 * Providing a format allows for some flexibility. The inefficiency of
	 * constructing a Date and a SimpleDateFormat each time, plus a lack of
	 * sub-millisecond support does cry out for a custom Formatter, though.
	 * 
	 * @param format
	 * @return A formatted string version of the current DateTime.
	 */
	public String toString(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date(this.systemDur.getMillis()));
	}

	/**
	 * The toLocalString method provides a sortable ISO 8601 date and time to
	 * the nearest second, but is rendered from the perspective of the time zone
	 * ascribed to the DateTime object, regardless of the system's time zone.
	 * 
	 * @return A string version of the this DateTime specified in local time.
	 */
	public String toLocalString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(this.timeZone);
		return simpleDateFormat.format(new Date(this.systemDur.getMillis()));
	}

	/**
	 * Add a fixed duration of time
	 * 
	 * @param dur
	 * @return Newly calculated DateTime object.
	 */
	public DateTime add(Duration dur) {
		Duration calcDur = dur.add(this.getSeconds(), this.getNanos());
		return new DateTime(calcDur.getSeconds(), calcDur.getNanos(),
				this.timeZone);
	}

	/**
	 * Add a fixed duration in milliseconds. The Duration object provides fixed
	 * multipliers such as SECOND or HOUR.
	 * 
	 * @param milliseconds
	 * @return Newly calculated DateTime object.
	 */
	public DateTime add(long milliseconds) {
		Duration dur = this.systemDur.add(milliseconds);
		return new DateTime(dur.getSeconds(), dur.getNanos());
	}

	/**
	 * Add increments of any calendar time unit from a nanosecond to a century.
	 * This is different from a Duration in that it will make adjustments to
	 * preserve non-linear values such as daylight saving or day-of-month
	 * offsets.
	 * 
	 * @param calUnit
	 * @param qty
	 *            May be positive or negative.
	 * @return Newly calculated DateTime object.
	 */
	public DateTime add(CalendarUnit calUnit, int qty) {
		/* Fixed durations */
		if (calUnit.compareTo(CalendarUnit.DAY) < 0) {
			if (calUnit == CalendarUnit.HOUR) {
				return this.add(qty * 3600000);
			}
			if (calUnit == CalendarUnit.MINUTE) {
				return this.add(qty * 60000);
			}
			if (calUnit == CalendarUnit.SECOND) {
				return this.add(qty * 1000);
			}
			if (calUnit == CalendarUnit.MILLISECOND) {
				return this.add(qty);
			}
			if (calUnit == CalendarUnit.MICROSECOND) {
				return this.add(new Duration(0, qty * 1000));
			}
			if (calUnit == CalendarUnit.NANOSECOND) {
				return this.add(new Duration(0, qty));
			}
		}
		/* Calendar periods (same time, different day) */
		Calendar cal = Calendar.getInstance(this.timeZone);
		cal.setTimeInMillis(this.systemDur.millis);
		if (calUnit == CalendarUnit.DAY) {
			cal.add(Calendar.DATE, qty);
		} else if (calUnit == CalendarUnit.WEEK) {
			cal.add(Calendar.DATE, qty * 7);
		} else if (calUnit == CalendarUnit.MONTH) {
			cal.add(Calendar.MONTH, qty);
		} else if (calUnit == CalendarUnit.QUARTER) {
			cal.add(Calendar.MONTH, qty * 3);
		} else if (calUnit == CalendarUnit.YEAR) {
			cal.add(Calendar.YEAR, qty);
		} else if (calUnit == CalendarUnit.CENTURY) {
			cal.add(Calendar.YEAR, 100 * qty);
		}
		return new DateTime(cal.getTimeInMillis() / 1000, systemDur.getNanos(),
				this.timeZone);
	}

	/**
	 * Return numeric day of week, usually Sun=1, Mon=2, ... , Sat=7;
	 * 
	 * @return Numeric day of week, usually Sun=1, Mon=2, ... , Sat=7. See
	 *         DateTimeConfig.
	 */
	public int getWeekday() {
		long leftover = 0;
		// Adding 2000 years in weeks makes all calculations positive.
		// Adding epoch DOW shifts us into phase with start of week.
		long offset = getConfig().getEpochDOW() * Duration.DAY + 52
				* Duration.WEEK * 2000;
		leftover = offset + this.getMillis()
				+ timeZone.getOffset(this.getMillis());
		leftover %= Duration.WEEK;
		leftover /= Duration.DAY;
		// Convert from zero to one based
		leftover++;
		return (int) leftover;
	}

	/**
	 * Parse a time reference that fits in a single word. Supports: YYYYMMDD,
	 * [+-]D, [0-9]+Y
	 * 
	 * @param str
	 * @return New DateTime interpreted from string.
	 */
	private static DateTime parseRelativeDate(String str, IDateTimeConfig config) {
		char firstChar = str.charAt(0);
		char lastChar = str.charAt(str.length() - 1);
		if (str.length() == 8 && StringTool.onlyDigits(str)) {
			// YYYYMMDD
			StringBuffer sb = new StringBuffer(str.substring(0, 4));
			sb.append('/');
			sb.append(str.substring(4, 2));
			sb.append('/');
			sb.append(str.substring(6));
			return parse(sb.toString(), config);
		}
		DateTime dt = new DateTime();
		if ((firstChar == '+' || firstChar == '-') && lastChar >= '0'
				&& lastChar <= '9') {
			if (StringTool.onlyDigits(str.substring(1))) {
				int offset = (new Integer((firstChar == '+') ? str.substring(1)
						: str)).intValue();
				return dt.add(CalendarUnit.DAY, offset);
			}
		}
		if ((lastChar == 'D' || lastChar == 'Y' || lastChar == 'M')) {
			CalendarUnit unit = null;
			if (lastChar == 'D') {
				unit = CalendarUnit.DAY;
			} else if (lastChar == 'Y') {
				unit = CalendarUnit.YEAR;
			} else if (lastChar == 'M') {
				unit = CalendarUnit.MONTH;
			}
			String inner = str.substring(
					(firstChar >= '0' && firstChar <= '9') ? 0 : 1, str
							.length() - 1);
			// ^[+-][0-9]+$
			if (firstChar == '+') {
				if (StringTool.onlyDigits(inner)) {
					int offset = (new Integer(inner)).intValue();
					return dt.add(unit, offset);
				}
			}
			if (firstChar == '-' || firstChar >= '0' && firstChar <= '9') {
				if (StringTool.isInteger(inner)) {
					int innerVal = (new Integer(inner)).intValue();
					return dt.add(unit, innerVal);
				}
			}
		}
		throw new IllegalArgumentException("Could not parse date from '" + str
				+ "'");
	}

	/**
	 * Interpret a DateTime from a String using global defaults.
	 * 
	 * @param str
	 * @return New DateTime interpreted from string.
	 */
	public static DateTime parse(String str) {
		DateTimeConfig config = DateTimeConfig.getGlobalDefault();
		return parse(str, config);
	}

	/**
	 * Interpret a DateTime from a String.
	 * 
	 * @param str
	 * @param config
	 * @return New DateTime interpreted from string according to alternate
	 *         rules.
	 */
	public static DateTime parse(String str, IDateTimeConfig config) {
		if (str == null) {
			return null;
		}
		str = str.trim().toUpperCase();
		if (str.length() == 0) {
			throw new NullPointerException(
					"Cannot parse time from empty string.");
		}
		Tm tm = new Tm(System.currentTimeMillis());
		// Calendar cal = Calendar.getInstance();
		if (str.charAt(0) == '+' || str.charAt(0) == '-') {
			return parseRelativeDate(str, config);
		}
		String[] parts = partsPattern.split(str);
		boolean isYearFirst = false;
		boolean hasYear = false, hasMonth = false, hasDay = false;
		boolean hasHour = false, hasMinute = false, hasSecond = false;
		boolean hasNanosecond = false, hasTimeZone = false, inDST = false;
		int year = 0, month = 0, day = 0;
		int hour = 0, minute = 0, second = 0, nanosecond = 0;
		String tzString = null;
		TimeZone tz = TimeZone.getDefault();
		int thisYear = tm.getYear();
		int centuryTurn = thisYear - (thisYear % 100);
		// Build a table describing which fields are integers.
		boolean[] integers = new boolean[parts.length];
		boolean[] usedint = new boolean[parts.length];
		for (int i = 0; i < parts.length; i++) {
			if (StringTool.startsWithDigit(parts[i]))
				integers[i] = true;
		}
		// First, scan for text month
		for (int i = 0; i < parts.length; i++) {
			if (!integers[i] && parts[i].length() > 2) {
				Object[] langs = config.getSupportedLanguages();
				for (int lang = 0; lang < langs.length; lang++) {
					String[] mos = (String[]) DateTimeConfig.LANGUAGE_MONTHS
							.get(langs[lang]);
					int mo = StringTool.indexedStartMatch(mos, parts[i]);
					if (mo >= 0) {
						month = mo;
						hasMonth = true;
						break;
					}
				}
			}
			if (hasMonth) {
				break;
			}
		}
		// Next scan for 4-digit year or an 8 digit YYYYMMDD
		for (int i = 0; i < parts.length; i++) {
			if (integers[i] && !usedint[i]) {
				if (!hasYear
						&& (parts[i].length() == 4 || parts[i].length() == 5)) {
					char c = parts[i].charAt(parts[i].length() - 1);
					if (c >= '0' && c <= '9') {
						year = StringTool.parseIntFragment(parts[i]);
						hasYear = true;
						usedint[i] = true;
						isYearFirst = (i == 0);
						// If integer is to the immediate left of year, use now.
						if (DateTimeConfig.getGlobalDefault().isDmyOrder()) {
							if (!hasMonth && i > 0 && integers[i - 1]
									&& !usedint[i - 1]) {
								month = StringTool
										.parseIntFragment(parts[i - 1]);
								hasMonth = true;
								usedint[i - 1] = true;
							}
						} else {
							if (!hasDay && i > 0 && integers[i - 1]
									&& !usedint[i - 1]) {
								day = StringTool.parseIntFragment(parts[i - 1]);
								hasDay = true;
								usedint[i - 1] = true;
							}
						}
						break;
					}
				}
				if (!hasYear && !hasMonth && !hasDay && parts[i].length() == 8) {
					year = Integer.parseInt(parts[i].substring(0, 4));
					month = Integer.parseInt(parts[i].substring(4, 6));
					day = Integer.parseInt(parts[i].substring(6, 8));
					hasYear = true;
					hasMonth = true;
					hasDay = true;
					usedint[i] = true;
				}
			}
		}
		// One more scan for Date.toString() style
		if (!hasYear && str.endsWith("T " + parts[parts.length - 1])) {
			year = Integer.parseInt(parts[parts.length - 1]);
			hasYear = true;
			usedint[usedint.length - 1] = true;
		}
		// Assign integers to remaining slots in order
		for (int i = 0; i < parts.length; i++) {
			if (integers[i] && !usedint[i]) {
				int part = StringTool.parseIntFragment(parts[i]);
				if (!hasDay && part < 32 && config.isDmyOrder()) {
					/*
					 * If one sets the isDmyOrder to true in DateTimeConfig,
					 * then this will properly interpret DD before MM in
					 * DD-MM-yyyy dates. If the first number is a year, then an
					 * ISO 8601 date is assumed, in which MM comes before DD.
					 */
					if (!isYearFirst) {
						day = part;
						hasDay = true;
						usedint[i] = true;
						continue;
					}
				}
				if (!hasMonth && part < 13) {
					month = part - 1;
					hasMonth = true;
					usedint[i] = true;
					continue;
				}
				if (!hasDay && part < 32) {
					day = part;
					hasDay = true;
					usedint[i] = true;
					continue;
				}
				if (!hasYear && part < 1000) {
					if (part > 99) {
						year = 1900 + part;
					} else {
						if (centuryTurn + part - thisYear > 20) {
							year = centuryTurn + part - 100;
						} else {
							year = centuryTurn + part;
						}
					}
					hasYear = true;
					usedint[i] = true;
					continue;
				}
				if (!hasHour && part < 24) {
					hour = part;
					hasHour = true;
					usedint[i] = true;
					continue;
				}
				if (!hasMinute && part < 60) {
					minute = part;
					hasMinute = true;
					usedint[i] = true;
					if (parts[i].endsWith("PM")) {
						if (hasHour && hour >= 0 && hour < 12) {
							hour += 12;
						}
					}
					continue;
				}
				if (!hasSecond && part <= 60) {
					if (part < 60 || minute == 59 && hour == 23 && day >= 30
							&& (month == 11 || month == 5)) {
						second = part;
						hasSecond = true;
						usedint[i] = true;
						continue;
					}
				}
				if (!hasNanosecond && part < 1000000000) {
					nanosecond = Integer.parseInt((parts[i] + "00000000")
							.substring(0, 9));
					hasNanosecond = true;
					usedint[i] = true;
					continue;
				}
			} else if (!integers[i]) {
				if (hasHour && parts[i].equals("PM")) {
					if (hour >= 0 && hour < 12) {
						hour += 12;
					}
				}
			}
			// Typical formats are "Region/City, PST, Z, GMT+8, PST8PDT,
			// GMT-0800, GMT-08:00"
			if (!hasTimeZone) {
				tzString = tzParse(str);
				if (tzString == null || "PM".equals(tzString)
						|| "AM".equals(tzString)) {
					tz = TimeZone.getDefault();
				} else {
					String ref = (String) config.getTzMap().get(tzString);
					if (ref != null) {
						tzString = ref;
					}
					tz = TimeZone.getTimeZone(tzString);
				}
				hasTimeZone = true;
			}
		}
		/**
		 * Validate
		 */
		if (!hasYear || !hasMonth || !hasDay) {
			throw new IllegalArgumentException(
					"Could not determine Year, Month, and Day from '" + str
							+ "'");
		}
		if (month == FEB) {
			if (day > 28 + (year % 4 == 0 ? 1 : 0)) {
				throw new IllegalArgumentException("February " + day
						+ " does not exist in " + year);
			}
		} else if (month == SEP || month == APR || month == JUN || month == NOV) {
			if (day > 30) {
				throw new IllegalArgumentException(
						"30 days hath Sep, Apr, Jun, and Nov... not " + day);
			}
		} else {
			if (day > 31) {
				throw new IllegalArgumentException("No month has " + day
						+ " days in it.");
			}
		}
		DateTime returnDt;
		Calendar cal = null;
		if (hasTimeZone) {
			cal = Calendar.getInstance(tz);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			cal.set(Calendar.MILLISECOND, nanosecond / 1000000);
			returnDt = new DateTime(cal.getTimeInMillis(), tz);
		} else {
			// Start with Pacific Standard Time (which observes DST)
			tz = TimeZone.getTimeZone("America/Los_Angeles");
			cal = Calendar.getInstance(tz);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			cal.set(Calendar.MILLISECOND, nanosecond / 1000000);
			// Determine if date is in DST
			if (tz.inDaylightTime(new Date(cal.getTimeInMillis()))) {
				inDST = true;
			}
			StringBuffer sb = new StringBuffer(8);
			if (tzString != null) {
				char[] tzChars = tzString.toCharArray();
				char c = tzChars[0];
				/**
				 * GMT-7 and +07:00 mean the same thing, as do GMT+7 and -07:00.
				 * This is a weird POSIX requirement, but predictable.
				 */
				boolean isNeg = false;
				boolean isTz = false;
				if (c == '+' || c == '-') {
					isNeg = (c == '-');
					isTz = true;
				} else {
					if (tzString.indexOf("00") == -1) {
						isNeg = tzString.indexOf('+') >= 0;
					}
				}
				if (isTz) {
					for (int i = 0; i < tzChars.length; i++) {
						c = tzChars[i];
						if (c >= '0' && c <= '9') {
							sb.append(c);
						}
					}
					int offsetHours = Integer.parseInt(sb.toString());
					if (offsetHours > 100) {
						offsetHours /= 100;
					}
					if (isNeg) {
						offsetHours = -offsetHours;
					}
					if (inDST) {
						offsetHours--;
					}
					tz = TimeZone.getTimeZone(config.getTzMap().get(
							new Integer(offsetHours).toString()).toString());
				}
			}
			cal = Calendar.getInstance(tz);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			cal.set(Calendar.MILLISECOND, nanosecond / 1000000);
			returnDt = new DateTime(cal.getTimeInMillis(), tz);
		}
		return new DateTime(returnDt.getSeconds(), nanosecond);
	}

	/**
	 * Truncate DateTime down to its nearest time unit as a time.
	 * CalendarUnit.(WEEK|DAY|HOUR|MINUTE|SECOND|MILLISECOND)
	 * 
	 * @param unit
	 * @return A newly calculated DateTime.
	 */
	public DateTime truncate(CalendarUnit unit) {
		long trim = 0;
		// Simple optimization.
		if (unit.compareTo(CalendarUnit.HOUR) < 0) {
			if (unit == CalendarUnit.MINUTE) {
				trim = this.systemDur.millis % Duration.MINUTE;
				if (trim < 0)
					trim += Duration.MINUTE;
				return new DateTime(this.systemDur.millis - trim, this.timeZone);
			}
			if (unit == CalendarUnit.SECOND) {
				trim = this.systemDur.millis % Duration.SECOND;
				if (trim < 0)
					trim += Duration.SECOND;
				return new DateTime(this.systemDur.millis - trim, this.timeZone);
			}
			if (unit == CalendarUnit.MILLISECOND) {
				return new DateTime(this.systemDur.millis, this.timeZone);
			}
			if (unit == CalendarUnit.MICROSECOND) {
				int nanotrim = this.systemDur.nanos % 1000000;
				if (nanotrim < 0)
					nanotrim += 1000000;
				return new DateTime(this.getSeconds(), this.systemDur.nanos
						- nanotrim, this.timeZone);
			}
			return new DateTime(this.systemDur.millis, this.timeZone);
		}
		// Shift to same time of day at Rose line
		long calcTime = this.systemDur.millis
				+ timeZone.getOffset(this.systemDur.millis);
		// Truncate and shift back to local time
		if (unit == CalendarUnit.HOUR) {
			trim = calcTime % Duration.HOUR;
			if (trim < 0)
				trim += Duration.HOUR;
			calcTime -= trim;
			calcTime -= timeZone.getOffset(calcTime);
			return new DateTime(calcTime, this.timeZone);
		}
		if (unit == CalendarUnit.DAY) {
			trim = calcTime % Duration.DAY;
			if (trim < 0)
				trim += Duration.DAY;
			calcTime -= trim;
			calcTime -= timeZone.getOffset(calcTime);
			return new DateTime(calcTime, this.timeZone);
		}
		if (unit == CalendarUnit.WEEK) {
			if (config == null) {
				config = DateTimeConfig.getGlobalDefault();
			}
			long dow = ((calcTime / Duration.DAY) + config.getEpochDOW()) % 7;
			calcTime -= (calcTime % Duration.DAY + Duration.DAY * dow);
			calcTime -= timeZone.getOffset(calcTime);
			return new DateTime(calcTime, this.timeZone);
		}
		// TODO: Wean off of Calendar object
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.systemDur.millis);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if (unit == CalendarUnit.MONTH) {
			return new DateTime(cal.getTimeInMillis(), this.timeZone);
		}
		if (unit == CalendarUnit.QUARTER) {
			int monthOffset = cal.get(Calendar.MONTH) % 3;
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - monthOffset);
			return new DateTime(cal.getTimeInMillis(), this.timeZone);
		}
		if (unit == CalendarUnit.YEAR) {
			cal.set(Calendar.MONTH, 0);
			return new DateTime(cal.getTimeInMillis(), this.timeZone);
		}
		if (unit == CalendarUnit.CENTURY) {
			int calYear = cal.get(Calendar.YEAR);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.YEAR, calYear - calYear % 100);
			return new DateTime(cal.getTimeInMillis(), this.timeZone);
		}
		throw new IllegalArgumentException(
				"That precision is still unsupported.  Sorry, my bad.");
	}

	/**
	 * Whole seconds offset from epoch.
	 * 
	 * @return Whole seconds offset from epoch (1970-01-01 00:00:00).
	 */
	public long getSeconds() {
		return systemDur.getSeconds();
	}

	/**
	 * Whole milliseconds offset from epoch.
	 * 
	 * @return Milliseconds offset from epoch (1970-01-01 00:00:00).
	 */
	public long getMillis() {
		return systemDur.getMillis();
	}

	/**
	 * Positive nanosecond offset from Seconds.
	 * 
	 * @return Fractional second in nanoseconds for the given time.
	 */
	public int getNanos() {
		int nanos = systemDur.getNanos();
		return nanos >= 0 ? nanos : 1000000000 + nanos;
	}

	/**
	 * This compares a DateTime with either another DateTime or any of Java's
	 * standard dates extending from java.util.Date.
	 * 
	 * @param dateTime
	 * @return True if DateTime values represent the same point in time.
	 */
	public boolean equals(Object dateTime) {
		DateTime dt = null;
		if (dateTime == null) {
			return false;
		}
		if (dateTime instanceof DateTime) {
			dt = (DateTime) dateTime;
		} else if (dateTime instanceof java.util.Date) {
			dt = new DateTime(((java.util.Date) dateTime).getTime());
		}
		return systemDur.getMillis() == dt.getMillis()
				&& systemDur.getNanos() == dt.getNanos();
	}

	/**
	 * Return the global configuration used by DateTime.
	 * 
	 * @return the global DateTimeConfig object used by DateTime.
	 */
	public DateTimeConfig getConfig() {
		if (config == null) {
			config = DateTimeConfig.getGlobalDefault();
		}
		return config;
	}

}