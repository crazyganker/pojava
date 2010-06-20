package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for values where no conversion is necessary.
 * 
 * @author John Pile
 * 
 */
@SuppressWarnings("unchecked")
public class PassthroughAdaptor extends BindingAdaptor {
    public Binding inbound(Binding inBinding) {
        return inBinding;
    }

    public Binding outbound(Binding outBinding) {
        return outBinding;
    }

    public Class inboundType() {
        return Object.class;
    }

    public Class outboundType() {
        return Object.class;
    }
}
