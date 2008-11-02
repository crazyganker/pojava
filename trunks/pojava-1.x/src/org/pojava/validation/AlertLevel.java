package org.pojava.validation;

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
 * An ordinal for {DEBUG, INFO, ERROR, WARNING}
 * 
 * @author John Pile
 *
 */
public class AlertLevel extends Ordinal {

	private static final OrdinalSet ordinals = new OrdinalSet();

	public static final AlertLevel DEBUG = new AlertLevel("DEBUG");
	public static final AlertLevel INFO = new AlertLevel("INFO");
	public static final AlertLevel ERROR = new AlertLevel("ERROR");
	public static final AlertLevel WARNING = new AlertLevel("WARNING");

	/**
	 * Construct an alert by name (DEBUG, INFO, ERROR, WARNING)
	 * @param name
	 */
	private AlertLevel(String name) {
		register(ordinals, name, this);
	}

	/**
	 * Iterate the list of available AlertLevel options.
	 */
	public Iterator iterator() {
		return ordinals.iterator();
	}

	/**
	 * Lookup ordinal by name.
	 * @param name
	 * @return AlertLevel matching name
	 */
	public static AlertLevel valueOf(String name) {
		Ordinal located = ordinals.get(name);
		if (located == null) {
			throw new IllegalArgumentException("No ordinal class "
					+ AlertLevel.class.getName() + "." + name);
		}
		return (AlertLevel) located;
	}

}
