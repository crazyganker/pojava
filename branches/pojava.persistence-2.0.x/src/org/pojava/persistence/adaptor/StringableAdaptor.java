package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

public class StringableAdaptor extends BindingAdaptor {
	public Binding inbound(Binding inBinding) {
		// TODO Auto-generated method stub
		Binding outBinding=new Binding(String.class, inBinding.getObj().toString());
		outBinding.setObj(inBinding.getObj().toString());
		return outBinding;
	}

	public Binding outbound(Binding outBinding) {
		Binding inBinding=new Binding(String.class, outBinding.getObj());
		inBinding.setObj(outBinding.getObj().toString());
		return inBinding;
	}

	public Class inboundType() {
		return String.class;
	}

	public Class outboundType() {
		return String.class;
	}
}
