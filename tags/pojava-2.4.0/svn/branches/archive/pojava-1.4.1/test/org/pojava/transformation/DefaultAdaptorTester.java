package org.pojava.transformation;

import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;

import org.pojava.datetime.DateTime;
import org.pojava.lang.Binding;

public class DefaultAdaptorTester extends TestCase {

	public void testDateTimeCase() {
		BindingAdaptor adaptor=new DefaultAdaptor();
		Binding local=new Binding(DateTime.class, new DateTime(123));
		Binding remote=new Binding(Timestamp.class, new Timestamp(123));
		Binding adapted=adaptor.inbound(remote);
		assertEquals(local.getObj(), adapted.getObj());
		adapted=adaptor.outbound(local);
		assertEquals(remote.getObj(), adapted.getObj());
	}
	
	public void testUtilDateCase() {
		BindingAdaptor adaptor=new DefaultAdaptor();
		Binding local=new Binding(Date.class, new Date(123));
		Binding remote=new Binding(Timestamp.class, new Timestamp(123));
		Binding adapted=adaptor.inbound(remote);
		assertEquals(123, ((DateTime)adapted.getObj()).toMillis());
		adapted=adaptor.outbound(local);
		assertEquals(123, ((Timestamp)adapted.getObj()).getTime());
	}
	
	public void testBooleanCase() {
		BindingAdaptor adaptor=new DefaultAdaptor();
		Binding local=new Binding(Boolean.class, Boolean.TRUE);
		Binding remote=new Binding(Boolean.class, Boolean.TRUE);
		Binding adapted=adaptor.inbound(remote);
		assertEquals(local.getObj(), adapted.getObj());
		adapted=adaptor.outbound(local);
		assertEquals(remote.getObj(), adapted.getObj());
	}
	
	public void testNullCase() {
		BindingAdaptor adaptor=new DefaultAdaptor();
		assertEquals(null, adaptor.inbound(new Binding(String.class, null)).getObj());
		// Look out... if we can't determine type, we just return null.
		assertEquals(null, adaptor.outbound(null));
	}
}
