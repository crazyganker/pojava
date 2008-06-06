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

import org.pojava.validation.Alert;
import org.pojava.validation.AlertLevel;
import org.pojava.validation.ValidationException;

/**
 * Duration is the foundation for measurements of time within Current Era.
 * 
 * @author John Pile
 * 
 */
public class Duration {

	public static final long MILLISECOND = 1L;

	public static final long SECOND = 1000L * MILLISECOND;

	public static final long MINUTE = 60L * SECOND;

	public static final long HOUR = 60L * MINUTE;

	public static final long DAY = 24L * HOUR;

	public static final long WEEK = 7L * DAY;

	/** Non-leap Milliseconds since an epoch */
	protected long millis = 0;

	/** Nanoseconds used by high-resolution timestamps */
	protected int nanos = 0;

	/**
	 * 
	 */
	public Duration() {
		// Default is zero duration
	}

	private Duration(long millis) {
		this.millis = millis;
	}

	public Duration(long seconds, int nanos) {
		if (nanos < -999999999 || nanos > 999999999) {
			throw new ValidationException(new Alert(AlertLevel.ERROR,
					"Nanos may not exceed +/- 999999999."));
		}
		boolean isNegative = (seconds < 0 || seconds == 0 && nanos < 0);
		if (isNegative && nanos > 0) {
			nanos = -nanos;
		}
		this.millis = seconds * 1000 + nanos / 1000000;
		this.nanos = nanos;
	}

	/**
	 * Constructs a duration in milliseconds;
	 * 
	 * @param millis
	 * @return
	 */
	public static Duration newInstance(long millis) {
		return new Duration(millis);
	}

	/**
	 * Constructs a duration in seconds and fractional seconds expressed in
	 * nanos. It is possible to input the same values two different ways. For
	 * example, 1.75 seconds could be (1,750000000) or (2,-250000000). The
	 * result will be stored using the same sign for both seconds and nanos.
	 * 
	 * @param seconds
	 *            Integral seconds.
	 * @param nanos
	 *            Fractional seconds expressed in allowed range of +/- 999999999
	 *            nanoseconds.
	 * @return
	 */
	public static Duration newInstance(long seconds, int nanos) {
		return new Duration(seconds, nanos);
	}

	/**
	 * Return fractional seconds in nanoseconds<br/> Sign of value will match
	 * whole time value.
	 * 
	 * @return
	 */
	public int getNanos() {
		return nanos;
	}

	/**
	 * Return time truncated to milliseconds
	 * 
	 * @return
	 */
	public long getMillis() {
		return millis;
	}

	/**
	 * Return duration truncated seconds.
	 * 
	 * @return
	 */
	public long getSeconds() {
		if (millis < 0 && millis % 1000 != 0) {
			return millis / 1000 - 1;
		}
		return millis / 1000;
	}

}
