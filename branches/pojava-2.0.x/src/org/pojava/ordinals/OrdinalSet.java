package org.pojava.ordinals;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides storage for an ordinal set of values.
 * 
 * @author John Pile
 * 
 */
public class OrdinalSet {

	private List<Ordinal> list = new ArrayList<Ordinal>();
	private Map<String, Ordinal> map = new HashMap<String, Ordinal>();

	protected void add(String name, Ordinal obj) {
		list.add(obj);
		map.put(name, obj);
	}

	public Ordinal get(String name) {
		return (Ordinal) map.get(name);
	}

	public Iterator<Ordinal> iterator() {
		return list.iterator();
	}
}
