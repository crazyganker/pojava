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
 * A CalendarUnit represents a regular or irregular time interval.
 * 
 * @author John Pile
 * 
 */
public class CalendarUnit extends Ordinal {

	private static final OrdinalSet ordinals = new OrdinalSet();

	public static final CalendarUnit NANOSECOND = new CalendarUnit("NANOSECOND");
	public static final CalendarUnit MICROSECOND = new CalendarUnit(
			"MICROSECOND");
	public static final CalendarUnit MILLISECOND = new CalendarUnit(
			"MILLISECOND");
	public static final CalendarUnit SECOND = new CalendarUnit("SECOND");
	public static final CalendarUnit MINUTE = new CalendarUnit("MINUTE");
	public static final CalendarUnit HOUR = new CalendarUnit("HOUR");
	public static final CalendarUnit DAY = new CalendarUnit("DAY");
	public static final CalendarUnit WEEK = new CalendarUnit("WEEK");
	public static final CalendarUnit MONTH = new CalendarUnit("MONTH");
	public static final CalendarUnit QUARTER = new CalendarUnit("QUARTER");
	public static final CalendarUnit YEAR = new CalendarUnit("YEAR");
	public static final CalendarUnit CENTURY = new CalendarUnit("CENTURY");

	private CalendarUnit(String name) {
		register(ordinals, name, this);
	}

	public Iterator iterator() {
		return ordinals.iterator();
	}

	public static CalendarUnit valueOf(String name) {
		Ordinal located = ordinals.get(name);
		if (located == null) {
			throw new IllegalArgumentException("No ordinal class "
					+ CalendarUnit.class.getName() + "." + name);
		}
		return (CalendarUnit) located;
	}

}
