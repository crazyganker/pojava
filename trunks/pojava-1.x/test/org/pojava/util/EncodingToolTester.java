package org.pojava.util;

import junit.framework.TestCase;

public class EncodingToolTester extends TestCase {
	
	private static final boolean DEBUG=false;

	/**
	 * This reference message was taken from http://en.wikipedia.org/wiki/Base64 
	 */
	String encoded = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz"
			+ "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg"
			+ "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu"
			+ "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo"
			+ "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
	
	String decoded = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
	
	public void testBase64Decode() {
		String test=new String(EncodingTool.base64Decode(encoded.toCharArray()));
		assertEquals(decoded, test);
	}
	
	public void testBase64Encode() {
		String test=new String(EncodingTool.base64Encode(decoded.getBytes()));
		assertEquals(encoded, test);
	}
	
	/**
	 * Base64 encodes in 24-bit chunks, padding where needed to reach 24 bits.
	 * This test tries various lengths to ensure it is padded appropriately.
	 */
	public void testBase64Padding() {
		for (int i=0; i<10; i++) {
			StringBuffer sb=new StringBuffer();
			for (int j=0; j<i; j++) {
				sb.append(j);
			}
			char[] encoded=EncodingTool.base64Encode(sb.toString().getBytes());
			String decoded=new String(EncodingTool.base64Decode(encoded));
			assertEquals(sb.toString(), decoded);
		}
	}
	
	public void testHexEncode() {
		byte[] oneByte=new byte[1];
		String[] strings=new String[256];
		for (int i=0; i<256; i++) {
			oneByte[0]=(byte)i;
			strings[i]=EncodingTool.hexEncode(oneByte);
		}
		// Test for all 256 possible combinations in alphabetical order.
		assertTrue(strings[0].equals("00"));
		for (int i=1; i<256; i++) {
			assertTrue(strings[i].compareTo(strings[i-1])>0);
			assertTrue(strings[i].matches("^[a-f0-9]{2}$"));
		}
	}

	public void testHexDecode() {
		String example="00a1B2c3ff";
		byte[] decoded=EncodingTool.hexDecode(example);
		assertEquals((byte)0x00, decoded[0]);
		assertEquals((byte)0xa1, decoded[1]);
		assertEquals((byte)0xb2, decoded[2]);
		assertEquals((byte)0xc3, decoded[3]);
		assertEquals((byte)0xff, decoded[4]);
	}

	public void testHexDecodeNullString() {
		String s=null;
		byte[] decoded=EncodingTool.hexDecode(s);
		assertEquals(0, decoded.length);
	}
	
	public void testHexDecodeInvalidStrings() {
		try {
			EncodingTool.hexDecode("zz");
			fail("Expecting IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			if (DEBUG) {
				System.out.println(ex.getMessage());
			}
		}
		try {
			EncodingTool.hexDecode("bad"); // Uneven length
			fail("Expecting IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			if (DEBUG) {
				System.out.println(ex.getMessage());
			}
		}
	}

}
