package org.pojava.testing;

/*
 Copyright 2008 John Pile

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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * A helper class for stubbing out an InitialContext for unit tests.
 * 
 * @author John Pile
 * 
 */
public class JNDIRegistry {

	/**
	 * Force use of the factory method
	 */
	private JNDIRegistry() {
	}

	/**
	 * Optional one-time initialization wipes all values.
	 */
	public static Context resetInitialContext() throws NamingException {
		String initialContextFactory = "com.raleys.junit.support.TestContextFactory";
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				initialContextFactory);
		return new InitialContext(env);
	}

	/**
	 * Optional one-time initialization wipes all values.
	 */
	public static Context resetInitialContext(String initialContextFactory)
			throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				initialContextFactory);
		return new InitialContext(env);
	}

	/**
	 * Look up an Environment variable from JNDI.
	 * 
	 * @param name
	 * @return
	 * @throws NamingException
	 */
	public static Object lookupEnv(String name) throws NamingException {
		Context ctx = new InitialContext();
		return ctx.lookup("java:comp/env/" + name);
	}

	/**
	 * Look up a DataSource from JNDI.
	 * 
	 * @param key
	 * @return
	 * @throws NamingException
	 */
	public static DataSource lookupDataSource(String key)
			throws NamingException {
		Context ctx = new InitialContext();
		return (DataSource) ctx.lookup("java:comp/env/jdbc/" + key);
	}

	/**
	 * Lookup an object from JNDI by its fully qualified name.
	 * 
	 * @param name
	 * @return
	 * @throws NamingException
	 */
	public static Object lookupFullyQualified(String name)
			throws NamingException {
		Context ctx = new InitialContext();
		return ctx.lookup(name);
	}

}
