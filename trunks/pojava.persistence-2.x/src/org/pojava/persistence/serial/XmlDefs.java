package org.pojava.persistence.serial;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pojava.datetime.DateTime;
import org.pojava.exception.InconceivableException;
import org.pojava.exception.PersistenceException;
import org.pojava.lang.Accessors;
import org.pojava.persistence.factories.DateFactory;
import org.pojava.persistence.factories.DateTimeFactory;
import org.pojava.persistence.factories.DefaultFactory;
import org.pojava.persistence.factories.SerialFactory;
import org.pojava.util.ReflectionTool;

/**
 * XmlDefs holds configuration settings for serialization.
 * 
 * @author John Pile
 * 
 */
public class XmlDefs {

    /**
     * Factories define how objects are constructed.
     */
    public Map<Class<?>, SerialFactory<?>> factories = new HashMap<Class<?>, SerialFactory<?>>();
    /**
     * The defaultFactory constructs all the primitive equivalents.
     */
    private SerialFactory<?> defaultFactory = new DefaultFactory();
    /**
     * Objects are tracked in the reference map to ensure that the object is referenced rather
     * than duplicated when it is parsed from the XML produced.
     */
    private Map<Object, Integer> referenced = new HashMap<Object, Integer>();
    /**
     * The serialized map show which objects have been serialized, and are candidates for
     * referencing.
     */
    private Map<Object, Integer> serialized = new HashMap<Object, Integer>();
    /**
     * Renamed fields are stored by type.
     */
    private Map<Class<?>, RenamedFields> renamed = new HashMap<Class<?>, RenamedFields>();
    /**
     * Omissions is used to define objects to omit from the serialized document.
     */
    private Map<Class<?>, Set<String>> omissions = new HashMap<Class<?>, Set<String>>();
    /**
     * Properties holds the getters and setters of interest to the serialization process.
     */
    private Map<Class<?>, Map<String, Class<?>>> properties = new HashMap<Class<?>, Map<String, Class<?>>>();
    /**
     * Maps the getters and setters of a class.
     */
    private Map<Class<?>, Accessors> accessors = new HashMap<Class<?>, Accessors>();
    /**
     * The referenceId is a serial number for representing referenced objects.
     */
    private int referenceId = 1;
    /**
     * Defines the number of pad characters used by each indent.
     */
    private int padSize = 2;
    /**
     * Indentation is defined by substring portions of pad.
     */
    private String pad = "                                                                ";
    /**
     * When omitting nulls, null values are not serialized, and the deserializer will accept
     * whatever the default is for that field (which works out well when the default is null).
     */
    private boolean omittingNulls = false;

    private boolean ignoringInvocationTargetException = false;

    private boolean ignoringIllegalAccessException = false;

    /**
     * Initialize XmlDefs with known factory mappings.
     */
    public XmlDefs() {
        registerFactories();
        registerCustomAccessors();
    }

    /**
     * POJava does its best to guess at getters and setters based on whether a method starts
     * with the word "get", "set", or "is".
     * 
     * What of the exceptions to the general rule? You can predefine custom accessors before
     * serializing to either include nonstandard names or exclude unwanted names.
     */
    private void registerCustomAccessors() {
        Accessors getterSetters;
        Map<String, Method> getters, setters;
        Class<?>[] timeParams = { long.class };
        Class<?>[] nanosParams = { int.class };
        Class<?> type;
        try {
            // Timestamp ====================================
            getterSetters = new Accessors();
            getters = getterSetters.getGetters();
            setters = getterSetters.getSetters();
            type = java.sql.Timestamp.class;
            getterSetters.setType(type);
            getters.put("getTime", type.getMethod("getTime", (Class<?>[]) null));
            getters.put("getNanos", type.getMethod("getNanos", (Class<?>[]) null));
            setters.put("setTime", type.getMethod("setTime", timeParams));
            setters.put("setNanos", type.getMethod("setNanos", nanosParams));
            accessors.put(type, getterSetters);
            // java.sql.Date ====================================
            type = java.sql.Date.class;
            getterSetters = new Accessors();
            getters = getterSetters.getGetters();
            setters = getterSetters.getSetters();
            getterSetters.setType(type);
            getters.put("getTime", type.getMethod("getTime", (Class<?>[]) null));
            setters.put("setTime", type.getMethod("setTime", timeParams));
            accessors.put(type, getterSetters);
            type = java.util.Date.class;
            getterSetters = new Accessors();
            getters = getterSetters.getGetters();
            setters = getterSetters.getSetters();
            getterSetters.setType(type);
            getters.put("getTime", type.getMethod("getTime", (Class<?>[]) null));
            setters.put("setTime", type.getMethod("setTime", timeParams));
            accessors.put(type, getterSetters);
        } catch (NoSuchMethodException ex) {
            throw new InconceivableException(
                    "Unless Java discontinued Timestamp, this should never occur.", ex);
        }
    }

