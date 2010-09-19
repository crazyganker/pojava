package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for values where no conversion is necessary.
 * 
 * @author John Pile
 * 
 */
public class PassthroughAdaptor<I,O> extends BindingAdaptor<I,O> {
    @SuppressWarnings("unchecked")
	public Binding<I> inbound(@SuppressWarnings("rawtypes") Binding inBinding) {
        return inBinding;
    }

    @SuppressWarnings("unchecked")
	public Binding<O> outbound(@SuppressWarnings("rawtypes") Binding outBinding) {
        return outBinding;
    }

    @SuppressWarnings("unchecked")
	public Class<I> inboundType() {
        return (Class<I>) Object.class;
    }

    @SuppressWarnings("unchecked")
	public Class<O> outboundType() {
        return (Class<O>) Object.class;
    }
}
