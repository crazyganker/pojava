package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type long.
 * 
 * @author John Pile
 * 
 */
public class LongAdaptor extends BindingAdaptor<Long,Long> {

	/**
	 * The type the translator will produce for the bean.
	 */
	public Class<Long> inboundType() {
		return Long.class;
	}

	/**
	 * The type the translator will produce for the JDBC driver.
	 */
	public Class<Long> outboundType() {
		return Long.class;
	}

	/**
	 * Translate the binding from the data source towards Java bean.
	 */
	public Binding<Long> inbound(Binding<Long> inBinding) {
		if (inBinding == null || inBinding.getObj()==null || inBinding.getObj().getClass()==Long.class) {
			return inBinding;
		}
		Binding<Long> outBinding=new Binding<Long>(Long.class, null);
		outBinding.setObj(new Long(inBinding.getObj().toString()));
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding<Long> outbound(Binding<Long> outBinding) {
		return outBinding;
	}
}
