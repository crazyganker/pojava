package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type double.
 * 
 * @author John Pile
 * 
 */
public class DoubleAdaptor extends BindingAdaptor<Double, Double> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Double> inboundType() {
        return Double.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Double> outboundType() {
        return Double.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Double> inbound(Binding<Double> inBinding) {
        if (inBinding == null || inBinding.getObj() == null
                || inBinding.getObj().getClass() == Double.class) {
            return inBinding;
        }
        Binding<Double> outBinding = new Binding<Double>(Double.class, null);
        outBinding.setObj(new Double(inBinding.getObj().toString()));
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Double> outbound(Binding<Double> outBinding) {
        return outBinding;
    }
}
