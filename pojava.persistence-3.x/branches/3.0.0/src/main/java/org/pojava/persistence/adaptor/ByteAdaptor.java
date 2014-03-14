package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a Byte value.
 *
 * @author John Pile
 */
public class ByteAdaptor extends BindingAdaptor<Byte, Byte> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Byte> inboundType() {
        return Byte.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Byte> outboundType() {
        return Byte.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Byte> inbound(Binding<Byte> inBinding) {
        if (inBinding == null || inBinding.getObj() == null
                || Byte.class.equals(inBinding.getType())) {
            return inBinding;
        }
        Binding<Byte> outBinding = new Binding<Byte>(Byte.class, null);
        /*
         * When DB value is a smallint, JDBC will receive it as type Integer. With the DAO layer
         * seeing only Integer to Byte, there's a danger of overflow, but we trust the developer
         * had valid reason to choose Byte over Integer in the bean.
         */
        outBinding.setValue(Byte.valueOf(inBinding.getObj().toString()));
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Byte> outbound(Binding<Byte> outBinding) {
        return outBinding;
    }
}
