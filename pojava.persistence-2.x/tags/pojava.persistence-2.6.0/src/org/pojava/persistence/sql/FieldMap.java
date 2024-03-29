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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.pojava.exception.ReflectionException;
import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;
import org.pojava.util.ReflectionTool;

/**
 * A FieldMap maps an individual field of a database table to an individual bean property. It
 * specifies whether the field is regarded as a key field, the drill-down pattern should the
 * mapped property be nested in more complex objects, and specifies an adaptor that can perform
 * transformations of the data on import and export.
 * 
 * @author John Pile
 * 
 */
public class FieldMap<POJO, PROP, COL> {

    private String property;
    private String columnName;
    private boolean keyField;
    private Class<PROP> propertyClass;
    private Class<COL> columnClass;
    private BindingAdaptor<PROP, COL> adaptor;
    private Method[] getters;
    private Method[] setters;
    private TableMap<POJO> tableMap;

    /**
     * Construct an empty FieldMap.
     */
    public FieldMap() {
        this.adaptor = null;
    }

    /**
     * Construct a FieldMap, specifying whether this is a key field.
     * 
     * @param property
     * @param fieldName
     * @param isKeyField
     * @param columnClass
     * @param tableMap
     */
    @SuppressWarnings("unchecked")
	public FieldMap(String property, String fieldName, boolean isKeyField,
            Class<COL> columnClass, TableMap<POJO> tableMap) throws NoSuchMethodException {
        Class<POJO> parentType = tableMap.getJavaClass();
        this.property = property;
        this.columnName = fieldName;
        this.keyField = isKeyField;
        this.getters = ReflectionTool.getterMethodDrilldown(parentType, property);
        this.setters = ReflectionTool.setterMethodDrilldown(this.getters);
        this.propertyClass = (Class<PROP>) this.getters[this.getters.length - 1]
                .getReturnType();
        this.tableMap = tableMap;
        this.columnClass = columnClass;
        this.adaptor = DefaultAdaptorMap.getInstance().chooseAdaptor(
                this.getters[this.getters.length - 1], columnClass);
    }

    /**
     * Construct a FieldMap, specifying whether this is a key field.
     * 
     * @param property
     * @param fieldName
     * @param isKeyField
     * @param tableMap
     * @param columnClass
     * @param adaptorMap
     *            custom rules engine for determining a BindingAdaptor
     */
    @SuppressWarnings("unchecked")
    public FieldMap(String property, String fieldName, boolean isKeyField,
            TableMap<POJO> tableMap, Class<COL> columnClass, AdaptorMap<PROP,COL> adaptorMap)
            throws NoSuchMethodException {
        Class<POJO> parentType = tableMap.getJavaClass();
        this.property = property;
        this.columnName = fieldName;
        this.keyField = isKeyField;
        this.getters = ReflectionTool.getterMethodDrilldown(parentType, property);
        this.setters = ReflectionTool.setterMethodDrilldown(this.getters);
        this.propertyClass = (Class<PROP>) this.getters[this.getters.length - 1]
                .getReturnType();
        this.tableMap = tableMap;
        this.columnClass = columnClass;
        this.adaptor = adaptorMap.chooseAdaptor(this.getters[this.getters.length - 1],
                propertyClass);
    }

    /**
     * Construct a FieldMap, specifying whether this is a key field.
     * 
     * @param property
     * @param fieldName
     * @param isKeyField
     * @param adaptor
     * @param tableMap
     */
    public FieldMap(String property, String fieldName, boolean isKeyField,
            BindingAdaptor<PROP, COL> adaptor, TableMap<POJO> tableMap)
            throws NoSuchMethodException {
        Class<POJO> parentType = tableMap.getJavaClass();
        this.property = property;
        this.columnName = fieldName;
        this.keyField = isKeyField;
        this.getters = ReflectionTool.getterMethodDrilldown(parentType, property);
        this.setters = ReflectionTool.setterMethodDrilldown(this.getters);
        this.propertyClass = adaptor.inboundType();
        this.tableMap = tableMap;
        this.columnClass = adaptor.outboundType();
        this.adaptor = adaptor;
    }

