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
	
	private static final int NANOS_PER_MILLI=1000000;

	/** Non-leap Milliseconds since an epoch */
	protected long millis = 0;

	/** Nanoseconds used by high-resolution time stamps */
	protected int nanos = 0;

	/**
	 * 
	 */
	public Duration() {
		// Default is zero duration
	}

	public Duration(long millis) {
		this.millis = millis;
		this.nanos=(int) (millis%SECOND) * NANOS_PER_MILLI;
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
		this.millis = seconds * SECOND + nanos / NANOS_PER_MILLI;
		this.nanos = nanos;
	}

	/**
	 * Add a duration, producing a new duration.
	 * 
	 * @param dur
	 * @return
	 */
	public Duration add(Duration dur) {
		return new Duration(this.getSeconds() + dur.getSeconds(), this.nanos
				+ dur.nanos);
	}

	/**
	 * Add fixed number of (+/-) milliseconds to a Duration, producing a new
	 * Duration.
	 * 
	 * @param milliseconds
	 * @return
	 */
	public Duration add(long milliseconds) {
		return new Duration(this.getSeconds() + milliseconds / 1000, this.nanos
				+ (int) ((milliseconds % 1000) * 1000000));
	}

	/**
	 * Add to a Duration, producing a new Duration.
	 * 
	 * @param seconds
	 * @param nanos
	 */
	public Duration add(long seconds, int nanos) {
		// Adjust to safe range to prevent overflow.
		if (nanos > 999999999) {
			seconds++;
			nanos -= 1000000000;
		}
		return new Duration(this.getSeconds() + seconds, this.nanos + nanos);
	}

	/**
	 * Return relative comparison between two Durations.
	 */
	public int compareTo(Object other) {
		if (other == null) {
			throw new NullPointerException("Cannot compare Duration to null.");
		}
		if (!Duration.class.isAssignableFrom(other.getClass())) {
			throw new IllegalArgumentException(
					"Cannot compare Duration type to "
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
	 * Two durations are equal if internal values are identical.
	 * @param dur
	 * @return
	 */
	public boolean equals(Object other) {
		if (other.getClass().isAssignableFrom(Duration.class)) {
			Duration dur=(Duration)other;
			return (this.millis==dur.getMillis() && this.nanos==dur.getNanos());
		}
		return false;
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
		if (millis < 0 && (millis % 1000 != 0)) {
			return millis / 1000 - 1;
		}
		return millis / 1000;
	}

}
