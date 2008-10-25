package org.pojava.persistence.processor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

public class DoubleAdaptor implements BindingAdaptor {

	public Binding inbound(Binding inBinding) {
		Binding outBinding=inBinding;
		if (inBinding==null) return null;
		if (inBinding.getObj()==null) {
			return outBinding;
		}
		outBinding.setObj(new Double(inBinding.getObj().toString()));
		return outBinding;
	}

	public Binding outbound(Binding obj) {
		return obj;
	}
}
