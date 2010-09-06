package org.pojava.util;

import junit.framework.TestCase;

public class ProcessToolTester extends TestCase {

	/**
	 * Hopefully, java is on your PATH whether in Windows or Unix/Linux/Mac.
	 * 
	 * @throws Exception
	 */
	public void testSet() throws Exception {
		StringBuffer stdout=new StringBuffer();
		StringBuffer stderr=new StringBuffer();
		ProcessTool.exec("java", stdout, stderr);
		assertTrue(stdout.length()>1000);
		assertEquals(0, stderr.length());
	}
}
