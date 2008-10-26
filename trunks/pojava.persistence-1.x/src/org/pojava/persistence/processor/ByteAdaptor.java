package org.pojava.persistence.processor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a Byte value.
 * 
 * @author John Pile
 * 
 */
public class ByteAdaptor implements BindingAdaptor {

	/**
	 * Translate the binding from the data source towards Java bean.
	 */
	public Binding inbound(Binding inBinding) {
		Binding outBinding = inBinding;
		if (inBinding == null)
			return null;
		if (inBinding.getObj() == null) {
			return outBinding;
		}
		outBinding.setObj(new Byte(inBinding.getObj().toString()));
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding outbound(Binding obj) {
		return obj;
	}
}
