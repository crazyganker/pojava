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

import java.util.Iterator;

import org.pojava.ordinals.Ordinal;
import org.pojava.ordinals.OrdinalSet;

/**
 * A CalendarUnit represents a time interval whose duration is allowed to vary
 * in order to adjust to Daylight Saving Time where needed.
 * 
 * The fundamental difference between CalendarUnit and Duration is that
 * calculations performed with CalendarUnit will seek to match an equivalent
 * time of day should a DST to non-DST boundary be crossed, whereas calculations
 * with Duration will simply add a fixed unit of time.
 * 
 * @author John Pile
 * 
 */
public class CalendarUnit extends Ordinal {

	private static final OrdinalSet ordinals = new OrdinalSet();

	/**
	 * A NANOSECOND = a billionth of a second
	 */
	public static final CalendarUnit NANOSECOND = new CalendarUnit("NANOSECOND");
	/**
	 * A MICROSECOND = a millionth of a second
	 */
	public static final CalendarUnit MICROSECOND = new CalendarUnit(
			"MICROSECOND");
	/**
	 * A MILLISECOND = a thousandth of a second
	 */
	public static final CalendarUnit MILLISECOND = new CalendarUnit(
			"MILLISECOND");
	/**
	 * A SECOND = Well, you probably know what a second is...
	 */
	public static final CalendarUnit SECOND = new CalendarUnit("SECOND");
	/**
	 * A MINUTE = sixty seconds (there are no leap seconds in system time)
	 */
	public static final CalendarUnit MINUTE = new CalendarUnit("MINUTE");
	/**
	 * An HOUR = a shift of one hour on the clock, regardless of elapsed time.
	 * For example, adding 3 CalendarUnit.HOUR units to a DateTime value at
	 * midnight will always evaluate to 3:00am, regardless of whether Daylight
	 * Saving Time begins or ends on that date.
	 */
	public static final CalendarUnit HOUR = new CalendarUnit("HOUR");
	/**
	 * A DAY = a shift to the same time on the next calendar day
	 */
	public static final CalendarUnit DAY = new CalendarUnit("DAY");
	/**
	 * A WEEK = a shift to the same time offset by 7 calendar days
	 */
	public static final CalendarUnit WEEK = new CalendarUnit("WEEK");
	/**
	 * A MONTH = a shift to the same day of the month for a different month
	 */
	public static final CalendarUnit MONTH = new CalendarUnit("MONTH");
	/**
	 * A QUARTER = a shift of three calendar months
	 */
	public static final CalendarUnit QUARTER = new CalendarUnit("QUARTER");
	/**
	 * A YEAR = a shift of one calendar year
	 */
	public static final CalendarUnit YEAR = new CalendarUnit("YEAR");
	/**
	 * A CENTRY = a shift of one hundred calendar years
	 */
	public static final CalendarUnit CENTURY = new CalendarUnit("CENTURY");

	private CalendarUnit(String name) {
		register(ordinals, name, this);
	}

	/**
	 * An iterator over each of the CalendarUnit constants.
	 */
	public Iterator iterator() {
		return ordinals.iterator();
	}

	/**
	 * Access a CalendarUnit by its name.
	 * 
	 * @param name
	 * @return Named CalendarUnit
	 */
	public static CalendarUnit valueOf(String name) {
		Ordinal located = ordinals.get(name);
		if (located == null) {
			throw new IllegalArgumentException("No ordinal class "
					+ CalendarUnit.class.getName() + "." + name);
		}
		return (CalendarUnit) located;
	}

}
