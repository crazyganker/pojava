package org.pojava.persistence.factories;

import java.util.Date;
import java.util.Map;

public class DateFactory implements SerialFactory {

	public Object construct(Class type, Object[] params) {
		if (Date.class.isAssignableFrom(type)) {
			if (params.length == 1) {
				return new Date(Long.parseLong(params[0].toString()));
			}
		}
		return null;
	}

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

	public String serialize(Object obj) {
		if (Date.class.isAssignableFrom(obj.getClass())) {
			Date d = (Date) obj;
			return new Long(d.getTime()).toString();
		}
		return "";
	}

}
