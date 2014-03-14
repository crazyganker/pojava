package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type float.
 *
 * @author John Pile
 */
public class FloatAdaptor extends BindingAdaptor<Float, Float> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Float> inboundType() {
        return Float.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Float> outboundType() {
        return Float.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Float> inbound(Binding<Float> inBinding) {
        if (inBinding == null || inBinding.getObj() == null
                || inBinding.getObj().getClass() == Float.class) {
            return inBinding;
        }
        Binding<Float> outBinding = new Binding<Float>(Float.class, null);
        outBinding.setObj(new Float(inBinding.getObj().toString()));
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Float> outbound(Binding<Float> outBinding) {
        return outBinding;
    }
}
