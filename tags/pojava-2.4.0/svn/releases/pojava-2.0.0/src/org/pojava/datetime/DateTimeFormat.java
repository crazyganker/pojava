package org.pojava.datetime;

import java.util.TimeZone;
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

/**
 * DateTimeFormat formats a DateTime object as a String according to a template.
 * This class provides similar functionality to Java's SimpleDateFormat class,
 * with some notable differences. It changes the meaning of the "S" character
 * from "millisecond" to "fractional second" so that, for example, nine
 * consecutive "S" characters would represent nanoseconds, while three "S"
 * characters represent milliseconds. The upper-case "G" still represents "BC"
 * or "AD", but I added a lower-cased "g" to the format to use "BCE" or "CE".
 * 
 * Because it does not "compile" the format String, DateTimeFormat can provide a
 * static method with the same performance as a constructed object. It does
 * allow a constructed object for similar behavior to existing formatters, but
 * there is no performance advantage in doing so. In either case, this class is
 * "almost" thread-safe, provided your application is not trying to change the
 * definition of TimeZone objects as you're using them.
 * 
 * It is important to understand that the default behavior is to format the output
 * according to the system's time zone.  If you want to format the output according
 * to the DateTime object's internal time zone, then pass the time zone as a
 * parameter.  For example,
 * <pre>
 * DateTimeFormat("yyyy-MM-dd", dateTimeObj, dateTimeObj.getTimezone);
 * </pre>
 * 
 * For this version, you're kind of stuck with an English-only version of dates.
 * I'll be revising that at a future date.
 * 
 * @author John Pile
 * 
 */
public class DateTimeFormat {

	private static final String[] mos = { "", "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private static final String[] months = { "", "January", "February",
			"March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };
	private static final String[] wes = { "", "Sun", "Mon", "Tue", "Wed",
			"Thu", "Fri", "Sat" };
	private static final String[] weeks = { "", "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private static final int[] dom = { 0, 31, 59, 90, 120, 151, 181, 212, 243,
			273, 304, 334, 365 };
	private String template;

	public DateTimeFormat(String template) {
		this.template = template;
	}

	public String format(DateTime dt) {
		return format(this.template, dt);
	}

	public String format(long millis) {
		return format(this.template, new DateTime(millis));
	}

	public static String format(String template, long millis) {
		return format(template, new DateTime(millis));
	}

	public static String format(String template, DateTime dt) {
		return format(template, dt, TimeZone.getDefault());
	}

	public static String format(String template, DateTime dt, TimeZone tz) {
		Tm tm = new Tm(dt, tz);
		StringBuffer sb = new StringBuffer();
		StringBuffer word = new StringBuffer();
		char[] fmt = template.toCharArray();
		char prior = fmt[0];
		word.append(prior);
		for (int i = 1; i < fmt.length; i++) {
			if (fmt[i] == prior) {
				word.append(prior);
			} else {
				appendWord(sb, word, tm, dt);
				prior = fmt[i];
				word.setLength(0);
				word.append(prior);
			}
		}
		appendWord(sb, word, tm, dt);
		return sb.toString();
	}

	private static void appendWord(StringBuffer sb, StringBuffer word, Tm tm,
			DateTime dt) {
		char c = word.charAt(0);
		int len = word.length();
		switch (c) {
		case 'g':
			sb.append(tm.getYear() < 0 ? "BCE" : "CE");
			break;
		case 'G':
			sb.append(tm.getYear() < 0 ? "BC" : "AD");
			break;
		case 'y':
			sb.append(zfill(tm.getYear(), Math.max(2, len)));
			break;
		case 'M':
			if (len > 3) {
				sb.append(months[tm.getMonth()]);
			} else if (len == 3) {
				sb.append(mos[tm.getMonth()]);
			} else if (len == 2) {
				sb.append(zfill(tm.getMonth(), 2));
			} else {
				sb.append(tm.getMonth());
			}
			break;
		case 'D':
			if (len > 1) {
				sb.append(zfill(
						dom[tm.getMonth()] + tm.getDay() + leapDays(tm), len));
			} else {
				sb.append(dom[tm.getMonth()] + tm.getDay() + leapDays(tm));
			}
			break;
		case 'd':
			if (len > 1) {
				sb.append(zfill(tm.getDay(), len));
			} else {
				sb.append(tm.getDay());
			}
			break;
		case 'E':
			if (len > 3) {
				sb.append(weeks[tm.getWeekday()]);
			} else {
				sb.append(wes[tm.getWeekday()]);
			}
		case 'a':
			sb.append(tm.getHour() > 11 ? "PM" : "AM");
			break;
		case 'H':
			if (len > 1) {
				sb.append(zfill(tm.getHour(), len));
			} else {
				sb.append(tm.getHour());
			}
			break;
		case 'k':
			if (len > 1) {
				sb.append(zfill(1 + tm.getHour(), len));
			} else {
				sb.append(1 + tm.getHour());
			}
			break;
		case 'K':
			if (len > 1) {
				sb.append(zfill(tm.getHour() % 12, len));
			} else {
				sb.append(tm.getHour() % 12);
			}
			break;
		case 'h':
			int hr = tm.getHour() % 12;
			if (hr == 0)
				hr = 12;
			if (len > 1) {
				sb.append(zfill(hr, len));
			} else {
				sb.append(hr);
			}
			break;
		case 'm':
			if (len > 1) {
				sb.append(zfill(tm.getMinute(), len));
			} else {
				sb.append(tm.getMinute());
			}
			break;
		case 's':
			if (len > 1) {
				sb.append(zfill(tm.getSecond(), len));
			} else {
				sb.append(tm.getSecond());
			}
			break;
		case 'S':
			sb.append(zfill(tm.getNanosecond(), 9).substring(0, len));
			break;
		case 'z':
			sb.append(dt.timeZone().getDisplayName());
			break;
		case 'Z':
			int minutes = dt.timeZone().getOffset(dt.toMillis()) / 60000;
			if (minutes < 0) {
				sb.append('-');
				minutes = -minutes;
			} else {
				sb.append('+');
			}
			int hours = minutes / 60;
			sb.append(zfill(hours*100+minutes, 4));
			break;
		default:
			sb.append(word);
			break;
		}
	}

	private static String zfill(int value, int size) {
		StringBuffer zeros = new StringBuffer("000000000000");
		zeros.append(Integer.toString(value));
		return zeros.substring(zeros.length() - Math.min(zeros.length(), size));
	}

	private static int leapDays(Tm tm) {
		if (tm.getMonth() < 3) {
			return 0;
		}
		int year = tm.getYear();
		return year % 4 == 0 && (year % 400 == 0 || year % 100 != 0) ? 1 : 0;
	}
}