    /**
     * Register factories by class.
     */
    private void registerFactories() {
        registerFactory(Date.class, new DateFactory<Date>());
        registerFactory(java.sql.Date.class, new DateFactory<java.sql.Date>());
        registerFactory(Timestamp.class, new DateFactory<Timestamp>());
        registerFactory(DateTime.class, new DateTimeFactory<DateTime>());
        registerFactory(Integer.class, defaultFactory);
        registerFactory(Character.class, defaultFactory);
        registerFactory(Long.class, defaultFactory);
        registerFactory(Byte.class, defaultFactory);
        registerFactory(Boolean.class, defaultFactory);
        registerFactory(Float.class, defaultFactory);
        registerFactory(Short.class, defaultFactory);
        registerFactory(String.class, defaultFactory);
    }

    /**
     * You can add or override your own custom factory for each type.
     * 
     * @param type
     * @param factory
     */
    public void registerFactory(Class<?> type, SerialFactory<?> factory) {
        factories.put(type, factory);
    }

    /**
     * Return a registered factory (or null if unregistered)
     * 
     * @param type
     * @return SerialFactory for given type
     */
    public SerialFactory<?> factory(Class<?> type) {
        return factories.get(type);
    }

    /**
     * Construct an object matching the given params using a custom constructor
     * 
     * @param type
     * @param params
     * @return constructed Object
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class<?> type, Object[] params) {
        SerialFactory factory = factories.get(type);
        if (factory == null) {
            factory = defaultFactory;
        }
        return factory.construct(type, params);
    }

    /**
     * Helper to fill collection from a Map expecting <#,Object>
     * @param col Collection
     * @param params Sequential parameters
     */
    private void fillCollection(Collection<Object> col, Map<?,?> params) {
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                col.add(params.get(Integer.valueOf(i)));
            }
        }
    }

    /**
     * Construct an object using named parameters.
     * 
     * @param type
     * @param params
     * @return constructed Object
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class<?> type, Map params) {
        Object newObj;
        SerialFactory factory = factories.get(type);
        try {
            if (factory == null) {
                if (List.class.equals(type) || Collection.class.equals(type)) {
                    ArrayList list = new ArrayList();
                    fillCollection(list, params);
                    newObj = list;
                    // TODO: Populate from map... embedded List?
                } else if (Set.class.equals(type)) {
                    HashSet set = new HashSet();
                    fillCollection(set, params);
                    newObj = set;
                } else if (Collection.class.isAssignableFrom(type)) {
                    Collection col = (Collection) type.newInstance();
                    fillCollection(col, params);
                    newObj = col;
                } else {
                    newObj = type.newInstance();
                    Accessors accessors = ReflectionTool.accessors(type);
                    ReflectionTool.populateFromMap(newObj, params, accessors.getSetters());
                }
                return newObj;
            } else {
                return factory.construct(type, params);
            }
        } catch (Exception ex) {
            throw new PersistenceException(ex.getMessage(), ex);
        }
    }

    /**
     * True if type is supported by a factory.
     * 
     * @param type
     * @return true if supported
     */
    public boolean isValue(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || factories.containsKey(type);
    }

    /**
     * Determine if object was reference by another object.
     * 
     * @param obj
     * @return true if object is referenced
     */
    public boolean isReferenced(Object obj) {
        return referenced.containsKey(obj);
    }

    /**
     * Get the numeric serial ID of the reference object.
     * 
     * @param obj
     * @return
     */
    public Integer getReferenceId(Object obj) {
        return referenced.get(obj);
    }

    /**
     * Determine if object has been serialized earlier in the stream.
     * 
     * @param obj
     * @return
     */
    public boolean isSerialized(Object obj) {
        return serialized.containsKey(obj);
    }

    /**
     * <pre>
     * Null = Serialize
     *    0 = getReferenceId
     *   1+ = referenceId
     * </pre>
     * 
     * @param obj
     * @return
     */
    public Integer register(Object obj) {
        if (obj == null) {
            return null;
        }
        Class<? extends Object> type = obj.getClass();
        if (!type.isArray() && !hasAccessors(type)
                && !java.util.Collection.class.isAssignableFrom(type)
                && !AbstractMap.class.isAssignableFrom(type)) {
            addAccessors(ReflectionTool.accessors(type));
        }
        Integer refId = null;
        if (serialized.containsKey(obj)) {
            refId = serialized.get(obj);
            if (refId.intValue() == 0) {
                refId = Integer.valueOf(referenceId++);
                serialized.put(obj, refId);
                referenced.put(obj, refId);
            }
        } else if (referenced.containsKey(obj)) {
            serialized.put(obj, referenced.get(obj));
        } else {
            serialized.put(obj, Integer.valueOf(0));
        }
        return refId;
    }

    /**
     * Reset this registry for a new parse.
     */
    public void resetRegistry() {
        serialized.clear();
        referenceId = 1;
    }

    public void rename(Class<?> type, String property, String xmlName) {
        RenamedFields rf;
        if (renamed.containsKey(type)) {
            rf = renamed.get(type);
        } else {
            rf = new RenamedFields();
            renamed.put(type, rf);
        }
        rf.rename(property, xmlName);
    }

    /**
     * 
     * @param type
     * @param name
     * @return
     */
    public String renamedXml(Class<?> type, String name) {
        if (renamed.containsKey(type)) {
            RenamedFields rf = renamed.get(type);
            return rf.javaNameFor(name);
        } else {
            return name;
        }
    }

    /**
     * Keeps track of properties to rename in XML tags.
     * 
     * @param type
     * @param name
     * @return
     */
    public String renamedJava(Class<?> type, String name) {
        if (renamed.containsKey(type)) {
            RenamedFields rf = renamed.get(type);
            return rf.xmlNameFor(name);
        } else {
            return name;
        }
    }

    /**
     * Indent to the given depth.
     * 
     * @param depth
     * @return
     */
    public String indent(int depth) {
        while (pad.length() < depth) {
            pad += pad;
        }
        return pad.substring(0, depth * padSize);
    }

    /**
     * Useful for omitting objects from the XML stream.
     * 
     * @param type
     * @param property
     */
    public void addOmission(Class<?> type, String property) {
        Set<String> propertySet;
        if (omissions.containsKey(type)) {
            propertySet = omissions.get(type);
        } else {
            propertySet = new HashSet<String>();
            omissions.put(type, propertySet);
        }
        propertySet.add(property);
    }

    /**
     * Determine if a given property should be omitted.
     * 
     * @param type
     * @param property
     * @return
     */
    public boolean isOmission(Class<?> type, String property) {
        if (omissions.containsKey(type)) {
            return omissions.get(type).contains(property);
        }
        return false;
    }

    /**
     * Generate (or retrieve from cache) properties for a given class.
     * 
     * @param type
     * @return
     */
    public Map<String, Class<?>> getProperties(Class<?> type) {
        Map<String, Class<?>> props = properties.get(type);
        if (props == null) {
            props = ReflectionTool.propertyMap(type);
            properties.put(type, props);
        }
        return props;
    }

    /**
     * Return true if Accessors have been cached for this class.
     * 
     * @param type
     * @return
     */
    public boolean hasAccessors(Class<? extends Object> type) {
        return accessors.containsKey(type);
    }

    /**
     * Retrieve the cached accessors for this class.
     * 
     * @param type
     * @return
     */
    public Accessors getAccessors(Class<?> type) {
        return accessors.get(type);
    }

    /**
     * Cache accessors for a class.
     * 
     * @param accessor
     */
    public void addAccessors(Accessors accessor) {
        accessors.put(accessor.getType(), accessor);
    }

    /**
     * True if null values are omitted from serialized document.
     * 
     * @return
     */
    public boolean isOmittingNulls() {
        return omittingNulls;
    }

    /**
     * Set false to include null values explicitly in the output.
     * 
     * @param omittingNulls
     */
    public void setOmittingNulls(boolean omittingNulls) {
        this.omittingNulls = omittingNulls;
    }

    /**
     * If true, then fields that throw InvocationTargetException are quietly ignored.
     * 
     * @return true or false
     */
    public boolean isIgnoringInvocationTargetException() {
        return ignoringInvocationTargetException;
    }

    /**
     * If true, then fields that throw InvocationTargetException are quietly ignored.
     */
    public void setIgnoringInvocationTargetException(boolean ignoringInvocationTargetException) {
        this.ignoringInvocationTargetException = ignoringInvocationTargetException;
    }

    /**
     * If true, then fields that throw IllegalAccessException are quietly ignored.
     * 
     * @return true or false
     */
    public boolean isIgnoringIllegalAccessException() {
        return ignoringIllegalAccessException;
    }

    /**
     * If true, then fields that throw IllegalAccessException are quietly ignored.
     * 
     * @param ignoringIllegalAccessException
     *            true to quietly ignore exception.
     */
    public void setIgnoringIllegalAccessException(boolean ignoringIllegalAccessException) {
        this.ignoringIllegalAccessException = ignoringIllegalAccessException;
    }

}
