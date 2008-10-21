package org.pojava.persistence.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.pojava.testing.DriverManagerDataSource;
import org.pojava.testing.JNDIRegistry;
import org.pojava.util.StringTool;

public class DatabaseCacheTester extends TestCase {

	public static final boolean DEBUG=false;
	
	protected void setUp() throws Exception {
		JNDIRegistry.getInitialContext();
		Class.forName("org.postgresql.Driver");
		DataSource ds=new DriverManagerDataSource("jdbc:postgresql://localhost:5432/postgres", "pojava", "popojava");
		DatabaseCache.registerDataSource("pojava_pg", ds);
	}
	
	public void testGetDataSource() {
		DataSource ds=DatabaseCache.getDataSource("pojava_pg");
		assertTrue(ds!=null);
	}
	
	public void testMetaData() throws Exception {
		DataSource ds=DatabaseCache.getDataSource("pojava_pg");
		assertTrue(ds!=null);
		TransConnection internalConn=null;
		Connection conn=null;
		try {
			Connection originalConn=ds.getConnection();
			internalConn=new TransConnection(originalConn);
			conn=(Connection)internalConn;
			DatabaseMetaData connMeta=conn.getMetaData();
			ResultSet catalogs=connMeta.getCatalogs();
			ResultSetMetaData rsMeta=catalogs.getMetaData();
			if (DEBUG) {
				describe(rsMeta);
				view(catalogs, rsMeta.getColumnCount());
			}
		} finally {
			if (conn!=null) {
				conn.close();
				assertFalse(internalConn.isClosed());
				internalConn.close();
				assertFalse(internalConn.isClosed());
				internalConn.setCloseAllowed(true);
				internalConn.close();
				assertTrue(internalConn.isClosed());
			}
		}
	}
	
	public void testTable() throws Exception {
		DataSource ds=DatabaseCache.getDataSource("pojava_pg");
		assertTrue(ds!=null);
		Connection conn=null;
		try {
			conn=ds.getConnection();
			PreparedStatement ps=conn.prepareStatement("select * from dao_test");
			ResultSet daoTest=ps.executeQuery();
			ResultSetMetaData dtMeta=daoTest.getMetaData();
			if (DEBUG) {
				describe(dtMeta);
				view(daoTest, dtMeta.getColumnCount());
			}
		} finally {
			if (conn!=null) {
				conn.close();
			}
		}
	}
	
	private void view(ResultSet rs, int columnCt) throws SQLException {
		StringBuffer sb=new StringBuffer();
		while (rs.next()) {
			for (int i=0; i<columnCt; i++) {
				int column=i+1;
				if (i>=0) sb.append("\t");
				sb.append(rs.getString(column));
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	String pad(String str, int width) {
		StringBuffer sb=new StringBuffer(str.substring(0,Math.min(str.length(), width)));
		width-=sb.length();
		for (int j=0; j<width; j++) {
			sb.append(" ");
		}
		return sb.toString();
	}
	
	
	int maxWidth(ResultSetMetaData rsMeta, String label) throws SQLException {
		int width=0;
		int cols=rsMeta.getColumnCount();
		for (int i=0; i<cols; i++) {
			int column=i+1;
			if ("columnName".equals(label)) {
				width=Math.max(width, 2+rsMeta.getColumnName(column).length());
			}
			if ("columnClassName".equals(label)) {
				width=Math.max(width, 2+rsMeta.getColumnClassName(column).length());
			}
			if ("columnTypeName".equals(label)) {
				width=Math.max(width, 2+rsMeta.getColumnTypeName(column).length());
			}
		}
		return width;
	}
	
	private void describe(ResultSetMetaData rsMeta) throws Exception {
		int cols=rsMeta.getColumnCount();
		StringBuffer sb=new StringBuffer();
		int columnNamePad=maxWidth(rsMeta, "columnName");
		int columnClassNamePad=maxWidth(rsMeta, "columnClassName");
		int columnTypeNamePad=maxWidth(rsMeta, "columnTypeName");
		for (int i=0; i<cols; i++) {
			int column=i+1;
			sb.append(pad(StringTool.camelFromUnderscore(rsMeta.getColumnName(column)),columnNamePad));
			sb.append(pad(rsMeta.getColumnName(column),columnNamePad));
			sb.append(pad(rsMeta.getColumnClassName(column),columnClassNamePad));
			sb.append(pad(rsMeta.getColumnTypeName(column),columnTypeNamePad));
			sb.append(pad(Integer.toString(rsMeta.getColumnDisplaySize(column)),5));
			sb.append(pad(Integer.toString(rsMeta.getPrecision(column)),5));
			sb.append(pad(Integer.toString(rsMeta.getScale(column)),5));
			sb.append(pad(Integer.toString(rsMeta.isNullable(column)),5));
			sb.append("\n");
		}
		System.out.println(sb.toString());
		
	}
	
	
}

