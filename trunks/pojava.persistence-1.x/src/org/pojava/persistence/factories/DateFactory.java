package org.pojava.persistence.factories;

import java.util.Date;
import java.util.Map;

/**
 * DateFactory provides serialization for the java.util.Date and its
 * derived objects up to millisecond precision.
 * 
 * @author John Pile
 */
public class DateFactory implements SerialFactory {

	/**
	 * Construct a java.util.Date object.
	 */
	public Object construct(Class type, Object[] params) {
		if (Date.class.isAssignableFrom(type)) {
			if (params.length == 1) {
				return new Date(Long.parseLong(params[0].toString()));
			}
		}
		return null;
	}

	/**
	 * Construct a java.util.Date object.
	 */
	public Object construct(Class type, Map params) {
		if (Date.class.isAssignableFrom(type)) {
			if (params.containsKey("time")) {
				return new Date(((Long) params.get("time")).longValue());
			}
			if (params.containsKey("millis")) {
				return new Date(((Long) params.get("millis")).longValue());
			}
		}
		return null;
	}

	/**
	 * Serialize a java.util.Date object to UTC millis.
	 */
	public String serialize(Object obj) {
		if (Date.class.isAssignableFrom(obj.getClass())) {
			Date d = (Date) obj;
			return new Long(d.getTime()).toString();
		}
		return "";
	}

}
