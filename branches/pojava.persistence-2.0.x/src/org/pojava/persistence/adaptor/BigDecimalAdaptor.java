package org.pojava.persistence.adaptor;

import java.math.BigDecimal;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type double.
 * 
 * @author John Pile
 * 
 */
public class BigDecimalAdaptor extends BindingAdaptor {

	/**
	 * The type the translator will produce for the bean.
	 */
	public Class inboundType() {
		return BigDecimal.class;
	}

	/**
	 * The type the translator will produce for the JDBC driver.
	 */
	public Class outboundType() {
		return BigDecimal.class;
	}

	/**
	 * Translate the binding from the data source towards Java bean.
	 */
	public Binding inbound(Binding inBinding) {
		Binding outBinding = new Binding(BigDecimal.class, null);
		if (inBinding == null || inBinding.getObj() == null)
			return outBinding;
		if (inBinding.getObj().getClass().equals(BigDecimal.class)) {
			outBinding.setObj(inBinding.getObj());
			return outBinding;
		}
		outBinding.setObj(new BigDecimal(inBinding.getObj().toString()));
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding outbound(Binding obj) {
		return obj;
	}
}
