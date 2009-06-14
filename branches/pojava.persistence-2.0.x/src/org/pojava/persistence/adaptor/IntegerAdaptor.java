package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type integer.
 * 
 * @author John Pile
 * 
 */
public class IntegerAdaptor extends BindingAdaptor {

	/**
	 * The type the translator will produce for the bean.
	 */
	public Class inboundType() {
		return Integer.class;
	}

	/**
	 * The type the translator will produce for the JDBC driver.
	 */
	public Class outboundType() {
		return Integer.class;
	}

	/**
	 * Translate the binding from the data source towards Java bean.
	 */
	public Binding inbound(Binding inBinding) {
		Binding outBinding = new Binding(Integer.class, null);
		if (inBinding == null || inBinding.getObj()==null) {
			return outBinding;
		}
		if (inBinding.getObj().getClass().equals(Integer.class)) {
			outBinding.setObj(inBinding.getObj());
		} else {
			outBinding.setObj(new Integer(inBinding.getObj().toString()));
		}
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding outbound(Binding outBinding) {
		Binding inBinding = new Binding(Integer.class, null);
		if (outBinding == null || outBinding.getObj()==null) {
			return outBinding;
		}
		if (outBinding.getObj().getClass().equals(Integer.class)) {
			inBinding.setObj(outBinding.getObj());
		} else {
			inBinding.setObj(new Integer(outBinding.getObj().toString()));
		}
		return inBinding;
	}
}
