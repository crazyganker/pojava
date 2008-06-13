package org.pojava.util;

import junit.framework.TestCase;

public class StringToolTester extends TestCase {

	public void testIsInteger() {
		assertTrue(StringTool.isInteger("0"));
		assertTrue(StringTool.isInteger("-123456"));
		assertTrue(StringTool.isInteger("999"));
		assertFalse(StringTool.isInteger(null));
		assertFalse(StringTool.isInteger(""));
		assertFalse(StringTool.isInteger("-123-"));
		assertFalse(StringTool.isInteger(""));
		assertFalse(StringTool.isInteger("one"));
		assertFalse(StringTool.isInteger("1.23"));
		assertFalse(StringTool.isInteger("123Four"));
	}

	public void testOnlyDigits() {
		assertTrue(StringTool.onlyDigits("123412341234"));
		assertTrue(StringTool.onlyDigits("0"));
		assertFalse(StringTool.onlyDigits("-123"));
		assertFalse(StringTool.onlyDigits(null));
		assertFalse(StringTool.onlyDigits("A1B2"));
		assertFalse(StringTool.onlyDigits("1A"));
	}

	public void testParsIntFragment() {
		assertEquals(123, StringTool.parseIntFragment("123"));
		assertEquals(123, StringTool.parseIntFragment("123.45"));
		assertEquals(321, StringTool.parseIntFragment("321Penguins"));
		assertEquals(-4, StringTool.parseIntFragment("-4M"));
		assertEquals(18, StringTool.parseIntFragment("18:14"));
		assertEquals(0, StringTool.parseIntFragment(null));
	}

	public void startsWithDigit() {
		assertTrue(StringTool.onlyDigits("-123"));
		assertTrue(StringTool.onlyDigits("123412341234"));
		assertFalse(StringTool.onlyDigits(null));
		assertFalse(StringTool.onlyDigits("A1B2"));
		assertFalse(StringTool.onlyDigits("1A"));
	}

}
