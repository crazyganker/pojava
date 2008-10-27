package org.pojava.persistence.adaptor;

import java.sql.Time;
import java.util.Date;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Process a Time value from a ResultSet. This ensures that the hidden date
 * portion of the Time value is set to 1/1/1970.
 * 
 * @author John Pile
 * 
 */
public class TimeAdaptor implements BindingAdaptor {

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
		outBinding.setObj(new Time(
				((Date) inBinding.getObj()).getTime() % 86400000));
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding outbound(Binding obj) {
		return obj;
	}
}
