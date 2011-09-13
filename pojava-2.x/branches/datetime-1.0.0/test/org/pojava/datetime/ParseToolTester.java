package org.pojava.datetime;

import junit.framework.TestCase;

public class ParseToolTester extends TestCase {

    public void testIsInteger() {
        assertTrue(ParseTool.isInteger("0"));
        assertTrue(ParseTool.isInteger("-123456"));
        assertTrue(ParseTool.isInteger("999"));
        assertFalse(ParseTool.isInteger(null));
        assertFalse(ParseTool.isInteger(""));
        assertFalse(ParseTool.isInteger("-123-"));
        assertFalse(ParseTool.isInteger(""));
        assertFalse(ParseTool.isInteger("one"));
        assertFalse(ParseTool.isInteger("1.23"));
        assertFalse(ParseTool.isInteger("123Four"));
    }

    public void testOnlyDigits() {
        assertTrue(ParseTool.onlyDigits("123412341234"));
        assertTrue(ParseTool.onlyDigits("0"));
        assertFalse(ParseTool.onlyDigits("-123"));
        assertFalse(ParseTool.onlyDigits(null));
        assertFalse(ParseTool.onlyDigits("A1B2"));
        assertFalse(ParseTool.onlyDigits("1A"));
    }

    public void testParseIntFragment() {
        assertEquals(123, ParseTool.parseIntFragment("123"));
        assertEquals(123, ParseTool.parseIntFragment("123.45"));
        assertEquals(321, ParseTool.parseIntFragment("321Penguins"));
        assertEquals(-4, ParseTool.parseIntFragment("-4M"));
        assertEquals(18, ParseTool.parseIntFragment("18:14"));
        assertEquals(0, ParseTool.parseIntFragment(null));
    }

}