    /**
     * The "property" is a reference to a field in a Java bean (POJO), that will typically match
     * your private property name. It is derived from the getter by deleting the word "get" (or
     * "is") and lower-casing the first letter.
     * 
     * Nested references are supported. For example, if your object contains a billing zip code
     * as <code>obj.getBillingAddress().getZip()</code> then the property would be specified as
     * <code>billingAddress.zip</code>.
     * 
     * @return Property associated with this FieldMap
     */
    public String getProperty() {
        return property;
    }

    /**
     * The "property" is a reference to a field in a Java bean (POJO), that will typically match
     * your private property name. It is derived from the getter by deleting the word "get" (or
     * "is") and lower-casing the first letter.
     * 
     * Nested references are supported. For example, if your object contains a billing zip code
     * as <code>obj.getBillingAddress().getZip()</code> then the property would be specified as
     * <code>billingAddress.zip</code>.
     * 
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * The columnName is a reference to the database field name.
     * 
     * @return Database field name.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Specify the database field name.
     * 
     * @param columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * The columnClass specifies the class of the JDBC field representing the database field
     * value.
     * 
     * @return type of JDBC column.
     */
    public Class<COL> getColumnClass() {
        return this.columnClass;
    }

    /**
     * The columnClass specifies the class of the JDBC field representing the database field
     * value.
     * 
     * @param columnClass
     *            data type returned by JDBC for this field
     */
    public void setColumnClass(Class<COL> columnClass) {
        this.columnClass = columnClass;
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
     * The property class represents the Class of the property stored in the Java bean.
     * 
     * @return Java Class of property value.
     */
    public Class<PROP> getPropertyClass() {
        return propertyClass;
    }

    /**
     * The property class represents the Class of the property stored in the Java bean.
     * 
     * @param propertyClass
     */
    public void setPropertyClass(Class<PROP> propertyClass) {
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
    @SuppressWarnings("unchecked")
    public void setPropertyValue(ResultSet rs, int column, Object obj) throws SQLException {
        Object value = rs.getObject(column);
        if (this.adaptor != null) {
            value = this.adaptor.inbound(new Binding(this.getColumnClass(), value)).getObj();
        }
        try {
            ReflectionTool.setNestedValue(this.getters, this.setters, obj, value);
        } catch (NoSuchMethodException ex) {
            throw new ReflectionException("Failed to set value for " + this.getProperty(), ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException("Failed to set value for " + this.getProperty(), ex);
        } catch (InstantiationException ex) {
            throw new ReflectionException("Failed to set value for " + this.getProperty(), ex);
        }
    }

    /**
     * Get the array of getters that drill down to the property.
     * 
     * @return Array of (often only one) getters drilling down to a property.
     */
    public Method[] getGetters() {
        return this.getters;
    }

    /**
     * Set the array of getters that drill down to this FieldMap's referenced property.
     * 
     * @param getters
     */
    public void setGetters(Method[] getters) {
        this.getters = getters;
    }

    /**
     * Get the array of setters that drill down to the property.
     * 
     * @return Array of (often only one) setters drilling down to a property.
     */
    public Method[] getSetters() {
        return setters;
    }

    /**
     * Set the array of setters drilling down to the specified class
     * 
     * @param setters
     */
    public void setSetters(Method[] setters) {
        this.setters = setters;
    }

    /**
     * Get the adaptor responsible for transforming the data between the database field and the
     * bean property.
     * 
     * @return BindingAdaptor
     */
    public BindingAdaptor<PROP, COL> getAdaptor() {
        return adaptor;
    }

    /**
     * Set the adaptor responsible for transforming the data between the database field and the
     * bean property.
     */
    public void setAdaptor(BindingAdaptor<PROP, COL> adaptor) {
        this.adaptor = adaptor;
    }

    /**
     * Specify the TableMap that contains this FieldMap.
     * 
     * @param map
     */
    public void setTableMap(TableMap<POJO> map) {
        this.tableMap = map;
    }

    /**
     * Get the TableMap that contains this FieldMap.
     * 
     * @return containing TableMap
     */
    public TableMap<POJO> getTableMap() {
        return this.tableMap;
    }
}
