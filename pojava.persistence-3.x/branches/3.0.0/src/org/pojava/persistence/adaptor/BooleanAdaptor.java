package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing translations for values of type boolean.
 * 
 * @author John Pile
 * 
 */
public class BooleanAdaptor extends BindingAdaptor<Boolean, Boolean> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Boolean> inboundType() {
        return Boolean.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Boolean> outboundType() {
        return Boolean.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Boolean> inbound(Binding<Boolean> inBinding) {
        if (inBinding == null || inBinding.getObj() == null
                || Boolean.class == inBinding.getObj().getClass()) {
            return inBinding;
        }
        // Variations like 10, TF, YN get their own adaptors so we know what to
        // do on the way out.
        return new Binding<Boolean>(Boolean.class, Boolean.valueOf(inBinding.getObj()
                .toString()));
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Boolean> outbound(Binding<Boolean> outBinding) {
        Binding<Boolean> inBinding = new Binding<Boolean>(Boolean.class, null);
        if (outBinding == null || outBinding.getObj() == null) {
            return outBinding;
        }
        inBinding.setObj(outBinding.getObj());
        return inBinding;
    }
}
