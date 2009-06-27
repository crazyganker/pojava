package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a java.sql.Date value mapped to a java.util.Date value
 * or one of its derivatives.
 * 
 * @author John Pile
 * 
 */
public class UtilDateSqlAdaptor implements BindingAdaptor {

    /**
     * The type the translator will produce for the bean.
     */
    public Class inboundType() {
        return java.util.Date.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class outboundType() {
        return java.sql.Date.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding inbound(Binding inBinding) {
        // Prevent constructing a new object when you can.
        if (inBinding == null)
            return null;
        if (inBinding.getObj() == null || java.sql.Date.class.equals(inBinding.getType())) {
            return inBinding;
        }
        Binding outBinding;
        outBinding = new Binding(java.util.Date.class, (java.util.Date) inBinding.getObj());
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding outbound(Binding outBinding) {
        // Prevent constructing a new object when you can.
        if (outBinding == null)
            return null;
        if (outBinding.getObj() == null || java.sql.Date.class.equals(outBinding.getType())) {
            return outBinding;
        }
        Binding inBinding = new Binding(java.sql.Date.class, new java.sql.Date(
                ((java.util.Date) outBinding.getObj()).getTime()));
        return inBinding;
    }
}
