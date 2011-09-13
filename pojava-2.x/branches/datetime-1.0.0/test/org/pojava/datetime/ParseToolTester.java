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

    public void testCapitalize() {
        assertEquals("Hey, Mo", ParseTool.capitalize("hey, Mo"));
        assertEquals("Hey, mo", ParseTool.capitalize("hey, mo"));
    }

    public void testCamelFromUnderscore() {
        assertEquals("iAmACamel", ParseTool.camelFromUnderscore("i_am_a_camel"));
        assertEquals("iAmACamel", ParseTool.camelFromUnderscore("I_am_a_Camel"));
    }

    public void testUnderscoreFromCamel() {
        assertEquals("under_dog", ParseTool.underscoreFromCamel("underDog"));
        assertEquals("a1_and_a2", ParseTool.underscoreFromCamel("a1AndA2"));
    }

    public void testStripWhitespace() {
        assertEquals(
                "Vapidfoxpawszipquicklyundermybrightjar.",
                ParseTool
                        .stripWhitespace("   \tVapid fox paws\tzip quickly \t \tunder my bright jar .\n\n "));
    }

    public void testPad() {
        assertEquals("Ten       ", ParseTool.pad("Ten", 10));
        assertEquals(" eleven    ", ParseTool.pad(" eleven ", 11));
        // Pad does not truncate or trim.
        assertEquals("three ", ParseTool.pad("three ", 3));
    }
    
    public void testTrue() {
        assertEquals(true, ParseTool.isTrue("Yes"));
        assertEquals(true, ParseTool.isTrue("y"));
        assertEquals(true, ParseTool.isTrue("True"));
        assertEquals(true, ParseTool.isTrue("t"));
        assertEquals(true, ParseTool.isTrue("1"));
        assertEquals(false, ParseTool.isTrue("No"));
        assertEquals(false, ParseTool.isTrue("n"));
        assertEquals(false, ParseTool.isTrue("False"));
        assertEquals(false, ParseTool.isTrue("f"));
        assertEquals(false, ParseTool.isTrue("0"));
        assertEquals(false, ParseTool.isTrue("z"));
        assertEquals(false, ParseTool.isTrue(""));
        assertEquals(false, ParseTool.isTrue(null));
    }
    
    public void testParseCommandQuotes() {
    	String[] cmd=ParseTool.parseCommand("useradd \"Joshua Timothy\"");
    	assertEquals("useradd", cmd[0]);
    	assertEquals("Joshua Timothy", cmd[1]);
    	cmd=ParseTool.parseCommand("promote \"Jacob  Andrew\" to  Captain");
    	assertEquals("promote", cmd[0]);
    	assertEquals("Jacob  Andrew", cmd[1]);
    	assertEquals("to", cmd[2]);
    	assertEquals("Captain", cmd[3]);
    }
    
    public void testParseCommandApostrophes() {
    	String[] cmd=ParseTool.parseCommand("promote 'Jacob's [sic] dog to Captain");
    	assertEquals("promote", cmd[0]);
    	assertEquals("Jacobs", cmd[1]);
    	assertEquals("[sic]", cmd[2]);
    	assertEquals("dog", cmd[3]);
    	assertEquals("to", cmd[4]);
    	assertEquals("Captain", cmd[5]);
    	cmd=ParseTool.parseCommand("promote \"Jacob's dog\" to Captain");
    	assertEquals("promote", cmd[0]);
    	assertEquals("Jacob's dog", cmd[1]);
    	assertEquals("to", cmd[2]);
    	assertEquals("Captain", cmd[3]);
    	try {
    		cmd=ParseTool.parseCommand("My shell wouldn't tolerate this");
    		fail("Uncaught open single quote.");
    	} catch (IllegalArgumentException ex) {
    		assertEquals("Unclosed quotes in argument.", ex.getMessage());
    	}
    }
    
    public void testParseCommandBacktics() {
    	String[] cmd=ParseTool.parseCommand("dir poj-`date +%d`.log");
    	assertEquals("dir", cmd[0]);
    	assertEquals("poj-`date +%d`.log", cmd[1]);
    }
}
