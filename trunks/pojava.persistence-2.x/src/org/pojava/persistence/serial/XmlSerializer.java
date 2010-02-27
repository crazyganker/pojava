package org.pojava.persistence.serial;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.pojava.exception.PersistenceException;
import org.pojava.lang.Accessors;
import org.pojava.persistence.factories.SerialFactory;
import org.pojava.util.ReflectionTool;

/**
 * The XmlSerializer class converts a Java Object to XML.
 * 
 * @author John Pile
 * 
 */
public class XmlSerializer {

    /**
     * A reference to the configuration object.
     */
    private XmlDefs config;

    /**
     * Construct a new serializer
     */
    public XmlSerializer() {
        config = new XmlDefs();
    }

    /**
     * Construct an XmlSerializer using a custom configuration object.
     * 
     * Be careful not to use the same configuration object concurrently.
     * 
     * @param config
     */
    public XmlSerializer(XmlDefs config) {
        this.config = config;
    }

    /**
     * Walk the tree of objects, gathering types and checking for circular references. Circular
     * references are supported, but must be handled correctly.
     * 
     * @param pojo
     */
    private void walk(Object pojo) {
        if (pojo == null) {
            return;
        }
        if (!ReflectionTool.isBasic(pojo.getClass())) {
            Integer ref = config.register(pojo);
            if (ref != null) {
                return;
            }
        }
        Class<?> type = pojo.getClass();
        if (ReflectionTool.isBasic(type) || type.equals(Object.class) || type.equals(Class.class)) {
            return;
        } else if (config.hasAccessors(type)) {
            Map<String, Method> getters = config.getAccessors(type).getGetters();
            for (Iterator<Entry<String, Method>> it = getters.entrySet().iterator(); it
                    .hasNext();) {
                Entry<String, Method> entry = it.next();
                // String key = entry.getKey();
                // String property = ReflectionTool.propertyFor(key);
                String property = entry.getKey();
                if (!config.isOmission(type, property)) {
                    try {
                        Object obj = entry.getValue().invoke(pojo, (Object[]) null);
                        walk(obj);
                    } catch (IllegalAccessException ex) {
                        if (!config.isIgnoringIllegalAccessException()) {
                            throw new PersistenceException("Could not serialize "
                                    + pojo.getClass().getName(), ex);
                        }
                    } catch (InvocationTargetException ex2) {
                        if (!config.isIgnoringInvocationTargetException()) {
                            throw new PersistenceException("Could not serialize "
                                    + pojo.getClass().getName(), ex2);
                        }
                    }
                }
            }
        } else if (java.util.Collection.class.isAssignableFrom(type)) {
            Collection<?> collection = (Collection<?>) pojo;
            for (Iterator<?> listIter = collection.iterator(); listIter.hasNext();) {
                walk(listIter.next());
            }
        } else if (java.util.AbstractMap.class.isAssignableFrom(type)) {
            Map<?, ?> map = (Map<?, ?>) pojo;
            for (Iterator<?> mapIter = map.keySet().iterator(); mapIter.hasNext();) {
                Object mapKey = mapIter.next();
                walk(mapKey);
                walk(map.get(mapKey));
            }
        } else if (type.isArray()) {
            int length = Array.getLength(pojo);
            for (int i = 0; i < length; i++) {
                walk(Array.get(pojo, i));
            }
        } else {
            Accessors accessors = ReflectionTool.accessors(type);
            config.addAccessors(accessors);
            Map<String, Method> getters = accessors.getGetters();
            for (Iterator<String> it = getters.keySet().iterator(); it.hasNext();) {
                try {
                    Method meth = getters.get(it.next());
                    walk((Object) meth.invoke(pojo, (Object[]) null));
                } catch (InvocationTargetException ex1) {
                    throw new PersistenceException("Couldn't walk. " + ex1.toString(), ex1);
                } catch (IllegalAccessException ex2) {
                    throw new PersistenceException("Couldn't walk. " + ex2.toString(), ex2);
                }
            }
        }
    }

