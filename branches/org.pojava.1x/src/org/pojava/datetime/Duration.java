package org.pojava.datetime;

import java.security.InvalidParameterException;

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

/**
 * Duration is the foundation for measurements of time within Current Era.
 * 
 * @author John Pile
 * 
 */
public class Duration implements Comparable {

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

	public Duration(long millis) {
		this.millis = millis;
	}

	/**
	 * Seconds + nanos pair will always be adjusted so that nanos is positive.
	 * 
	 * @param seconds
	 * @param nanos
	 */
	public Duration(long seconds, int nanos) {
		while (nanos > 999999999) {
			seconds++;
			nanos -= 1000000000;
		}
		while (nanos < 0) {
			seconds--;
			nanos += 1000000000;
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
	 * result will be recalculated to always use positive values for nanos.
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

	public Duration add(Duration dur) {
		long calcSeconds;
		int calcNanos;
		if (dur.getNanos() > 0 && this.nanos > 0) {
			calcSeconds = this.getSeconds() + dur.getSeconds() + 1;
			calcNanos = this.nanos + (dur.getNanos() - 1000000000);
		} else if (dur.getNanos() < 0 && this.nanos < 0) {
			calcSeconds = this.getSeconds() + dur.getSeconds() - 1;
			calcNanos = this.nanos + (dur.getNanos() + 1000000000);
		} else {
			calcSeconds = this.getSeconds() + dur.getSeconds();
			calcNanos = this.nanos + dur.getNanos();
		}
		return new Duration(calcSeconds, calcNanos);
	}

	public Duration add(long milliseconds) {
		return new Duration(this.getSeconds() + milliseconds / 1000, this.nanos
				+ (int) ((milliseconds%1000) * 1000000));
	}

	/**
	 * Seconds + nanos pair is adjusted for safe addition.
	 * 
	 * @param seconds
	 * @param nanos
	 */
	public Duration add(long seconds, int nanos) {
		// Adjust to safe range to prevent overflow.
		while (nanos > 1000000000) {
			seconds++;
			nanos -= 1000000000;
		}
		while (nanos < -1000000000) {
			seconds--;
			nanos += 1000000000;
		}
		return new Duration(this.getSeconds() + seconds, this.nanos + nanos);
	}

	/**
	 * Return relative comparison between two Durations.
	 */
	public int compareTo(Object other) {
		if (other == null) {
			throw new NullPointerException("Cannot compare DateTime to null.");
		}
		if (other.getClass() != DateTime.class) {
			throw new InvalidParameterException(
					"Cannot compare DateTime type to "
							+ other.getClass().getName());
		}
		Duration otherDur = (Duration) other;
		if (this.millis == otherDur.millis) {
			return nanos < otherDur.nanos ? -1 : nanos == otherDur.nanos ? 0
					: 1;
		}
		return this.millis < otherDur.millis ? -1 : 1;
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
