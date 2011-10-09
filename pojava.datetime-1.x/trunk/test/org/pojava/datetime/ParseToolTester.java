package org.pojava.datetime;

import junit.framework.TestCase;

public class ParseToolTester extends TestCase {

    public void testIsInteger() {
    	assertEquals(false, ParseTool.isInteger(null));
    	assertEquals(false, ParseTool.isInteger(""));
    	assertEquals(false, ParseTool.isInteger("A"));
    	assertEquals(false, ParseTool.isInteger("2B"));
    	assertEquals(true, ParseTool.isInteger("-1"));
    	assertEquals(true, ParseTool.isInteger("0"));
    	assertEquals(true, ParseTool.isInteger("2"));
    	assertEquals(true, ParseTool.isInteger("12"));
    }

    public void testOnlyDigits() {
    	assertEquals(false, ParseTool.onlyDigits(null));
    	assertEquals(false, ParseTool.onlyDigits("a"));
    	assertEquals(false, ParseTool.onlyDigits("-1"));
    	assertEquals(true, ParseTool.onlyDigits("1"));
    }

    public void testStartsWithDigit() {
    	assertEquals(false, ParseTool.startsWithDigit(null));
    }

    public void testParseIntFragment() {
    	assertEquals(0, ParseTool.parseIntFragment(null));
    	assertEquals(0, ParseTool.parseIntFragment("C"));
    	assertEquals(0, ParseTool.parseIntFragment("C3"));
    	assertEquals(-1, ParseTool.parseIntFragment("-1"));
    	assertEquals(-12, ParseTool.parseIntFragment("-12"));
    	assertEquals(2, ParseTool.parseIntFragment("2B"));
    }

}