    /**
     * Convert an Object tree to XML.
     * 
     * @param obj
     * @return
     */
    public String toXml(Object obj) {
        walk(obj);
        config.resetRegistry();
        return toXml(obj, null, null, 0, null);
    }

    /**
     * This performs the actual work of serializing to xml.
     * 
     * @param pojo
     * @param name
     * @param attribs
     * @param depth
     * @param baseClass
     * @return XML document as a String.
     */
    @SuppressWarnings("unchecked")
    private String toXml(Object pojo, String name, String attribs, int depth, Class<?> baseClass) {
        StringBuffer sb;
        if (pojo == null) {
            if (config.isOmittingNulls()) {
                return "";
            } else {
                sb = new StringBuffer();
                openTag(sb, name, attribs, depth);
                sb.append("<null/>");
                closeTag(sb, name);
                return sb.toString();
            }
        }
        sb = new StringBuffer();
        StringBuffer attribSb = new StringBuffer();
        Class<?> type = pojo.getClass();
        if (attribs != null) {
            attribSb.append(attribs);
        }
        // An unnamed object must specify its type
        if (name == null || name.length() == 0) {
            attribSb.append(" class=\"");
            attribSb.append(className(pojo));
            attribSb.append('"');
            name = "obj";
        }
        if (baseClass == null) {
            baseClass = pojo.getClass();
        }
        Integer refId = null;
        // Objects referenced multiple times get special attention
        if (!ReflectionTool.isBasic(type)) {
            refId = config.getReferenceId(pojo);
            if (refId != null) {
                // This ref comparison is intentional.
                if (refId != config.register(pojo)) {
                    refId = config.getReferenceId(pojo);
                    attribSb.append(" mem=\"");
                    attribSb.append(refId);
                    attribSb.append("\"");
                } else {
                    // Subsequent references are referenced.
                    sb.append(config.indent(depth));
                    name = config.renamedJava(type, name);
                    sb.append("<");
                    sb.append(name);
                    sb.append(" ref=\"");
                    sb.append(refId);
                    sb.append("\"/>\n");
                    return sb.toString();
                }
            }
        }
        // Simple objects can be overridden with a factory
        SerialFactory<Object> override = (SerialFactory<Object>) config.factory(type);
        if (override != null) {
            name = config.renamedJava(baseClass, name);
            openTag(sb, name, attribSb.toString(), depth);
            sb.append(override.serialize(pojo));
            closeTag(sb, name);
            return sb.toString();
        }
        if (ReflectionTool.isBasic(type) || type.isEnum()) {
            sb.append(config.indent(depth));
            sb.append(simpleElement(pojo, name, attribSb.toString()));
        } else if (type.equals(Object.class)) {
            sb.append(snippetFromUntyped(pojo, name, attribSb.toString(), depth, baseClass));
        } else if (baseClass.equals(Object.class)
                && java.util.Collection.class.isAssignableFrom(type)) {
            sb.append(snippetFromUntyped(pojo, name, attribSb.toString(), depth, baseClass));
        } else if (java.util.Collection.class.isAssignableFrom(type)) {
            sb.append(snippetFromCollection(pojo, name, attribSb.toString(), depth));
        } else if (java.util.AbstractMap.class.isAssignableFrom(type)) {
            sb.append(snippetFromMap(pojo, name, attribSb.toString(), depth));
        } else if (type.isArray()) {
            sb.append(snippetFromArray(pojo, name, attribSb.toString(), depth, baseClass));
        } else {
            sb.append(snippetFromPojo(pojo, name, attribSb.toString(), depth, baseClass));
        }
        return sb.toString();
    }

    /**
     * Return class type, abbreviating for common classes.
     * 
     * @param obj
     * @return
     */
    private String className(Object obj) {
        String name = obj.getClass().getName();
        if (name.startsWith("java.lang."))
            return name.substring(10);
        if (name.equals("org.pojava.datetime.DateTime"))
            return "DateTime";
        return name;
    }

