package org.pojava.persistence.util;

import junit.framework.TestCase;

public class WebSafeTester extends TestCase {

    private static final String TEST_KEY = "AES EgNhNcv0E/QYrWso04LreA==";

    public void testNormal() throws Exception {
        String orig="TEST";
        WebSafe websafe=new WebSafe(TEST_KEY);
        String str=websafe.objectToString(orig);
        // System.out.println(str);
        Object prodigal=websafe.stringToObject(str);
        assertEquals(orig, prodigal.toString());
    }    
    
}
