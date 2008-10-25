package org.pojava.persistence.processor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

public class CharAdaptor implements BindingAdaptor {

	public Binding inbound(Binding inBinding) {
		Binding outBinding=inBinding;
		if (inBinding==null) return null;
		if (inBinding.getObj()==null) {
			return outBinding;
		}
		if (String.class.equals(inBinding.getType())) {
			outBinding.setObj(new Character(((String)inBinding.getObj()).charAt(0)));
		}
		return outBinding;
	}

	public Binding outbound(Binding obj) {
		return obj;
	}
}
