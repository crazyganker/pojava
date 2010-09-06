package org.pojava.lang;

import junit.framework.TestCase;

public class BindingTester extends TestCase {

	public void testAccessors() {
		Binding binding=new Binding(String.class, "tada");
		assertEquals(String.class, binding.getType());
		binding.setType(Integer.class);
		binding.setObj(new Integer(42));
		assertEquals(Integer.class, binding.getType());
		assertEquals(new Integer(42), binding.getObj());
	}


	public void testNulledAccessors() {
		Binding binding=new Binding(String.class, "tada");
		binding.setType(null);
		binding.setObj(null);
		assertEquals(null, binding.getObj());
		assertEquals(null, binding.getType());
	}
}
