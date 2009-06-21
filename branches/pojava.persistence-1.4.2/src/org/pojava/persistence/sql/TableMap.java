package org.pojava.persistence.sql;

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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.pojava.exception.InconceivableException;
import org.pojava.exception.PersistenceException;
import org.pojava.lang.Binding;
import org.pojava.lang.BoundString;
import org.pojava.persistence.adaptor.TypedAdaptor;
import org.pojava.persistence.util.SqlTool;
import org.pojava.util.ReflectionTool;
import org.pojava.util.StringTool;

/**
 * TableMap manages a collection of mappings between a Java bean and a database
 * table. It allows for either a manual or automated mapping of each property.
 * Each property is defined by its get/set accessors.
 * 
 * @author John Pile
 */
public class TableMap {
	List keyFields = new ArrayList();
	List nonKeyFields = new ArrayList();
	Map allFields = new HashMap();
	Class javaClass = null;
	String tableName = null;
	String dataSourceName = null;

	Logger logger = Logger.getLogger("persistence.TableMap");

	/**
	 * Construct a table map defining the Java class, Table name and DataSource
	 * name.
	 * 
	 * @param javaClass
	 * @param tableName
	 * @param dataSourceName
	 */
	public TableMap(Class javaClass, String tableName, String dataSourceName) {
		if (javaClass == null) {
			throw new IllegalArgumentException(
					"Null javaClass not allowed in TableMap.");
		}
		if (tableName == null) {
			throw new IllegalArgumentException(
					"Null tableName not allowed in TableMap.");
		}
		if (dataSourceName == null) {
			throw new IllegalArgumentException(
					"Null dataSourceName not allowed in TableMap.");
		}
		this.javaClass = javaClass;
		this.tableName = tableName;
		this.dataSourceName = dataSourceName;
	}

