package org.pojava.persistence.sql;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.pojava.exception.ReflectionException;
import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;
import org.pojava.transformation.DefaultAdaptor;
import org.pojava.util.ReflectionTool;

/**
 * A FieldMap maps an individual field of a database table to an individual bean
 * property. It specifies whether the field is regarded as a key field, the
 * drill-down pattern should the mapped property be nested in more complex
 * objects, and specifies an adaptor that can perform transformations of the
 * data on import and export.
 * 
 * @author John Pile
 * 
 */
public class FieldMap {

	private String property;
	private String fieldName;
	private boolean keyField;
	private Class propertyClass;
	private Class fieldClass;
	private BindingAdaptor adaptor;
	private Method[] getters;
	private Method[] setters;
	private TableMap tableMap;
	private static final DefaultAdaptor ADAPTOR = new DefaultAdaptor();

	/**
	 * Construct an empty FieldMap.
	 */
	public FieldMap() {
		this.adaptor = ADAPTOR;
	}

	/**
	 * Construct a FieldMap for the specified property and database field name.
	 * 
	 * @param property
	 * @param fieldName
	 */
	public FieldMap(String property, String fieldName) {
		this.property = property;
		this.fieldName = fieldName;
		this.adaptor = ADAPTOR;
	}

	/**
	 * Construct a FieldMap, specifying whether this is a key field.
	 * 
	 * @param property
	 * @param fieldName
	 * @param isKeyField
	 */
	public FieldMap(String property, String fieldName, boolean isKeyField) {
		this.property = property;
		this.fieldName = fieldName;
		this.keyField = isKeyField;
		this.adaptor = ADAPTOR;
	}

	/**
	 * The "property" is a reference to a field in a Java bean (POJO), where you
	 * delete the word "get" or "set" and lower-case the first letter.
	 * Dot-separate any nested references. For example, if your object contains
	 * a billing zip code as <code>obj.getBillingAddress().getZip()</code>
	 * then the property would be specified as <code>billingAddress.zip</code>.
	 * 
	 * @return Property reference.
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * The "property" is a reference to a field in a Java bean (POJO), where you
	 * delete the word "get" or "set" and lower-case the first letter.
	 * Dot-separate any nested references. For example, if your object contains
	 * a billing zip code as <code>obj.getBillingAddress().getZip()</code>
	 * then the property would be specified as <code>billingAddress.zip</code>.
	 * 
	 * @param property
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * The fieldName is a reference to the database field name.
	 * 
	 * @return Database field name.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Specify the database field name.
	 * 
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * The field class specifies the class of the JDBC field representing the
	 * database field value.
	 * 
	 * @return Java class of property.
	 */
	public Class getFieldClass() {
		return fieldClass;
	}

	/**
	 * The field class specifies the class of the JDBC field representing the
	 * database field value.
	 * 
	 * @param fieldClass
	 */
	public void setFieldClass(Class fieldClass) {
		this.fieldClass = fieldClass;
	}

	/**
	 * True if field represents a key field in the table.
	 * 
	 * @return True if key field.
	 */
	public boolean isKeyField() {
		return keyField;
	}

	/**
	 * Specify whether the field is regarded as a key field.
	 * 
	 * @param keyField
	 */
	public void setKeyField(boolean keyField) {
		this.keyField = keyField;
	}

	/**
	 * The property class represents the Class of the property stored in the
	 * Java bean.
	 * 
	 * @return Java Class of property value.
	 */
	public Class getPropertyClass() {
		return propertyClass;
	}

	/**
	 * The property class represents the Class of the property stored in the
	 * Java bean.
	 * 
	 * @param propertyClass
	 */
	public void setPropertyClass(Class propertyClass) {
		this.propertyClass = propertyClass;
	}

	/**
	 * Set the property value to the converted mapped column of the row.
	 * 
	 * @param rs
	 * @param column
	 * @param obj
	 * @throws SQLException
	 */
	public void setPropertyValue(ResultSet rs, int column, Object obj)
			throws SQLException {
		Object value = rs.getObject(column);
		if (this.adaptor != null) {
			value = this.adaptor
					.inbound(new Binding(this.getFieldClass(), obj));
		}
		try {
			ReflectionTool.setNestedValue(this.getters, this.setters, obj,
					value);
		} catch (NoSuchMethodException ex) {
			throw new ReflectionException("Failed to set value for "
					+ this.getProperty(), ex);
		} catch (IllegalAccessException ex) {
			throw new ReflectionException("Failed to set value for "
					+ this.getProperty(), ex);
		} catch (InstantiationException ex) {
			throw new ReflectionException("Failed to set value for "
					+ this.getProperty(), ex);
		}
	}

	/**
	 * Get the array of getters that drill down to the property.
	 * 
	 * @return Array of (often only one) getters drilling down to a property.
	 */
	public Method[] getGetters() {
		return getters;
	}

	/**
	 * Set the array of getters that drill down to this FieldMap's referenced
	 * property.
	 * 
	 * @param getters
	 */
	public void setGetters(Method[] getters) {
		this.getters = getters;
	}

	/**
	 * Set the array of setters drilling down to the specified class
	 * @param setters
	 */
	public void setSetters(Method[] setters) {
		this.setters = setters;
	}

	/**
	 * Get the adaptor responsible for transforming the data between the database field
	 * and the bean property.
	 * @return BindingAdaptor
	 */
	public BindingAdaptor getAdaptor() {
		return adaptor;
	}

	/**
	 * Set the adaptor responsible for transforming the data between the database field
	 * and the bean property.
	 */
	public void setAdaptor(BindingAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	/**
	 * Specify the TableMap that contains this FieldMap. 
	 * @param map
	 */
	public void setTableMap(TableMap map) {
		this.tableMap = map;
	}

	/**
	 * Get the TableMap that contains this FieldMap. 
	 * @param map
	 */
	public TableMap getTableMap() {
		return this.tableMap;
	}
}
