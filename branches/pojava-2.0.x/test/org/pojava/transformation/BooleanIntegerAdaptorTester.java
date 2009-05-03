package org.pojava.transformation;

import junit.framework.TestCase;

import org.pojava.lang.Binding;

public class BooleanIntegerAdaptorTester extends TestCase {

	public void testCleanCase() {
		BindingAdaptor adaptor=new BooleanIntegerAdaptor();
		Binding local=new Binding(Boolean.class, Boolean.TRUE);
		Binding remote=new Binding(Integer.class, new Integer(1));
		Binding adapted=adaptor.inbound(remote);
		assertEquals(local.getObj(), adapted.getObj());
		adapted=adaptor.outbound(local);
		assertEquals(remote.getObj(), adapted.getObj());
	}
	
	public void testDirtyCase() {
		BindingAdaptor adaptor=new BooleanIntegerAdaptor();
		try {
			adaptor.inbound(new Binding(String.class, "invalid"));
			fail("Expecting IllegalStateException.");
		} catch (IllegalStateException ex) {
			assertEquals("BooleanIntegerAdaptor.inbound cannot interpret binding of type java.lang.String.", ex.getMessage());
		}
	}
	
	public void testNullCase() {
		BindingAdaptor adaptor=new BooleanIntegerAdaptor();
		assertEquals(null, adaptor.inbound(new Binding(String.class, null)).getObj());
		assertEquals(null, adaptor.outbound(null).getObj());
	}
}