	/**
	 * Asks the database for the primary keys for the table.
	 * 
	 * @param conn
	 * @return keys as a set of the most probable keys for each table
	 * @throws SQLException
	 */
	private Set primaryKeys(Connection conn) throws SQLException {
		/*
		 * TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, KEY_SEQ, PK_NAME
		 */
		ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tableName);
		Set keys = new HashSet();
		while (rs.next()) {
			keys.add(rs.getString(4)); // COLUMN_NAME
		}
		return keys;
	}

	/**
	 * Populate the field mappings by asking the database for MetaData.
	 * 
	 * @throws SQLException
	 */
	public void autoBind() throws SQLException {
		DataSource ds = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		try {
			ds = DatabaseCache.getDataSource(this.dataSourceName);
			conn = ds.getConnection();
			sql.append("select * from ");
			sql.append(tableName);
			sql.append(" where 1=0");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			ResultSetMetaData dtMeta = rs.getMetaData();
			autoBind(conn, dtMeta);
		} finally {
		    SqlTool.close(rs,stmt,conn);
		}
	}

	/**
	 * Build the bindings between the Java object and the database table.
	 * 
	 * @param conn
	 * @param rsMeta
	 * @throws SQLException
	 */
	private void autoBind(Connection conn, ResultSetMetaData rsMeta)
			throws SQLException {
		Set primaryKeys = primaryKeys(conn);
		int cols = rsMeta.getColumnCount();
		for (int i = 0; i < cols; i++) {
			int column = i + 1;
			String fieldName = rsMeta.getColumnName(column);
			String property = StringTool.camelFromUnderscore(fieldName);
			FieldMap fm = null;
			try {
				fm = new FieldMap(property, fieldName, primaryKeys
						.contains(fieldName), Class.forName(rsMeta
						.getColumnClassName(column)), this);
			} catch (NoSuchMethodException ex) {
				throw new InconceivableException(
						"Did reflection find something that doesn't exist?", ex);
			} catch (ClassNotFoundException ex) {
				throw new InconceivableException(
						"Weird.  A class returned by MetaData is not in classpath: "
								+ rsMeta.getColumnClassName(column), ex);
			}
			try {
				addFieldMap(fm);
			} catch (NoSuchMethodException ex) {
				logger
						.info("TableMap.autoBind cannot find property to match fieldName="
								+ fieldName);
				/*
				 * Quietly ignored to allow an automated partial mapping.
				 */
			}
		}
	}

	public String getTableName() {
		return tableName;
	}

	public Class getJavaClass() {
		return javaClass;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public List getKeyFields() {
		return keyFields;
	}

	public List getNonKeyFields() {
		return nonKeyFields;
	}

	public Map getAllFields() {
		return allFields;
	}

	/**
	 * Add a field map to this table map.
	 * 
	 * @param field
	 */
	public void addFieldMap(FieldMap field) throws NoSuchMethodException {
		if (field.getGetters() == null) {
			field.setGetters(ReflectionTool.getterMethodDrilldown(javaClass, field
					.getProperty()));
		}
		if (field.getPropertyClass() == null && field.getGetters() != null) {
			field
					.setPropertyClass(field.getGetters()[field.getGetters().length - 1]
							.getReturnType());
		}
		if (field.getSetters() == null && field.getGetters() != null) {
			field.setSetters(ReflectionTool.setterMethodDrilldown(field.getGetters()));
		}
		field.setTableMap(this);
		this.allFields.put(field.getProperty(), field);
		if (field.isKeyField()) {
			this.keyFields.add(field);
		} else {
			this.nonKeyFields.add(field);
		}
	}

	/**
	 * Add a FieldMap to this TableMap.
	 * 
	 * @param property
	 * @param fieldName
	 * @param isKeyField
	 * @param columnClass
	 * @throws NoSuchMethodException
	 */
	public void addFieldMap(String property, String fieldName,
			boolean isKeyField, Class columnClass) throws NoSuchMethodException {
		addFieldMap(new FieldMap(property, fieldName, isKeyField, columnClass,
				this));
	}

	/**
	 * Add a FieldMap to this TableMap.
	 * 
	 * @param property
	 * @param fieldName
	 * @param isKeyField
	 * @param adaptor
	 * @throws NoSuchMethodException
	 */
	public void addFieldMap(String property, String fieldName, boolean isKeyField,
			TypedAdaptor adaptor)
			throws NoSuchMethodException {
		addFieldMap(new FieldMap(property, fieldName, isKeyField, adaptor, this));
	}

	/**
	 * Return comma-separated list of all fields.
	 * 
	 * @return
	 */
	private String csvAllFields() {
		StringBuffer sb = new StringBuffer();
		for (Iterator it = this.allFields.values().iterator(); it.hasNext();) {
			FieldMap fieldMap = (FieldMap) it.next();
			sb.append(fieldMap.getColumnName());
			sb.append(", ");
		}
		sb.setLength(Math.max(0, sb.length() - 2));
		return sb.toString();
	}

	/**
	 * Invoke a field's getter for the given bean. Recycle the getter methods
	 * where possible to speed up future calls.
	 * 
	 * @param field
	 * @param bean
	 * @return retrieved field data. Primitives are converted to equivalent
	 *         object.
	 */
	private Object getFieldValue(FieldMap field, Object bean)
			throws NoSuchMethodException {
		Object propertyObj;
		if (field.getProperty().indexOf('[') >= 0) {
			propertyObj = ReflectionTool.getNestedValue(field.getProperty(),
					bean);
		} else {
			Method[] getters = field.getGetters();
			if (getters == null) {
				getters = ReflectionTool.getterMethodDrilldown(bean.getClass(), field
						.getProperty());
				field.setGetters(getters);
			}
			propertyObj = ReflectionTool.getNestedValue(field.getGetters(),
					bean);
		}
		return propertyObj;
	}

	/**
	 * Form a SELECT statement for this map.
	 * 
	 * @return SQL select statement
	 */
	public BoundString sqlSelect() {
		BoundString bs = new BoundString();
		if (this.keyFields.isEmpty()) {
			throw new IllegalStateException(
					"At least one key field must be defined for table "
							+ this.getTableName());
		}
		bs.append("SELECT ");
		bs.append(csvAllFields());
		bs.append(" FROM ");
		bs.append(this.tableName);
		return bs;
	}

	/**
	 * Form a SELECT statement for this map filtered by key fields to a single
	 * object.
	 * 
	 * @return SQL select statement
	 */
	public BoundString sqlSelect(Object bean) {
		BoundString bs = new BoundString();
		if (this.keyFields.isEmpty()) {
			throw new IllegalStateException(
					"At least one key field must be defined for table "
							+ this.getTableName());
		}
		bs.append("SELECT ");
		bs.append(csvAllFields());
		bs.append(" FROM ");
		bs.append(this.tableName);
		bs.append(this.whereKeyFieldsMatch(bean));
		return bs;
	}

	/**
	 * Form an INSERT statement for this map.
	 * 
	 * @param bean
	 * @return SQL insert statement
	 */
	public BoundString sqlInsert(Object bean) {
		BoundString bs = new BoundString();
		bs.append("INSERT INTO ");
		bs.append(this.tableName);
		bs.append(" (");
		bs.append(csvAllFields());
		bs.append(") VALUES (");
		try {
			for (Iterator it = this.allFields.values().iterator(); it.hasNext();) {
				FieldMap field = (FieldMap) it.next();
				bs.append("?, ");
				Binding b = field.getAdaptor().outbound(
						new Binding(field.getPropertyClass(), getFieldValue(
								field, bean)));
				bs.getBindings().add(b);
				// bs.addBinding(field.getPropertyClass(), getFieldValue(field,
				// bean));
			}
		} catch (NoSuchMethodException ex) {
			throw new PersistenceException(
					"Bad FieldMap mapping somehow squeaked through. "
							+ ex.getMessage(), ex);
		}
		bs.chop(2);
		bs.append(")");
		return bs;
	}

	/**
	 * Form an UPDATE statement for this map.
	 * 
	 * @param bean
	 * @return SQL update statement
	 */
	public BoundString sqlUpdate(Object bean) {
		BoundString bs = new BoundString();
		bs.append("UPDATE ");
		bs.append(this.tableName);
		bs.append(" SET ");
		for (Iterator it = this.nonKeyFields.iterator(); it.hasNext();) {
			FieldMap field = (FieldMap) it.next();
			Method[] getters = field.getGetters();
			Object propertyObj = null;
			bs.append(field.getColumnName());
			bs.append("=?, ");
			if (getters == null) {
				if (field.getProperty().indexOf('[') >= 0) {
					propertyObj = ReflectionTool.getNestedValue(field
							.getProperty(), bean);
				} else {
					try {
						getters = ReflectionTool.getterMethodDrilldown(bean.getClass(),
								field.getProperty());
					} catch (NoSuchMethodException ex) {
						throw new PersistenceException(
								"Bad FieldMap mapping somehow squeaked through. "
										+ ex.getMessage(), ex);
					}
					field.setGetters(getters);
					propertyObj = ReflectionTool.getNestedValue(field
							.getGetters(), bean);
				}
			} else {
				propertyObj = ReflectionTool.getNestedValue(field.getGetters(),
						bean);
			}
			Binding b = field.getAdaptor().outbound(
					new Binding(field.getPropertyClass(), propertyObj));
			bs.addBinding(b.getType(), b.getObj());
		}
		bs.chop(2);
		bs.append(whereKeyFieldsMatch(bean));
		return bs;
	}

	/**
	 * Form a DELETE statement for this map.
	 * 
	 * @param bean
	 * @return SQL delete statement
	 */
	public BoundString sqlDelete(Object bean) {
		BoundString bs = new BoundString();
		bs.append("DELETE FROM ");
		bs.append(this.tableName);
		bs.append(whereKeyFieldsMatch(bean));
		return bs;
	}

	/**
	 * Form a WHERE clause for the key fields of this map.
	 * 
	 * @param bean
	 * @return SQL where clause
	 */
	public BoundString whereKeyFieldsMatch(Object bean) {
		BoundString bs = new BoundString();
		bs.append(" WHERE ");
		for (Iterator it = this.keyFields.iterator(); it.hasNext();) {
			FieldMap field = (FieldMap) it.next();
			bs.append(field.getColumnName());
			Object propertyObj = null;
			if (field.getGetters() == null) {
				propertyObj = ReflectionTool.getNestedValue(
						field.getProperty(), bean);
			} else {
				propertyObj = ReflectionTool.getNestedValue(field.getGetters(),
						bean);
			}
			if (propertyObj == null) {
				bs.append(" IS NULL AND ");
			} else {
				bs.append("=? AND ");
				bs.addBinding(field.getPropertyClass(), propertyObj);
			}
		}
		bs.chop(5);
		return bs;
	}

	/**
	 * Extract an Object specific to this TableMap
	 * 
	 * @param rs
	 *            ResultSet already advanced to next row.
	 * @return bean extracted from a ResultSet row.
	 */
	public Object extractObject(ResultSet rs) {
		Object obj = null;
		try {
			obj = this.javaClass.newInstance();
			int i = 0;
			for (Iterator it = allFields.values().iterator(); it.hasNext();) {
				i++;
				FieldMap fld = (FieldMap) it.next();
				fld.setPropertyValue(rs, i, obj);
			}
		} catch (InstantiationException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("Cannot construct ");
			sb.append(this.javaClass.getName());
			sb.append(": ");
			sb.append(ex.getMessage());
			throw new PersistenceException(sb.toString(), ex);
		} catch (IllegalAccessException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("Cannot construct ");
			sb.append(this.javaClass.getName());
			sb.append(": ");
			sb.append(ex.getMessage());
			throw new PersistenceException(sb.toString(), ex);
		} catch (SQLException ex) {
			StringBuffer sb = new StringBuffer();
			sb.append("SQL error while constructing ");
			sb.append(this.javaClass.getName());
			sb.append(": ");
			sb.append(ex.getMessage());
			throw new PersistenceException(sb.toString(), ex);
		}
		return obj;
	}

}
