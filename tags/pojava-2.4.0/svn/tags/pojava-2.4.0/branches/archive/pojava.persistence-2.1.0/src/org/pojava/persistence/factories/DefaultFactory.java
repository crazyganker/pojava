package org.pojava.persistence.factories;

import java.util.Map;

import org.pojava.exception.PersistenceException;
import org.pojava.util.EncodingTool;

/**
 * The DefaultFactory is a multi-purpose factory for handling objects with single-value
 * constructors, such as primitives.
 * 
 * @author John Pile
 * 
 */
@SuppressWarnings("unchecked")
public class DefaultFactory implements SerialFactory {

    /**
     * Interpret a Boolean value from a String.
     * 
     * @param var
     * @return Boolean.TRUE or (default) Boolean.FALSE
     */
    private Boolean parseBoolean(String var) {
        if (var == null) {
            return Boolean.FALSE;
        }
        char[] chars = var.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == 'Y' || c == 'y' || c == '1' || c == 'T' || c == 't') {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Construct an object.
     */
    public Object construct(Class type, Object[] values) {
        if (values == null || values.length != 1) {
            throw new PersistenceException(
                    "The DefaultConstructor only accepts an array of one String.", null);
        }
        String value = values[0].toString();
        if (type == java.lang.String.class) {
            return value;
        }
        if (type.isPrimitive()) {
            if (type == int.class) {
                return new Integer(value.trim());
            } else if (type == long.class) {
                return new Long(value);
            } else if (type == char.class) {
                if (value.length() > 0) {
                    return Character.valueOf(value.charAt(0));
                }
            } else if (type == byte.class) {
                if (value.length() == 2) {
                    return EncodingTool.hexDecode(value);
                } else {
                    throw new PersistenceException("byte.class cannot decode hex value '"
                            + value + "'", null);
                }
            } else if (type == boolean.class) {
                return parseBoolean(value);
            } else if (type == double.class) {
                return new Double(value);
            } else if (type == float.class) {
                return new Float(value);
            } else if (type == short.class) {
                return new Short(value);
            }
        }
        if (type.getName().startsWith("java.lang.")) {
            if (type == java.lang.Integer.class) {
                return new Integer(value.trim());
            } else if (type == java.lang.Character.class) {
                if (value.length() > 0) {
                    return Character.valueOf(value.charAt(0));
                }
            } else if (type == java.lang.Byte.class) {
                if (value.length() == 2) {
                    return EncodingTool.hexDecode(value);
                }
            } else if (type == java.lang.Boolean.class) {
                return parseBoolean(value);
            } else if (type == java.lang.Class.class) {
                try {
                    return Class.forName(value);
                } catch (ClassNotFoundException ex) {
                    throw new PersistenceException("ClassNotFoundException creating class '"
                            + value + "'", ex);
                }
            } else if (type == java.lang.Double.class) {
                return new Double(value);
            } else if (type == java.lang.Float.class) {
                return new Float(value);
            } else if (type == java.lang.Long.class) {
                return new Long(value);
            } else if (type == java.lang.Object.class) {
                return new Object();
            } else if (type == java.lang.Short.class) {
                return new Short(value);
            } else if (type == java.lang.StringBuffer.class) {
                return new StringBuffer(value);
            }
        }
        return null;
    }

    /**
     * Construct an object based on mapped parameters.
     */
    public Object construct(Class type, Map params) {
        if (params.containsKey("value")) {
            Object[] values = { params.get("value") };
            return construct(type, values);
        } else {
            return construct(type, params.entrySet().toArray());
        }
    }

    /**
     * Serialize an Object to XML.
     */
    public String serialize(Object obj) {
        return obj.toString().trim().replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

}
