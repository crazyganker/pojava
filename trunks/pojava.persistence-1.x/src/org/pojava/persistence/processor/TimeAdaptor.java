package org.pojava.persistence.processor;

import java.sql.Time;
import java.util.Date;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

public class TimeAdaptor implements BindingAdaptor {

	public Binding inbound(Binding inBinding) {
		Binding outBinding=inBinding;
		if (inBinding==null) return null;
		if (inBinding.getObj()==null) {
			return outBinding;
		}
		outBinding.setObj(new Time(((Date)inBinding.getObj()).getTime()%86400000));
		return outBinding;
	}

	public Binding outbound(Binding obj) {
		return obj;
	}
}
