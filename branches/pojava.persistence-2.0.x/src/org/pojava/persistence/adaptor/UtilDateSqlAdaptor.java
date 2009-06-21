package org.pojava.persistence.adaptor;

import java.sql.Timestamp;
import java.util.Date;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a Timestamp or Date value mapped to a
 * DateTime value.
 * 
 * @author John Pile
 * 
 */
public class UtilDateSqlAdaptor extends BindingAdaptor<Date,Timestamp> {

	/**
	 * The type the translator will produce for the bean.
	 */
	public Class<Date> inboundType() {
		return Date.class;
	}

	/**
	 * The type the translator will produce for the JDBC driver.
	 */
	public Class<Timestamp> outboundType() {
		return Timestamp.class;
	}

	/**
	 * Translate the binding from the data source towards Java bean.
	 */
	public Binding<Date> inbound(Binding<Timestamp> inBinding) {
		// Prevent constructing a new object when you can.
		if (inBinding == null)
			return null;
		Binding<Date> outBinding=new Binding<Date>(Date.class, null);
		if (inBinding.getObj() == null) {
			return outBinding;
		}
		outBinding.setValue(new Date(((Date) inBinding.getObj()).getTime()));
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding<Timestamp> outbound(Binding<Date> outBinding) {
		// Prevent constructing a new object when you can.
		if (outBinding == null)
			return null;
		Binding<Timestamp> inBinding=new Binding<Timestamp>(Timestamp.class, null);
		if (outBinding.getObj() == null) {
			return inBinding;
		}
		inBinding.setValue(new Timestamp(outBinding.getValue().getTime()));
		return inBinding;
	}
}
