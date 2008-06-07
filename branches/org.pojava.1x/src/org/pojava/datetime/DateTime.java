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
import java.security.InvalidParameterException;
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
 * nanoseconds. Leap years are calculated to match the same interpretation as
 * the java.util.Date object (every 4th year is a leap year). More accurate
 * representations of time, such as ones skipping leap years every century
 * except millenials, or adjusting for leap seconds, will be done by wrapping
 * this object and adding calculations.
 * </p>
 * <p>
 * There are two tables for calculating dates that change over time. One is time
 * zones, and the other is leap seconds. Ideally, updates to those service
 * tables or formulas would not require an update to this API. Handling of leap
 * seconds is a good fit for a wrapper class, (see DateTimeUTC), that adds or
 * subtracts leap time as needed before and after the inner calls to each
 * method. Handling of Daylight Saving Time is mitigated by allowing you to
 * remap your own time zone labels into existing ones provided by the TimeZone
 * object.
 * </p>
 * 
 * @author John Pile
 * 
 */
public class DateTime implements Serializable, Comparable {

	private static final long serialVersionUID = 1;

	/**
	 * These months have less than 31 days
	 */
	private static final int FEB = 1;
	private static final int APR = 3;
	private static final int JUN = 5;
	private static final int SEP = 8;
	private static final int NOV = 10;

	/**
	 * Default time zone is determined by system
	 */
	protected TimeZone timeZone = TimeZone.getDefault();

	/**
	 * System time is a lazy calculation of milliseconds from Unix epoch
	 * 1970-01-01 00:00:00, assuming no leap seconds and a leap year every year
	 * evenly divisible by 4.
	 */
	protected Duration systemDur = null;

	/** Non-leap Milliseconds since an epoch */
	// protected long millis = 0;
	/** Nanoseconds used by high-resolution time stamps */
	// protected int nanos = 0;
	private static Pattern partsPattern = Pattern.compile("[^A-Z0-9]+");
	private static Pattern tzPattern = Pattern.compile("[^A-Z0-9/:+-]+");

	/** A base offset that can be altered for testing. */
	// protected static long epoch = DateTimeConfig.UNIX_EPOCH;
	/**
	 * Default constructor gives current time to millisecond.
	 */
	public DateTime() {
		this.systemDur = new Duration(System.currentTimeMillis());
	}

	/**
	 * DateTime constructed from time in milliseconds since epoch.
	 * 
	 * @param time
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

	/*
	 * protected void init(long seconds, int nanos) { if (nanos > 999999999 ||
	 * nanos < -999999999) { throw new InvalidParameterException( "Nanos must be
	 * specified in range +/- 999999999"); } if (nanos < 0) { seconds--; nanos +=
	 * 1000000000; } this.millis = seconds * 1000 + nanos / 1000000; this.nanos =
	 * nanos; }
	 */

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
	 * Compare two DateTime objects to determine ordering.
	 */
	public int compareTo(DateTime other) {
		if (other == null) {
			throw new NullPointerException("Cannot compare DateTime to null.");
		}
		return this.systemDur.compareTo(other.systemDur);
	}

	public int compareTo(Object other) {
		if (other.getClass() != DateTime.class) {
			throw new InvalidParameterException(
					"Cannot compare DateTime type to "
							+ other.getClass().getName());
		}
		return compareTo((DateTime) other);
	}

	/**
	 * Get a timestamp useful for JDBC
	 * 
	 * @return
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
	 * @return
	 */
	public Date getDate() {
		return new Date(this.systemDur.getMillis());
	}

