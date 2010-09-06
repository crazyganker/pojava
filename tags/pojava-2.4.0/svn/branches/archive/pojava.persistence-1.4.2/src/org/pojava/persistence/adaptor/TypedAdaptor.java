package org.pojava.persistence.adaptor;

import org.pojava.transformation.BindingAdaptor;

/**
 * A TypedAdaptor is a BindingAdaptor that describes the target types that it supports.
 * 
 * @author John
 * 
 */
public interface TypedAdaptor extends BindingAdaptor {

    Class inboundType();

    Class outboundType();

}
