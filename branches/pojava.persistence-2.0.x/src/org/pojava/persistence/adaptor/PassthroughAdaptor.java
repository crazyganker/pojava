package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

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
