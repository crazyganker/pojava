package org.pojava.lang;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Accessors {

	Class<?> type;
	Map<String, Method> getters=new HashMap<String, Method>();
	Map<String, Method> setters=new HashMap<String, Method>();
	public Class<? extends Object> getType() {
		return type;
	}
	public void setType(Class<? extends Object> type) {
		this.type = type;
	}
	public Map<String, Method> getGetters() {
		return getters;
	}
	public Map<String, Method> getSetters() {
		return setters;
	}
	
}
