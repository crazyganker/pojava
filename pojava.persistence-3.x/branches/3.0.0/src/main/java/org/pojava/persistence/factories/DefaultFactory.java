package org.pojava.persistence.factories;

import org.pojava.exception.PersistenceException;
import org.pojava.util.EncodingTool;

import java.util.Map;

/**
 * The DefaultFactory is a multi-purpose factory for handling objects with single-value
 * constructors, such as primitives.
 *
 * @author John Pile
 */
public class DefaultFactory implements SerialFactory<Object> {

    /**
     * Interpret a Boolean value from a String.
     *
     * @param var String to parse
     * @return Boolean.TRUE or (default) Boolean.FALSE
     */
    private Boolean parseBoolean(String var) {
        if (var == null) {
            return Boolean.FALSE;
        }
        char[] chars = var.toCharArray();
        for (char c : chars) {
            if (c == 'Y' || c == 'y' || c == '1' || c == 'T' || c == 't') {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Construct an object.
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class type, Object[] values) {
        if (values == null || values.length != 1) {
            throw new PersistenceException(
                    "The DefaultConstructor only accepts an array of one String.", null);
        }
        String value = values[0].toString();
        if (type == String.class) {
            return value;
        }
        if (type.isPrimitive()) {
            if (type == int.class) {
                return new Integer(value.trim());
            } else if (type == long.class) {
                return new Long(value);
            } else if (type == char.class) {
                if (value.length() > 0) {
                    return value.charAt(0);
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
            if (type == Integer.class) {
                return new Integer(value.trim());
            } else if (type == Character.class) {
                if (value.length() > 0) {
                    return value.charAt(0);
                }
            } else if (type == Byte.class) {
                if (value.length() == 2) {
                    return EncodingTool.hexDecode(value);
                }
            } else if (type == Boolean.class) {
                return parseBoolean(value);
            } else if (type == Class.class) {
                try {
                    return Class.forName(value);
                } catch (ClassNotFoundException ex) {
                    throw new PersistenceException("ClassNotFoundException creating class '"
                            + value + "'", ex);
                }
            } else if (type == Double.class) {
                return new Double(value);
            } else if (type == Float.class) {
                return new Float(value);
            } else if (type == Long.class) {
                return new Long(value);
            } else if (type == Object.class) {
                return new Object();
            } else if (type == Short.class) {
                return new Short(value);
            } else if (type == StringBuffer.class) {
                return new StringBuffer(value);
            }
        }
        return null;
    }

    /**
     * Construct an object based on mapped parameters.
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class type, Map params) {
        if (params.containsKey("value")) {
            Object[] values = {params.get("value")};
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
