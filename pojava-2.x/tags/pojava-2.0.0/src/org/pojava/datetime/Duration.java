package org.pojava.datetime;

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
 * Duration is a fixed measurement of time.
 * 
 * @author John Pile
 * 
 */
public class Duration implements Comparable<Duration> {

	/**
	 * A MILLISECOND = one thousandth of a second
	 */
	public static final long MILLISECOND = 1L;

	/**
	 * A SECOND = One second is the time that elapses during 9,192,631,770 cycles of the radiation produced by the transition between two levels of the cesium 133 atom... rounded to some margin of error by your less accurate system clock.
	 */
	public static final long SECOND = 1000L * MILLISECOND;

	/**
	 * A MINUTE = 60 seconds
	 */
	public static final long MINUTE = 60L * SECOND;

	/**
	 * An HOUR = 60 minutes
	 */
	public static final long HOUR = 60L * MINUTE;

	/**
	 * A DAY = 24 hours (for a variable day, see CalendarUnit)
	 */
	public static final long DAY = 24L * HOUR;

	/**
	 * A WEEK = a fixed set of seven 24-hour days
	 */
	public static final long WEEK = 7L * DAY;
	
	private static final int NANOS_PER_MILLI=1000000;

	/** Non-leap Milliseconds since an epoch */
	protected long millis = 0;

	/** Nanoseconds used by high-resolution time stamps */
	protected int nanos = 0;

	/**
	 * Constructor for a duration of zero.
	 */
	public Duration() {
		// Default is zero duration
	}

	/**
	 * Duration specified in milliseconds.
	 * @param millis
	 */
	public Duration(long millis) {
		this.millis = millis;
		if (millis<0)
			this.nanos=1000000000 + (int) (millis%SECOND) * NANOS_PER_MILLI;
		else
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
		if (this.millis<0 && this.nanos>0) this.millis-=1000;
	}

	/**
	 * Add a duration, producing a new duration.
	 * 
	 * @param dur
	 * @return A newly calculated Duration.
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
	 * @return A newly calculated Duration.
	 */
	public Duration add(long milliseconds) {
		Duration d=new Duration(this.toMillis()+milliseconds);
		d.nanos+=this.nanos%1000000;
		if (d.nanos>1000000000) {
			d.nanos-=1000000000;
			d.millis+=1000;
		} else if (d.nanos<0) {
			d.nanos+=1000000000;
			d.millis-=1000;
		}
		return d;
	}

	/**
	 * Add seconds and nanoseconds to a Duration, producing a new Duration.
	 * 
	 * @param seconds
	 * @param nanos
	 * @return A newly calculated Duration.
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
	 * @return -1, 0, or 1 of left compared to right.
	 */
	public int compareTo(Duration other) {
		if (other == null) {
			throw new NullPointerException("Cannot compare Duration to null.");
		}
		if (this.millis == other.millis) {
			return nanos < other.nanos ? -1 : nanos == other.nanos ? 0
					: 1;
		}
		return this.millis < other.millis ? -1 : 1;
	}
	
	/**
	 * Two durations are equal if internal values are identical.
	 * @param other is a Duration or derived object
	 * @return True if durations match.
	 */
	public boolean equals(Duration other) {
		return (this.millis==other.toMillis() && this.nanos==other.getNanos());
	}

	/**
	 * Return fractional seconds in nanoseconds<br/> Sign of value will match
	 * whole time value.
	 * 
	 * @return Sub-second portion of Duration specified in nanoseconds.
	 */
	public int getNanos() {
		return nanos;
	}

	/**
	 * Return time truncated to milliseconds
	 * 
	 * @return Number of whole milliseconds in Duration.
	 */
	public long toMillis() {
		return millis;
	}

	/**
	 * Return duration truncated seconds.
	 * 
	 * @return Number of whole seconds in Duration.
	 */
	public long getSeconds() {
		return millis / 1000 - (nanos < 0 ? 1 : 0);
	}

}
