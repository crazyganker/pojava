package org.pojava.lang;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class BindingTester extends TestCase {

	public void testAccessors() {
		Binding<String> binding=new Binding<String>(String.class, "tada");
		assertEquals(String.class, binding.getType());
		assertEquals("tada", binding.getObj());
	}


	public void testNulledAccessors() {
		Binding<Long> binding=new Binding<Long>(null, 1234L);
		binding.setObj(null);
		assertEquals(null, binding.getObj());
		assertEquals(null, binding.getType());
	}
	
	public void testBindingList() {
		List<UncheckedBinding> bindingList=new ArrayList<UncheckedBinding>();
		bindingList.add(new Binding<String>(String.class, "new"));
		bindingList.add(new Binding<Long>(Long.class, 123L));
	}
}
