package org.pojava.lang;
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
 * A Binding is representation of an object with its class.
 * Its class can be inferred even when the object is null.
 * 
 * @author John Pile
 *
 */
public class Binding {

	private Class type;
	private Object obj;
	
	public Binding(Class type, Object obj) {
		this.type=type;
		this.obj=obj;
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
}
