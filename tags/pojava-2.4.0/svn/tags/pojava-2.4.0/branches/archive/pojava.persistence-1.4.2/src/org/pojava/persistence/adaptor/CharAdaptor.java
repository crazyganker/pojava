package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;

/**
 * Adaptor for managing Java to JDBC for a Character value.
 * 
 * @author John Pile
 * 
 */
public class CharAdaptor implements TypedAdaptor {

    /**
     * The type the translator will produce for the bean.
     */
    public Class inboundType() {
        return Character.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class outboundType() {
        return Character.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding inbound(Binding inBinding) {
        // Prevent constructing a new object when you can.
        if (inBinding == null)
            return null;
        if (inBinding.getObj() == null || Character.class.equals(inBinding.getType())) {
            return inBinding;
        }
        Binding outBinding = new Binding(Character.class, inBinding.getObj());
        // A single character array must be translated to a character.
        if (String.class.equals(inBinding.getType())) {
            String chars = (String) inBinding.getObj();
            outBinding.setObj(new Character(chars.length() == 0 ? ' ' : chars.charAt(0)));
        }
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding outbound(Binding obj) {
        return obj;
    }
}
