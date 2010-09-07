package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;

/**
 * Adaptor for managing translations for values of type double.
 * 
 * @author John Pile
 * 
 */
public class DoubleAdaptor implements TypedAdaptor {

    /**
     * The type the translator will produce for the bean.
     */
    public Class inboundType() {
        return Double.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class outboundType() {
        return Double.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding inbound(Binding inBinding) {
        Binding outBinding = inBinding;
        if (inBinding == null)
            return null;
        if (inBinding.getObj() == null) {
            return outBinding;
        }
        outBinding.setObj(new Double(inBinding.getObj().toString()));
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding outbound(Binding obj) {
        return obj;
    }
}
