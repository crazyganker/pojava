package org.pojava.persistence.adaptor;

import java.sql.Time;
import java.util.Date;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Process a Time value from a ResultSet. This ensures that the hidden date portion of the Time
 * value is set to 1/1/1970.
 * 
 * @author John Pile
 * 
 */
public class TimeAdaptor extends BindingAdaptor<Time, Time> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Time> inboundType() {
        return Time.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Time> outboundType() {
        return Time.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Time> inbound(Binding<Time> inBinding) {
        if (inBinding == null || inBinding.getObj() == null) {
            return inBinding;
        }
        if (inBinding.getObj().getClass() == Time.class) {
            long t = inBinding.getValue().getTime();
            if (t >= 86400000 || t < 0) {
                inBinding.getValue().setTime(t % 86400000);
            }
        }
        return new Binding<Time>(Time.class, new Time(
                ((Date) inBinding.getObj()).getTime() % 86400000));
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Time> outbound(Binding<Time> outBinding) {
        if (outBinding != null && outBinding.getObj() != null
                && outBinding.getObj().getClass() == Time.class) {
            long t = outBinding.getValue().getTime();
            if (t >= 86400000 || t < 0) {
                outBinding.getValue().setTime(t % 86400000);
            }
        }
        return outBinding;
    }
}
