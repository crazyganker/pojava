package org.pojava.ordinals;

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

/**
 * An ordinal is roughly equivalent to an Enum. Enums are great-- a good reason
 * to upgrade, really. If you still need backward compatibility with Java 1.4.2,
 * though, this can be a good alternative that can provide a smooth transition
 * to Enums in the future. <br/> <em>Example code:</em><br/>
 * 
 * <pre>
 * public class AlertLevel extends Ordinal {
 * 
 * 	private static final OrdinalSet ordinals = new OrdinalSet();
 * 
 * 	public static final AlertLevel INFO = new AlertLevel(&quot;INFO&quot;);
 * 	public static final AlertLevel WARNING = new AlertLevel(&quot;WARNING&quot;);
 * 	public static final AlertLevel ERROR = new AlertLevel(&quot;ERROR&quot;);
 * 
 * 	private AlertLevel(String name) {
 * 		register(ordinals, name, this);
 * 	}
 * 
 * 	public Iterator iterator() {
 * 		return ordinals.iterator();
 * 	}
 * 
 * }
 * </pre>
 * 
 * The above will behave much the same as:<br>
 * 
 * <pre>
 * public Enum AlertLevel {
 *  INFO, WARNING, ERROR
 * }
 * 
 * &#064;author John Pile
 * 
 */
public abstract class Ordinal implements Comparable {

	protected static int ct = 0;
	protected int pos = 0;
	protected String name;

	public abstract Iterator iterator();

	public int compareTo(Ordinal ord) {
		return this.pos - ord.pos;
	}

	public int compareTo(Object obj) {
		return compareTo((Ordinal) obj);
	}

	public boolean isLessThan(Ordinal ord) {
		return this.pos < ord.pos;
	}

	public boolean isGreaterThan(Ordinal ord) {
		return this.pos > ord.pos;
	}

	public boolean equals(Ordinal ord) {
		return this.pos == ord.pos;
	}

	public void register(OrdinalSet ordinals, String name, Ordinal ordinal) {
		this.pos = ct++;
		this.name = name;
		ordinals.add(name, ordinal);
	}

	public String toString() {
		return name;
	}

	public int ordinal() {
		return this.pos;
	}

}
