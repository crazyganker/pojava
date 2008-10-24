package org.pojava.transformation;
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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.pojava.datetime.DateTime;
import org.pojava.lang.Binding;

/**
 * The DefaultAdaptor passes most data through directly, but performs
 * some specialized transformations on date types.
 * 
 * @author John Pile
 *
 */
public class DefaultAdaptor implements BindingAdaptor {

	public Binding inbound(Binding inBinding) {
		Binding outBinding=inBinding;
		if (inBinding==null) return null;
		if (inBinding.getObj()==null) {
			return outBinding;
		}
		if (Time.class.equals(inBinding.getType())) {
			return outBinding;
		}
		if (Date.class.isAssignableFrom(inBinding.getObj().getClass())) {
			outBinding=new Binding(DateTime.class, new DateTime(((Date)inBinding.getObj()).getTime()));
		}
		return outBinding;
	}

	public Binding outbound(Binding outBinding) {
		Binding inBinding=outBinding;
		if (outBinding==null) return null;
		if (outBinding.getObj()==null) {
			inBinding=new Binding(Timestamp.class, null);
		}
		if (Time.class==outBinding.getType()) {
			return inBinding;
		}
		if (outBinding.getType().equals(DateTime.class)) {
			inBinding=new Binding(Timestamp.class, ((DateTime)outBinding.getObj()).getTimestamp());
		}
		if (Date.class.isAssignableFrom(outBinding.getType())) {
			inBinding=new Binding(Timestamp.class, new Timestamp(((Date)outBinding.getObj()).getTime()));
		}
		return inBinding;
	}

}
