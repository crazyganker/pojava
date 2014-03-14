package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

import java.math.BigDecimal;

/**
 * Adaptor for managing translations for values of type double.
 *
 * @author John Pile
 */
public class BigDecimalAdaptor extends BindingAdaptor<BigDecimal, BigDecimal> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<BigDecimal> inboundType() {
        return BigDecimal.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<BigDecimal> outboundType() {
        return BigDecimal.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<BigDecimal> inbound(Binding<BigDecimal> inBinding) {
        return inBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<BigDecimal> outbound(Binding<BigDecimal> outBinding) {
        return outBinding;
    }
}
