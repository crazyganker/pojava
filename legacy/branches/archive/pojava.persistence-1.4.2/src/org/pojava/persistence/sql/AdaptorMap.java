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
public interface AdaptorMap {

    /**
     * Choose an adaptor specific to a type and set of methods.
     * 
     * @param beanClass
     * @param getters
     * @param columnClass
     * @return
     */
    public BindingAdaptor chooseAdaptor(Class beanClass, Method[] getters, Class columnClass);

}
