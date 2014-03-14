package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type integer.
 *
 * @author John Pile
 */
public class IntegerAdaptor extends BindingAdaptor<Integer, Integer> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Integer> inboundType() {
        return Integer.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Integer> outboundType() {
        return Integer.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Integer> inbound(Binding<Integer> inBinding) {
        if (inBinding == null || inBinding.getObj() == null
                || inBinding.getObj().getClass() == Integer.class) {
            return inBinding;
        }
        Binding<Integer> outBinding = new Binding<Integer>(Integer.class, null);
        outBinding.setObj(new Integer(inBinding.getObj().toString()));
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Integer> outbound(Binding<Integer> outBinding) {
        return outBinding;
    }
}
