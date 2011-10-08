package org.pojava.testing;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

public class JNDIRegistryTester extends TestCase {

    public void testInitialContext() throws Exception {
        Context ctx = new InitialContext();
        Context workingCtx = JNDIRegistry.getInitialContext();
        ctx = new InitialContext();
        assertEquals(null, ctx.lookup("example"));
        assertEquals(null, workingCtx.lookup("example"));
        ctx.bind("example", "fixed");
        assertEquals("fixed", ctx.lookup("example"));
        assertEquals("fixed", workingCtx.lookup("example"));
        workingCtx.bind("example", "same");
        assertEquals("same", ctx.lookup("example"));
        assertEquals("same", workingCtx.lookup("example"));
    }

    public void testInitialContextFactory() throws Exception {
        String factory = "org.pojava.testing.TestingContextFactory";
        Context ctx = JNDIRegistry.getInitialContext(factory);
        ctx.bind("example", "alt");
        assertEquals("alt", ctx.lookup("example"));
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

}
