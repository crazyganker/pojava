package org.pojava.persistence.adaptor;

import java.sql.Timestamp;

import org.pojava.datetime.DateTime;
import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a Timestamp or Date value mapped to a
 * DateTime value.
 * 
 * @author John Pile
 * 
 */
public class DateTimeSqlAdaptor extends BindingAdaptor<DateTime,Timestamp> {

	/**
	 * The type the translator will produce for the bean.
	 */
	public Class<DateTime> inboundType() {
		return DateTime.class;
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
	public Binding<DateTime> inbound(Binding<Timestamp> inBinding) {
		// Prevent constructing a new object when you can.
		if (inBinding == null)
			return null;
		Binding<DateTime> outBinding=new Binding<DateTime>(DateTime.class, null);
		if (inBinding.getObj() == null) {
			return outBinding;
		}
		if (inBinding.getObj().getClass()==Timestamp.class) {
			outBinding.setValue(new DateTime((Timestamp) inBinding.getObj()));
		} else {
			outBinding.setValue(new DateTime(((java.util.Date)inBinding.getObj()).getTime()));
		}
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding<Timestamp> outbound(Binding<DateTime> outBinding) {
		// Prevent constructing a new object when you can.
		if (outBinding == null)
			return null;
		Binding<Timestamp> inBinding=new Binding<Timestamp>(Timestamp.class, null);
		if (outBinding.getObj() == null) {
			return inBinding;
		}
		inBinding.setValue(new Timestamp(outBinding.getValue().toMillis()));
		return inBinding;
	}
}
