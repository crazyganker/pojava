package org.pojava.persistence.jndi;

/*
 Copyright 2008-11 John Pile

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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class for stubbing out an InitialContext for unit tests.
 * 
 * As a concrete example, this can be used to place DataSource objects into JNDI without running
 * inside an application context.
 * 
 * @author John Pile
 * 
 */
public class JNDIRegistry {

    private static Logger logger = Logger.getLogger("org.pojava.persistence.jndi.JNDIRegistry");

    /**
     * Force use of the factory method
     */
    private JNDIRegistry() {
    }

    /**
     * Get an initial context, making one if it doesn't exist.
     * 
     * @return Context either newly created or retrieved.
     */
    public static Context getInitialContext() throws NamingException {
        String initialContextFactory = "org.pojava.persistence.jndi.TestingContextFactory";
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        return new InitialContext(env);
    }

    /**
     * Get an initial context, making one if it doesn't exist.
     * 
     * @return Context either newly created or retrieved.
     */
    public static Context getInitialContext(String initialContextFactory)
            throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        return new InitialContext(env);
    }

    /**
     * Look up an Environment variable from JNDI.
     * 
     * @param name Environment variable name
     * @return Environment variable retrieved from JNDI.
     * @throws javax.naming.NamingException
     */
    public static Object lookupEnv(String name) throws NamingException {
        Context ctx = new InitialContext();
        return ctx.lookup("java:comp/env/" + name);
    }

    /**
     * Look up a DataSource from JNDI.
     * 
     * @param key Registered datasource name
     * @return a DataSource object retrieved from JNDI registry.
     * @throws javax.naming.NamingException
     */
    public static DataSource lookupDataSource(String key) throws NamingException {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/" + key);
    }

    /**
     * Lookup an object from JNDI by its fully qualified name.
     * 
     * @param name Fully qualified name
     * @return an object retrieved from the JNDI registry.
     * @throws javax.naming.NamingException
     */
    public static Object lookupFullyQualified(String name) throws NamingException {
        Context ctx = new InitialContext();
        return ctx.lookup(name);
    }

    /**
     * Fetch properties from a custom.properties file.
     * 
     * @param propertyFile file holding properties to retrieve
     * @return Properties retrieved from file
     */
    public static Properties fetchProperties(String propertyFile) throws IOException {
        Properties dataSourceProps = new Properties();
        // override properties
        FileInputStream in=null;
        try {
            in = new FileInputStream(propertyFile);
            dataSourceProps.load(in);
        } finally {
            if (in!=null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "IOException occurred trying to read " + propertyFile, ex);
                }
            }
        }
        return dataSourceProps;
    }

    /**
     * Extract a JDBC DataSource from a property object.
     * @param props Properties retrieved from a property file
     * @param dsName Name identifying a DataSource
     * @return DataSource
     * @throws ClassNotFoundException
     */
    public static DataSource extractDataSource(Properties props, String dsName) throws ClassNotFoundException {
        String url=props.getProperty(dsName + ".url");
        String user=props.getProperty(dsName + ".user");
        String password=props.getProperty(dsName + ".password");
        String driver=props.getProperty(dsName + ".driver");
        Class.forName(driver);
        return new DriverManagerDataSource(url, user, password);
    }

    /**
     * Register JDBC DataSources from a property file
     * @param propertyFile Path to property file
     * @throws ClassNotFoundException
     * @throws javax.naming.NamingException
     */
    public static void registerDatasourcesFromFile(String propertyFile) throws ClassNotFoundException, NamingException, IOException {
        Properties dataSourceProps = fetchProperties(propertyFile);
        String propNamesCSV=(String)dataSourceProps.get("datasource_names");
        if (propNamesCSV==null) {
        	throw new IllegalStateException("No datasource_names property found in propertyFile: " + propertyFile);
        }
        String[] propNames = propNamesCSV.split("[ ,|]+");
        Context context=JNDIRegistry.getInitialContext();
        for (String propName : propNames) {
            DataSource ds = extractDataSource(dataSourceProps, propName);
            context.bind("java:comp/env/jdbc/" + propName, ds);
        }
    }

}