    /**
     * Return a simple one-line element.
     * 
     * @param name
     *            Simplified getter name
     * @param value
     *            Value to assign
     * @return
     * @author John Pile
     */
    private String simpleElement(Object value, String name, String attribs) {
        String inner = ReflectionTool.clean(value.toString());
        StringBuffer sb = new StringBuffer();
        sb.append('<');
        sb.append(name);
        sb.append(attribs);
        if (inner.length() == 0) {
            sb.append("/>\n");
        } else {
            sb.append('>');
            sb.append(ReflectionTool.clean(inner));
            closeTag(sb, name);
        }
        return sb.toString();
    }

    /**
     * Returns an xml snippet from an untyped Object.
     * 
     * @param pojo
     *            Collection object such as a List
     * @param name
     *            Name of array
     * @param attribs
     *            Attributes.
     * @return
     */
    private String snippetFromUntyped(Object pojo, String name, String attribs, int depth,
            Class<?> baseClass) {
        StringBuffer sb = new StringBuffer();
        if (pojo == null) {
            if (config.isOmittingNulls()) {
                return "";
            }
            openTag(sb, name, null, depth);
            sb.append("<null/>");
            closeTag(sb, name);
            return sb.toString();
        }
        boolean isColl = java.util.Collection.class.isAssignableFrom(pojo.getClass());
        if (isColl) {
            if (attribs == null || attribs.length() == 0) {
                openTag(sb, name, null, depth);
                sb.append('\n');
            }
        } else {
            if (pojo.getClass() == Object.class) {
                sb.append(config.indent(depth));
                sb.append('<');
                sb.append(name);
                sb.append(attribs);
                sb.append("/>\n");
                return sb.toString();
            }
            openTag(sb, name, attribs, depth);
            sb.append('\n');
        }
        Class<?> memberClass = pojo.getClass();
        if (memberClass != Object.class) {
            sb.append(toXml(pojo, null, null, depth + 1, baseClass));
        }
        if (!isColl || attribs == null || attribs.length() == 0) {
            sb.append(config.indent(depth));
            closeTag(sb, name);
        }
        return sb.toString();
    }

    /**
     * Returns an xml snippet from a Collection.
     * 
     * @param pojo
     *            Collection object such as a List
     * @param name
     *            Name of array
     * @param attribs
     *            Attributes.
     * @return
     */
    private String snippetFromCollection(Object pojo, String name, String attribs, int depth) {
        StringBuffer sb = new StringBuffer();
        openTag(sb, name, attribs, depth);
        sb.append('\n');
        Collection<?> collection = (Collection<?>) pojo;
        int counter = 1;
        for (Iterator<?> listIter = collection.iterator(); listIter.hasNext();) {
            Object member = listIter.next();
            if (member == null) {
                sb.append("<obj class=\"null\"/>\n");
            } else {
                sb.append(toXml(member, null, null, depth + 1, member.getClass()));
            }
            counter++;
        }
        sb.append(config.indent(depth));
        closeTag(sb, name);
        return sb.toString();
    }

    /**
     * Returns an xml snippet from a Map.
     * 
     * @param pojo
     *            Map object
     * @param heap
     *            Index of rendered objects.
     * @param name
     *            Name of array
     * @param attribs
     *            Optional type attribute.
     * @return
     */
    private String snippetFromMap(Object pojo, String name, String attribs, int depth) {
        StringBuffer sb = new StringBuffer();
        openTag(sb, name, attribs, depth);
        sb.append('\n');
        Map<?, ?> map = (Map<?, ?>) pojo;
        for (Iterator<?> listIter = map.entrySet().iterator(); listIter.hasNext();) {
            sb.append(config.indent(depth + 1));
            sb.append("<map>\n");
            Map.Entry<?, ?> entry = (Entry<?, ?>) listIter.next();
            // Map the key
            Object mapKey = entry.getKey();
            sb.append(toXml(mapKey, null, null, depth + 2, mapKey.getClass()));
            // Map the value
            Object mapValue = entry.getValue();
            if (mapValue == null) {
                sb.append(config.indent(depth + 2));
                sb.append("<null/>\n");
            } else {
                sb.append(toXml(mapValue, null, null, depth + 2, mapValue.getClass()));
            }
            sb.append(config.indent(depth + 1));
            sb.append("</map>\n");
        }
        sb.append(config.indent(depth));
        closeTag(sb, name);
        return sb.toString();
    }

