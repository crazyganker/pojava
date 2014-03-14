package org.pojava.persistence.jndi;

import junit.framework.TestCase;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.IOException;
import java.util.Properties;

public class JNDIRegistryTester extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		JNDIRegistry.getInitialContext();
		super.setUp();
	}

    public void testInitialContext() throws Exception {
        Context ctx = new InitialContext();
        Context workingCtx = JNDIRegistry.getInitialContext();
        assertEquals(null, ctx.lookup("example"));
        assertEquals(null, workingCtx.lookup("example"));
        ctx.bind("example", "fixed");
        assertEquals("fixed", ctx.lookup("example"));
        assertEquals("fixed", workingCtx.lookup("example"));
        workingCtx.bind("example", "same");
        assertEquals("same", ctx.lookup("example"));
        assertEquals("same", workingCtx.lookup("example"));
    }
    
    public void testBind() throws Exception {
    	Context ctx = new InitialContext();
        ctx.bind("java:comp/env/something", "test");
        assertEquals("test", ctx.lookup("java:comp/env/something"));
    }

    public void testInitialContextFactory() throws Exception {
        String factory = "org.pojava.persistence.jndi.TestingContextFactory";
        Context ctx = JNDIRegistry.getInitialContext(factory);
        ctx.bind("example", "alt");
        assertEquals("alt", ctx.lookup("example"));
        ctx.unbind("example");
    }

    public void testBindUnbind() throws Exception {
        Context ctx = JNDIRegistry.getInitialContext();
        ctx.bind("test", "Value");
        assertEquals("Value", ctx.lookup("test"));
        ctx.unbind("test");
        assertEquals(null, ctx.lookup("test"));
    }

    public void testRebind() throws Exception {
        Context ctx = JNDIRegistry.getInitialContext();
        ctx.bind("test", "Value");
        assertEquals("Value", ctx.lookup("test"));
        ctx.rebind("test", "Other");
        assertEquals("Other", ctx.lookup("test").toString());
    }

    public void testRename() throws Exception {
        Context ctx = JNDIRegistry.getInitialContext();
        ctx.bind("test", "Value");
        assertEquals("Value", ctx.lookup("test"));
        ctx.rename("test", "rebound");
        assertEquals("Value", ctx.lookup("rebound").toString());
    }

    public void testUnsupported() throws Exception {
        Context ctx = JNDIRegistry.getInitialContext();
        try {
            ctx.addToEnvironment("test", "test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.createSubcontext("test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.getEnvironment();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.getNameInNamespace();
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.getNameParser("test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.list("test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.listBindings("test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.lookupLink("test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            ctx.removeFromEnvironment("test");
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }

    }

    public void testFetchPropertiesNoFile() throws Exception {
    	try {
    		JNDIRegistry.fetchProperties("nofile.txt");
    	} catch (IOException ex) {
    		assertTrue(ex.getMessage().contains("nofile.txt"));
    	}
    }

    public void testFetchProperties() throws Exception {
        Properties props=JNDIRegistry.fetchProperties("config/ds_test.properties");
        assertEquals(5, props.size());
    }

    public void testExtractDataSource() throws Exception {
        Properties props=JNDIRegistry.fetchProperties("config/ds_test.properties");
        DriverManagerDataSource ds=(DriverManagerDataSource) JNDIRegistry.extractDataSource(props, "pojava_test");
        assertEquals("pojava", ds.user);
        assertEquals("popojava", ds.password);
        assertEquals("jdbc:postgresql://localhost:5432/postgres", ds.url);
    }

    public void testDataSourceFromFile() throws Exception {
    	JNDIRegistry.registerDatasourcesFromFile("config/ds_test.properties");
    	DriverManagerDataSource ds=(DriverManagerDataSource)JNDIRegistry.lookupDataSource("pojava_test");
    	assertEquals("pojava", ds.user);
        assertEquals("popojava", ds.password);
        assertEquals("jdbc:postgresql://localhost:5432/postgres", ds.url);
    }
    
    public void testLookups() throws Exception {
    	Context ctx=new InitialContext();
    	String value="value";
    	ctx.bind("java:comp/env/example", value);
    	assertEquals(value, JNDIRegistry.lookupEnv("example"));
    	assertEquals(value, JNDIRegistry.lookupFullyQualified("java:comp/env/example"));
    }

}
