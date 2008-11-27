package org.pojava.persistence.adaptor;

import org.pojava.datetime.DateTime;
import org.pojava.lang.Binding;

/**
 * Adaptor for managing Java to JDBC for a java.sql.Date value mapped to a
 * DateTime value.
 * 
 * @author John Pile
 * 
 */
public class DateTimeSqlAdaptor implements TypedAdaptor {

	/**
	 * The type the translator will produce for the bean.
	 */
	public Class inboundType() {
		return java.sql.Date.class;
	}

	/**
	 * The type the translator will produce for the JDBC driver.
	 */
	public Class outboundType() {
		return DateTime.class;
	}

	/**
	 * Translate the binding from the data source towards Java bean.
	 */
	public Binding inbound(Binding inBinding) {
		// Prevent constructing a new object when you can.
		if (inBinding == null)
			return null;
		if (inBinding.getObj() == null) {
			return inBinding;
		}
		Binding outBinding;
		outBinding = new Binding(DateTime.class, new DateTime(
				((java.util.Date) inBinding.getObj()).getTime()));
		return outBinding;
	}

	/**
	 * Translate the binding from the java bean to the data source.
	 */
	public Binding outbound(Binding outBinding) {
		// Prevent constructing a new object when you can.
		if (outBinding == null)
			return null;
		if (outBinding.getObj() == null) {
			return outBinding;
		}
		Binding inBinding = new Binding(java.sql.Timestamp.class,
				new java.sql.Date(((DateTime) outBinding.getObj()).toMillis()));
		return inBinding;
	}
}
