package org.pojava.persistence.jndi;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;



public class DriverManagerDataSourceTester extends TestCase {

	private DriverManagerDataSource ds;
	
	public void setUp() throws Exception {
		JNDIRegistry.getInitialContext();
    	JNDIRegistry.registerDatasourcesFromFile("config/ds_test.properties");
		this.ds=(DriverManagerDataSource) JNDIRegistry.lookupDataSource("pojava_test");
	}

	public void testGetConnection() throws Exception {
		Connection conn=ds.getConnection();
		assertTrue(conn!=null);
	}
	
	public void testGetConnectionNVBad() throws Exception {
		try {
			ds.getConnection("pojava", "incorrect");
		} catch (SQLException ex) {
			assertTrue(ex.getMessage().contains("failed"));
		}		
	}
	public void testGetConnectionNVGood() throws Exception {
		Connection conn=ds.getConnection("pojava", "popojava");
		assertTrue(conn!=null);
	}
	
	public void testLoginTimeout() throws Exception {
		ds.setLoginTimeout(123);
		assertEquals(123, ds.getLoginTimeout());
	}

	public void testPrintWriter() throws Exception {
		ds.setLogWriter(new PrintWriter("config/printwriter.txt"));
		PrintWriter pw=ds.getLogWriter();
		assertTrue(pw!=null);
	}
	
	
}