	/**
	 * Get the TimeZone
	 * 
	 * @return
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * The toString method gives a sortable ISO 8601 date and time to nearest
	 * second in the same timezone as the system.
	 */
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(new Date(this.systemDur.getMillis()));
	}

	/**
	 * Providing a format allows for some flexibility. The inefficiency of
	 * constructing a Date and a SimpleDateFormat each time, plus a lack of
	 * sub-millisecond support does cry out for a custom Formatter, though.
	 * 
	 * @param format
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @param unit
	 * @param value
	 */
	public DateTime add(CalendarUnit calUnit, int qty) {
		/* Fixed durations */
		if (calUnit.compareTo(CalendarUnit.DAY) < 0) {
			if (calUnit == CalendarUnit.NANOSECOND) {
				return this.add(new Duration(0, qty));
			}
			if (calUnit == CalendarUnit.MICROSECOND) {
				return this.add(new Duration(0, qty * 1000));
			}
			if (calUnit == CalendarUnit.MILLISECOND) {
				return this.add(qty);
			}
			if (calUnit == CalendarUnit.SECOND) {
				return this.add(qty * 1000);
			}
			if (calUnit == CalendarUnit.MINUTE) {
				return this.add(qty * 60000);
			}
			if (calUnit == CalendarUnit.HOUR) {
				return this.add(qty * 3600000);
			}
		}
		/* Calendar periods (same time, different day) */
		Calendar cal = Calendar.getInstance(this.timeZone);
		cal.setTimeInMillis(this.systemDur.millis);
		if (calUnit == CalendarUnit.DAY) {
			cal.add(Calendar.DATE, qty);
		}
		if (calUnit == CalendarUnit.WEEK) {
			cal.add(Calendar.DATE, qty * 7);
		}
		if (calUnit == CalendarUnit.MONTH) {
			cal.add(Calendar.MONTH, qty);
		}
		if (calUnit == CalendarUnit.QUARTER) {
			cal.add(Calendar.MONTH, qty * 3);
		}
		if (calUnit == CalendarUnit.YEAR) {
			cal.add(Calendar.YEAR, qty);
		}
		if (calUnit == CalendarUnit.CENTURY) {
			cal.add(Calendar.YEAR, 100 * qty);
		}
		return new DateTime(cal.getTimeInMillis() / 1000, (int) (cal
				.getTimeInMillis() % 1000)
				* 1000000 + systemDur.getNanos() % 1000000, this.timeZone);
	}

	/**
	 * Return numeric day of week, usually Sun=1, Mon=2, ... , Sat=7;
	 * 
	 * @return
	 */
	public int getWeekday() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.getMillis());
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Parse a time reference that fits in a single word. Supports: YYYYMMDD,
	 * [+-]D, [0-9]+Y
	 * 
	 * @param str
	 * @return
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
			// ^[+-][0-9]+$
			if (firstChar == '+') {
				if (StringTool.onlyDigits(str.substring(1))) {
					int offset = (new Integer(str.substring(1))).intValue();
					return dt.add(CalendarUnit.DAY, offset);
				}
			}
			if (firstChar == '-' || firstChar >= '0' && firstChar <= '9') {
				String inner = str.substring(0, str.length() - 1);
				if (StringTool.isInteger(inner)) {
					int innerVal = (new Integer(inner)).intValue();
					if (lastChar == 'D') {
						// Today + days
						return dt.add(CalendarUnit.DAY, innerVal);
					}
					if (lastChar == 'M') {
						// Today + months
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.MONTH, innerVal);
						return new DateTime(cal.getTimeInMillis());
					}
					if (lastChar == 'Y') {
						// Today + years
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.YEAR, innerVal);
						return new DateTime(cal.getTimeInMillis());
					}
				}
			}
		}
		throw new InvalidParameterException("Could not parse date from '" + str
				+ "'");
	}

	/**
	 * Interpret a DateTime from a String using global defaults.
	 * 
	 * @param str
	 * @return
	 */
	public static DateTime parse(String str) {
		DateTimeConfig config = DateTimeConfig.getGlobalDefault();
		return parse(str, config);
	}

	private static int indexedStartMatch(String[] list, String str) {
		for (int i = 0; i < list.length; i++) {
			int len = list[i].length();
			if (str.length() >= len && list[i].equals(str.substring(0, len))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Interpret a DateTime from a String.
	 * 
	 * @param str
	 * @param config
	 * @return
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
		Calendar cal = Calendar.getInstance();
		String[] parts = partsPattern.split(str);
		if (parts.length == 1) {
			return parseRelativeDate(str, config);
		}
		boolean isYearFirst = false;
		boolean hasYear = false, hasMonth = false, hasDay = false;
		boolean hasHour = false, hasMinute = false, hasSecond = false;
		boolean hasNanosecond = false, hasTimeZone = false, inDST = false;
		int year = 0, month = 0, day = 0;
		int hour = 0, minute = 0, second = 0, nanosecond = 0;
		String tzString = null;
		TimeZone tz = TimeZone.getDefault();
		int thisYear = cal.get(Calendar.YEAR);
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
					int mo = indexedStartMatch(mos, parts[i]);
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
					year = StringTool.parseIntFragment(parts[i]);
					hasYear = true;
					usedint[i] = true;
					isYearFirst = (i == 0);
					break;
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
				} else {
					// Typical format is "Region/City or TLA"
					if (!hasTimeZone) {
						String tzParts[] = tzPattern.split(str);
						if (tzParts != null) {
							String p = tzParts[tzParts.length - 1];
							if (p.startsWith("+") || p.startsWith("-")) {
								tzString = p;
							} else if (StringTool.isInteger(p)) {
								tz = TimeZone.getDefault();
								hasTimeZone = true;
							} else {
								/**
								 * If zone not known, but offset is, then use
								 * offset + parsed date to determine timezone.
								 */
								String ref = (String) config.getTzMap().get(p);
								if (ref != null) {
									p = ref;
								}
								tz = TimeZone.getTimeZone(p);
								if (tz.getRawOffset() != 0) {
									hasTimeZone = true;
								} else {
									tzString = p;
								}
							}
						}
					}
				}
			}
		}
		/**
		 * Validate
		 */
		if (!hasYear || !hasMonth || !hasDay) {
			throw new InvalidParameterException(
					"Could not determine Year, Month, and Day from '" + str
							+ "'");
		}
		if (month == FEB) {
			if (day > 28 + (year % 4 == 0 ? 1 : 0)) {
				throw new InvalidParameterException("February " + day
						+ " does not exist in " + year);
			}
		} else if (month == SEP || month == APR || month == JUN || month == NOV) {
			if (day > 30) {
				throw new InvalidParameterException(
						"30 days hath Sep, Apr, Jun, and Nov... not " + day);
			}
		} else {
			if (day > 31) {
				throw new InvalidParameterException("No month has " + day
						+ " days in it.");
			}
		}
		DateTime returnDt;
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
				 * This is weird, but predictably so.
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
	 * DateTime.(WEEK|DAY|HOUR|MINUTE|SECOND|MILLISECOND)
	 * 
	 * @param unit
	 * @return
	 */
	public DateTime truncate(CalendarUnit unit) {
		// Simple optimization.
		if (unit.compareTo(CalendarUnit.HOUR) < 0) {
			if (unit == CalendarUnit.MINUTE) {
				return new DateTime(this.systemDur.millis
						- this.systemDur.millis % Duration.MINUTE,
						this.timeZone);
			}
			if (unit == CalendarUnit.SECOND) {
				return new DateTime(this.systemDur.millis
						- this.systemDur.millis % Duration.SECOND,
						this.timeZone);
			}
			if (unit == CalendarUnit.MILLISECOND) {
				return new DateTime(this.systemDur.millis, this.timeZone);
			}
			if (unit == CalendarUnit.MICROSECOND) {
				return new DateTime(this.getSeconds(), this.systemDur.nanos
						- this.systemDur.nanos % 1000, this.timeZone);
			}
			return new DateTime(this.systemDur.millis, this.timeZone);
		}
		// Shift to same time of day at Rose line
		long calcTime = this.systemDur.millis
				+ timeZone.getOffset(this.systemDur.millis);
		// Truncate and shift back to local time
		if (unit == CalendarUnit.HOUR) {
			calcTime -= (calcTime % Duration.HOUR);
			calcTime -= timeZone.getOffset(calcTime);
			return new DateTime(calcTime, this.timeZone);
		}
		if (unit == CalendarUnit.DAY) {
			calcTime -= (calcTime % Duration.DAY);
			calcTime -= timeZone.getOffset(calcTime);
			return new DateTime(calcTime, this.timeZone);
		}
		if (unit == CalendarUnit.WEEK) {
			long dow = ((calcTime / Duration.DAY) + DateTimeConfig
					.getGlobalDefault().getEpochDOW()) % 7;
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
		throw new InvalidParameterException(
				"That precision is still unsupported.  Sorry, my bad.");
	}

	/**
	 * Return duration truncated seconds.
	 * 
	 * @return
	 */
	public long getSeconds() {
		return systemDur.getSeconds();
	}

	public long getMillis() {
		return systemDur.getMillis();
	}

	public int getNanos() {
		return systemDur.getNanos();
	}

}