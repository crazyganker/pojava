package org.pojava.lang;

import java.util.HashSet;
import java.util.Set;

public class Accessors {

	Class type;
	Set getters=new HashSet();
	Set setters=new HashSet();
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}
	public Set getGetters() {
		return getters;
	}
	public Set getSetters() {
		return setters;
	}
	
}
