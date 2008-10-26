package org.pojava.persistence.processor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a Character value.
 * 
 * @author John Pile
 * 
 */
public class CharAdaptor implements BindingAdaptor {

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
		if (String.class.equals(inBinding.getType())) {
			outBinding.setObj(new Character(((String) inBinding.getObj())
					.charAt(0)));
		}
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding outbound(Binding obj) {
		return obj;
	}
}
