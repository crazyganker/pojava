package org.pojava.persistence.sql;

import java.lang.reflect.Method;

import org.pojava.transformation.BindingAdaptor;

/**
 * AdaptorMap serves as a rules engine for determining which BindingAdaptor to use to translate
 * data between a database field and a bean property.
 * 
 * @author John Pile
 * 
 */
public interface AdaptorMap<PROP, COL> {

    /**
     * Choose an adaptor specific to a getter method and class.
     * 
     * @param getter
     *            a method
     * @param propertyClass
     *            the type of the drilled-down property
     * @return BindingAdaptor satisfying that property
     */
    public BindingAdaptor<PROP, COL> chooseAdaptor(Method getter, Class<PROP> propertyClass);

}
