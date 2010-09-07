package org.pojava.persistence.adaptor;

import org.pojava.lang.Binding;
import org.pojava.transformation.BindingAdaptor;

/**
 * Adaptor for managing Java to JDBC for a Character value.
 * 
 * @author John Pile
 * 
 */
public class CharAdaptor extends BindingAdaptor<Character, Character> {

    /**
     * The type the translator will produce for the bean.
     */
    public Class<Character> inboundType() {
        return Character.class;
    }

    /**
     * The type the translator will produce for the JDBC driver.
     */
    public Class<Character> outboundType() {
        return Character.class;
    }

    /**
     * Translate the binding from the data source towards Java bean.
     */
    public Binding<Character> inbound(Binding<Character> inBinding) {
        // Prevent constructing a new object when you can.
        if (inBinding == null || inBinding.getObj() == null
                || Character.class == inBinding.getObj().getClass()) {
            return inBinding;
        }
        Binding<Character> outBinding = new Binding<Character>(Character.class, null);
        // A single character array must be translated to a character.
        String chars = inBinding.getObj().toString();
        if (chars.length() == 0) {
            return null;
        } else {
            outBinding.setObj(Character.valueOf(chars.length() == 0 ? ' ' : chars.charAt(0)));
        }
        return outBinding;
    }

    /**
     * Translate the binding from the java bean to the data source.
     */
    public Binding<Character> outbound(Binding<Character> outBinding) {
        return outBinding;
    }
}
