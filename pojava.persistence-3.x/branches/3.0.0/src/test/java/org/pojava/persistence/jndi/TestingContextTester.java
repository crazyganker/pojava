package org.pojava.persistence.jndi;

import junit.framework.TestCase;


public class TestingContextTester extends TestCase {

    public void testLookup() throws Exception {
        TestingContext tc = new TestingContext();
        tc.bind("name", "obj");
        assertEquals("obj", tc.lookup("name"));
    }

    public void testRename() throws Exception {
        TestingContext tc = new TestingContext();
        tc.bind("name", "obj");
        assertNull(tc.lookup("newName"));
        tc.rename("name", "newName");
        assertEquals("obj", tc.lookup("newName").toString());
        assertNull(tc.lookup("name"));
    }

    public void testUnbind() throws Exception {
        TestingContext tc = new TestingContext();
        tc.bind("name", "obj");
        assertTrue(tc.lookup("name") != null);
        tc.unbind("name");
        assertNull(tc.lookup("name"));
    }


}
