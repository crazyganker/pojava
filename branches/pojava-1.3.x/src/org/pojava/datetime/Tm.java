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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class converts a DateTime into year, month, day, hour, minute, second,
 * millisecond, nanosecond. It is similar to the tm struct in C.
 * 
 * @author John Pile
 * 
 */
public class Tm {

	/**
	 * The following constants are represented in milliseconds
	 */
	private static final int HOUR = 3600000;
	private static final long DAY = HOUR * 24;
	private static final long YEAR = DAY * 365;
	private static final long QUADYEAR = DAY * (365 * 4 + 1);
	private static final long CENT = QUADYEAR * 25 - DAY;
	private static final long QUADCENT = 4 * CENT + DAY;
	// Our year starts March 1
	private static final long[] MONTH = { 0, 31 * DAY, 61 * DAY, 92 * DAY,
			122 * DAY, 153 * DAY, 184 * DAY, 214 * DAY, 245 * DAY, 275 * DAY,
			306 * DAY, 337 * DAY, 365 * DAY };
	/**
	 * The true Gregorian Calendar was initiated in October 1582, but this start
	 * date is easier for calculations, so I use it as an epoch. The year starts
	 * on March 1 so that a leap day is always at the end of a year.
	 */
	private static final long GREG_EPOCH = new DateTime("1600-03-01")
			.getMillis();
	/**
	 * These are the results we're looking to populate.
	 */
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int nanosecond;
	private int weekday;

	/**
	 * Populate year, month, day, hour, min, sec, nano from a DateTime
	 * 
	 * @param dt
	 *            DateTime object
	 */
	public Tm(DateTime dt) {
		init(dt);
	}

	/**
	 * Populate year, month, day, hour, min, sec, nano
	 * 
	 * @param millis
	 *            Date/Time in UTC assuming the default time zone.
	 */
	public Tm(long millis) {
		init(new DateTime(millis));
	}

	/**
	 * We'll direct the pre-GREG_EPOCH times here for now. Most folks don't use
	 * them, so optimizing is not my highest priority.
	 * 
	 * @param millis
	 */
	private void initYeOlde(long millis) {
		// Taking the easy way out...
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(millis);
		this.year = cal.get(Calendar.YEAR);
		this.month = cal.get(Calendar.MONTH);
		this.day = cal.get(Calendar.DATE);
		this.hour = cal.get(Calendar.HOUR_OF_DAY);
		this.minute = cal.get(Calendar.MINUTE);
		this.second = cal.get(Calendar.SECOND);
		this.weekday = cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Calculate date parts.
	 * 
	 * @param dt
	 *            DateTime
	 */
	private void init(DateTime dt) {
		long millis = dt.getMillis();
		long duration = millis - GREG_EPOCH;
		this.nanosecond = dt.getNanos();
		this.weekday = dt.getWeekday();
		if (dt.getTimeZone().inDaylightTime(dt.getDate())) {
			duration += HOUR;
		}
		if (millis < GREG_EPOCH) {
			initYeOlde(millis);
			return;
		}
		// Remove 400yr blocks, then 100yr, then 4, then 1.
		long quadCents = duration / QUADCENT;
		duration -= quadCents * QUADCENT;
		long cents = duration / CENT;
		duration -= cents * CENT;
		long quadYears = duration / QUADYEAR;
		duration -= quadYears * QUADYEAR;
		boolean canLeap = (duration >= YEAR);
		year = (int) (duration / YEAR);
		duration -= year * YEAR;
		// Calculate year based on those blocks
		year += 1600 + quadCents * 400 + cents * 100 + quadYears * 4;
		// This rough calculation gets close to the month, but not over
		month = (int) (duration / (30 * DAY));
		// This will get you the rest of the way there if needed.
		if (MONTH[month] <= duration) {
			month++;
		}
		// Same strategy as above, removing largest chunks first.
		duration -= MONTH[month - 1];
		day = (int) (duration / DAY);
		duration -= day * DAY;
		hour = (int) duration / HOUR;
		duration -= hour * HOUR;
		minute = (int) duration / 60000;
		duration -= minute * 60000;
		second = (int) duration / 1000;
		day++;
		// Shift from March calendar start, to January calendar start.
		month += 2;
		if (month > 12) {
			year++;
			month -= 12;
		}
		// If this is a leap year, March 1 is really Feb 29.
		if (canLeap && month == 3 && day == 1 && year % 4 == 0
				&& (year % 400 == 0 || year % 100 != 0)) {
			month--;
			day = 29;
		}
	}

	/**
	 * @return Year as YYYY
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Returns month between 1 and 12. Differs from C version of tm, but you can
	 * always subtract 1 if you want zero-based.
	 * 
	 * @return Month as Jan=1, Feb=2, ..., Dec=12
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Returns day of month between 1 and 31.
	 * 
	 * @return Day of month.
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Returns hour of day between 0 and 23.
	 * 
	 * @return Hour of day.
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Returns minute of hour between 0 and 59.
	 * 
	 * @return Minute of hour.
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * Returns second of minute between 0 and 59.
	 * 
	 * @return Second of minute.
	 */
	public int getSecond() {
		return second;
	}

	/**
	 * Returns millisecond fraction of second between 0 and 999999.
	 * 
	 * @return Millisecond fractino of second.
	 */
	public int getMillisecond() {
		return nanosecond / 1000000;
	}

	/**
	 * Returns nanosecond fraction of second between 0 and 999999999.
	 * 
	 * @return Nanosecond fraction of second.
	 */
	public int getNanosecond() {
		return nanosecond;
	}

	/**
	 * Returns weekday between 1 and 7
	 * 
	 * @return Typically (although configurable) Sun=1 .. Sat=7
	 */
	public int getWeekday() {
		return weekday;
	}

}