    /**
     * Returns an xml snippet from an array.
     * 
     * @param pojo
     *            Array object
     * @param heap
     *            Index of rendered objects.
     * @param name
     *            Name of array
     * @param attribs
     *            Optional type attribute.
     * @return
     */
    private String snippetFromArray(Object pojo, String name, String attribs, int depth,
            Class<?> baseClass) {
        StringBuffer sb = new StringBuffer();
        int length = Array.getLength(pojo);
        name = config.renamedJava(baseClass, name);
        openTag(sb, name, attribs, depth);
        sb.append('\n');
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                Object member = Array.get(pojo, i);
                // Output array element
                if (member==null) {
                    sb.append(toXml(null, "e", null, depth + 1, null));
                } else {
                    sb.append(toXml(member, "e", null, depth + 1, member.getClass()));
                }
            }
        }
        sb.append(config.indent(depth));
        closeTag(sb, name);
        return sb.toString();
    }

    /**
     * Returns an xml snippet from a pojo.
     * 
     * @param pojo
     *            Object to render as xml.
     * @param name
     *            Name of object
     * @param attribs
     *            Blank or " type=\"dot\""
     * @param depth
     *            Depth used for indentation.
     * @param baseClass
     *            Um...
     * @return
     */
    private String snippetFromPojo(Object pojo, String name, String attribs, int depth,
            Class<?> baseClass) {
        StringBuffer sb = new StringBuffer();
        String renamed = config.renamedJava(baseClass, name);
        if (renamed == null) {
            openTag(sb, name, attribs, depth);
        } else {
            openTag(sb, renamed, attribs, depth);
        }
        sb.append('\n');
        Class<?> type = pojo.getClass();
        // TODO: if type is Class.class then handle as ENUM.
        try {
            Accessors accessors = config.getAccessors(type);
            
            Map<String, Method> getters = accessors.getGetters();
            for (Iterator<Map.Entry<String, Method>> it = getters.entrySet().iterator(); it
                    .hasNext();) {
                Map.Entry<String, Method> entry = it.next();
                String property = entry.getKey();
                Method getter = entry.getValue();
                Class<?> fieldClass = getter.getReturnType();

                if (!config.isOmission(type, property)) {
                    Object innerPojo = getters.get(property).invoke(pojo, (Object[]) null);
                    if (fieldClass == Object.class) {
                        sb.append(snippetFromUntyped(innerPojo, property, "", depth + 1,
                                fieldClass));
                    } else {
                        sb.append(toXml(innerPojo, property, null, depth + 1, baseClass));
                    }

                }
            }
        } catch (InvocationTargetException ex) {
            if (!config.isIgnoringInvocationTargetException()) {
                throw new PersistenceException("Could not serialize. " + ex.toString(), ex);
            }
        } catch (IllegalAccessException ex) {
            if (!config.isIgnoringIllegalAccessException()) {
                throw new PersistenceException("Could not serialize. " + ex.toString(), ex);
            }
        }
        sb.append(config.indent(depth));
        if (renamed == null) {
            closeTag(sb, name);
        } else {
            closeTag(sb, renamed);
        }
        return sb.toString();
    }

    /**
     * Append indentation and an open tag to StringBuffer.
     * 
     * @param sb
     * @param name
     * @param attribs
     */
    private void openTag(StringBuffer sb, String name, String attribs, int depth) {
        sb.append(config.indent(depth));
        sb.append('<');
        sb.append(name);
        if (attribs != null) {
            sb.append(attribs);
        }
        sb.append(">");
    }

    /**
     * Append a closing tag to StringBuffer.
     * 
     * @param sb
     * @param name
     */
    private void closeTag(StringBuffer sb, String name) {
        sb.append("</");
        sb.append(name);
        sb.append(">\n");
    }

}
