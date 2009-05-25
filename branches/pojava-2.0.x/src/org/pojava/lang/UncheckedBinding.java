package org.pojava.lang;
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
 * A Binding is representation of an object (including null) with its class.
 * An UncheckedBinding provides loose requirements for mixing multiple
 * binding types into the same list.
 * 
 * @author John Pile
 *
 */
public class UncheckedBinding {

	private Class<?> type;
	
	/**
	 * The obj holds an object or null described by the type.
	 */
	private Object obj;
	
	/**
	 * Construct a Binding from an object.
	 * @param type
	 * @param obj
	 */
	public <T>UncheckedBinding(Class<T> type, T obj) {
		this.type=type;
		this.obj=obj;
	}

	public Class<?> getType() {
		return this.type;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
}
