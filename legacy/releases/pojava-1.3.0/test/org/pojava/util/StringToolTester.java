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

	public void testParseIntFragment() {
		assertEquals(123, StringTool.parseIntFragment("123"));
		assertEquals(123, StringTool.parseIntFragment("123.45"));
		assertEquals(321, StringTool.parseIntFragment("321Penguins"));
		assertEquals(-4, StringTool.parseIntFragment("-4M"));
		assertEquals(18, StringTool.parseIntFragment("18:14"));
		assertEquals(0, StringTool.parseIntFragment(null));
	}

	public void testCapitalize() {
		assertEquals("Hey, Mo", StringTool.capitalize("hey, Mo"));
		assertEquals("Hey, mo", StringTool.capitalize("hey, mo"));
	}
	
	public void testCamelFromUnderscore() {
		assertEquals("iAmACamel", StringTool.camelFromUnderscore("i_am_a_camel"));
		assertEquals("iAmACamel", StringTool.camelFromUnderscore("I_am_a_Camel"));
	}

	public void testUnderscoreFromCamel() {
		assertEquals("under_dog", StringTool.underscoreFromCamel("underDog"));
		assertEquals("a1_and_a2", StringTool.underscoreFromCamel("a1AndA2"));
	}

	public void testStripWhitespace() {
		assertEquals("Vapidfoxpawszipquicklyundermybrightjar.",
				StringTool.stripWhitespace("   \tVapid fox paws\tzip quickly \t \tunder my bright jar .\n\n "));
	}
	
	public void testPad() {
		assertEquals("Ten       ", StringTool.pad("Ten", 10));
		assertEquals(" eleven    ", StringTool.pad(" eleven ", 11));
		// Pad does not truncate or trim.
		assertEquals("three ", StringTool.pad("three ", 3));
	}
}
