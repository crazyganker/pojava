package org.pojava.testing;

/*
 Copyright 2008-09 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * An implementation of a Context for the purpose of unit testing. Useful for constructing a
 * basic InitialContext that is similar enough to the one provided by Tomcat (for example) to
 * test code that depends upon the existence of an InitialContext.
 * 
 * This Context supports binding and the creation and use of subcontexts. For basic stuff like
 * registering a DataSource or Environment variable similar to one provided by server.xml, it
 * should work exactly as expected.
 * 
 * This is not a full implementation of a Context and will break down for certain operations
 * involving subcontexts. Most notably, a subcontext returned by a lookup(...) will yield a
 * snapshot view that is only partially synchronized with its parent context. It supports only a
 * one-way relationship where changes to the subcontext are visible to the parent, but not the
 * other way around.
 * 
 * 
 * @author John Pile
 * @deprecated As of 2.6.1 moving to pojava.persistence-2.6.0
 * 
 */
public class TestingContext implements Context {

    private Hashtable<String, Object> map = new Hashtable<String, Object>();

    private String prefix = null;

    private Context parentContext = null;

    /**
     * The syntax "java:/path" and "java:path" are both in popular use. This method normalizes
     * both to "java/path".
     * 
     * @param name
     * @return
     */
    private String normalized(String name) {
        return name.replaceAll(":/?", "/");
    }

    private String normalized(Name name) {
        return name.toString().replaceAll(":/?", "/");
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new UnsupportedOperationException(
                "addToEnvironment(String propName, Object propVal) Not implemented.");
    }

    /**
     * Register an object under a specific name. This synchronizes a parent from any child, but
     * not the other way around. Good enough for our purposes... just be aware of the
     * limitation.
     */
    public void bind(Name name, Object obj) throws NamingException {
        bind(normalized(name), obj);
        if (parentContext != null) {
            parentContext.bind(prefix + "/" + normalized(name), obj);
        }
    }

    /**
     * Register an object under a specific name. This synchronizes a parent from any child, but
     * not the other way around. Good enough for our purposes... just be aware of the
     * limitation.
     * 
     */
    public void bind(String name, Object obj) throws NamingException {
        map.put(normalized(name), obj);
        if (parentContext != null) {
            parentContext.bind(prefix + "/" + normalized(name), obj);
        }
    }

    public void close() throws NamingException {
        map = null;
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException(
                "composeName(Name name, Name prefix) Not implemented.");
    }

    public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException(
                "composeName(String name, String prefix) Not implemented.");
    }

    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("createSubcontext(Name name) Not implemented.");
    }

    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException(
                "createSubcontext(String name) Not implemented.");
    }

    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException("destroySubcontext(Name name) Not implemented.");
    }

    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException(
                "destroySubcontext(String name) Not implemented.");
    }

    public Hashtable<String, Object> getEnvironment() throws NamingException {
        throw new UnsupportedOperationException("getEnvironment() Not implemented.");
    }

    public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException("getNameInNamespace() Not implemented.");
    }

    public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException("getNameParser(Name name) Not implemented.");
    }

    public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException("getNameParser(String name) Not implemented.");
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        throw new UnsupportedOperationException("list(Name name) Not implemented.");
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        throw new UnsupportedOperationException("list(String name) Not implemented.");
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException("listBindings(Name name) Not implemented.");
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException("listBindings(String name) Not implemented.");
    }

    /**
     * Retrieve an object registered under a specific name.
     */
    public Object lookup(Name name) throws NamingException {
        return lookup(normalized(name));
    }

    /**
     * Retrieve an object registered under a specific name. This shortcuts some of the work
     * necessary to support more complex and mature implementations by returning only a snapshot
     * of the requested subcontext.
     * 
     * It's likely good enough to do everything we need for unit testing, but not good enough to
     * serve as a production implementation of a Context.
     */
    public Object lookup(String name) throws NamingException {
        name = normalized(name);
        Object target = map.get(name);
        if (target == null) {
            Hashtable<String, Object> submap = new Hashtable<String, Object>();
            for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                if (key.startsWith(name + "/")) {
                    submap.put(key.substring(name.length() + 1), map.get(key));
                }
            }
            if (submap.size() > 0) {
                TestingContext ctx = new TestingContext();
                ctx.map = submap;
                ctx.parentContext = this;
                ctx.prefix = name;
                target = ctx;
            }
        }
        return target;
    }

    public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException("lookupLink(Name name) Not implemented.");
    }

    public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException("lookupLink(String name) Not implemented.");
    }

    public void rebind(Name name, Object obj) throws NamingException {
        bind(name, obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        bind(name, obj);
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException(
                "removeFromEnvironment(String propName) Not implemented.");
    }

    /**
     * This synchronizes a parent from any child, but not the other way around. Good enough for
     * our purposes... just be aware of the limitation.
     */
    public void rename(Name oldName, Name newName) throws NamingException {
        map.put(normalized(newName), map.remove(normalized(oldName)));
        if (parentContext != null) {
            parentContext.rename(prefix + "/" + normalized(oldName), prefix + "/"
                    + normalized(newName));
        }
    }

    /**
     * This synchronizes a parent from any child, but not the other way around. Good enough for
     * our purposes... just be aware of the limitation.
     */
    public void rename(String oldName, String newName) throws NamingException {
        map.put(normalized(newName), map.remove(normalized(oldName)));
        if (parentContext != null) {
            parentContext.rename(prefix + "/" + normalized(oldName), prefix + "/"
                    + normalized(newName));
        }
    }

    /**
     * This synchronizes a parent from any child, but not the other way around. Good enough for
     * our purposes... just be aware of the limitation.
     */
    public void unbind(Name name) throws NamingException {
        map.remove(normalized(name));
        if (parentContext != null) {
            parentContext.unbind(prefix + "/" + normalized(name));
        }
    }

    /**
     * This synchronizes a parent from any child, but not the other way around. Good enough for
     * our purposes... just be aware of the limitation.
     */
    public void unbind(String name) throws NamingException {
        map.remove(normalized(name));
        if (parentContext != null) {
            parentContext.unbind(prefix + "/" + normalized(name));
        }
    }

}
