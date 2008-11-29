package org.pojava.persistence.serial;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pojava.datetime.DateTime;
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
 * Note that this is not reentrant.
 *  
 * @author John Pile
 *
 */
public class XmlDefs {

	/**
	 * Factories define how objects are constructed.
	 */
	public Map factories = new HashMap();
	/**
	 * The defaultFactory constructs all the primitive equivalents. 
	 */
	private SerialFactory defaultFactory = new DefaultFactory();
	/**
	 * The referenceId is a serial number for representing referenced objects.
	 */
	private int referenceId = 0;
	/**
	 * Objects are tracked in the reference map to ensure that the object is
	 * referenced rather than duplicated when it is parsed from the XML produced.
	 */
	private Map referenced = new HashMap();
	/**
	 * The serialized map show which objects have been serialized, and are candidates
	 * for referencing.  
	 */
	private Map serialized = new HashMap();
	/**
	 * The renamedXml map keeps track of name changes from xml to java.
	 */
	private Map renamedXml = new HashMap();
	/**
	 * The renamedJava map keeps track of name changes from java to xml.
	 */
	private Map renamedJava = new HashMap();
	/**
	 * Omissions is used to define objects to omit from the serialized document. 
	 */
	private Set omissions = new HashSet();
	/**
	 * Properties holds the getters and setters of interest to the serialization process. 
	 */
	private Map properties = new HashMap();
	/**
	 * Defines the number of pad characters used by each indent.
	 */
	private int padSize = 2;
	/**
	 * Indentation is defined by substring portions of pad. 
	 */
	private String pad = "                                                               ";
	
	/**
	 * Initialize XmlDefs with known factory mappings.
	 */
	public XmlDefs() {
		add(Date.class, new DateFactory());
		add(DateTime.class, new DateTimeFactory());
		add(Integer.class, defaultFactory);
		add(Character.class, defaultFactory);
		add(Long.class, defaultFactory);
		add(Byte.class, defaultFactory);
		add(Boolean.class, defaultFactory);
		add(Float.class, defaultFactory);
		add(Short.class, defaultFactory);
		add(String.class, defaultFactory);
	}

	/**
	 * You can add or override your own custom factory for each type.
	 * @param type
	 * @param factory
	 */
	public void add(Class type, SerialFactory factory) {
		factories.put(type, factory);
	}

	/**
	 * Return a registered factory (or null if unregistered)
	 * @param type
	 * @return SerialFactory for given type
	 */
	public SerialFactory factory(Class type) {
		return (SerialFactory) factories.get(type);
	}

	/**
	 * Construct an object matching the given params using a custom constructor
	 * @param type
	 * @param params
	 * @return constructed Object
	 */
	public Object construct(Class type, Object[] params) {
		SerialFactory factory = (SerialFactory) factories.get(type);
		if (factory == null) {
			factory = defaultFactory;
		}
		return factory.construct(type, params);
	}
	
	/**
	 * Construct an object using named parameters.
	 * @param type
	 * @param params
	 * @return constructed Object
	 */
	public Object construct(Class type, Map params) {
		Object newObj;
		SerialFactory factory = (SerialFactory) factories.get(type);
		try {
		if (factory == null) {
			newObj=type.newInstance();
			Accessors accessors=ReflectionTool.accessors(type);
			ReflectionTool.populateFromMap(newObj, params, accessors.getSetters());
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
	 * @param type
	 * @return true if supported
	 */
	public boolean isValue(Class type) {
		if (type==null) {
			return false;
		}
		return type.isPrimitive() || factories.containsKey(type);
	}

	/**
	 * Determine if object was reference by another object.
	 * @param obj
	 * @return true if object is referenced
	 */
	public boolean isReferenced(Object obj) {
		return referenced.containsKey(obj);
	}

	/**
	 * Get the numeric serial ID of the reference object.
	 * @param obj
	 * @return
	 */
	public Integer getReferenceId(Object obj) {
		return (Integer) referenced.get(obj);
	}

	/**
	 * Determine if object has been serialized earlier in the stream.
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
		Integer refId = null;
		if (serialized.containsKey(obj)) {
			refId = (Integer) serialized.get(obj);
			if (refId.intValue() == 0) {
				refId = new Integer(this.referenceId++);
				serialized.put(obj, refId);
				referenced.put(obj, refId);
			}
		} else {
			serialized.put(obj, new Integer(0));
		}
		return refId;
	}

	/**
	 * Reset this registry for a new parse.
	 */
	public void resetRegistry() {
		serialized.clear();
		referenceId=1;
	}

	/**
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	public String renamedXml(Class type, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(type.getName());
		sb.append(' ');
		sb.append(name);
		return (String) renamedXml.get(sb.toString());
	}

	/**
	 * Keeps track of properties to rename in XML tags.
	 * @param type
	 * @param name
	 * @return
	 */
	public String renamedJava(Class type, String name) {
		return (String) renamedJava.get(key(type, name));
	}

	/**
	 * Keeps track of XML tags to rename when matching to properties.
	 * @param type
	 * @param name
	 * @return tag equivalent of property
	 */
	public static String key(Class type, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(type.getName());
		sb.append(' ');
		sb.append(name);
		return sb.toString();
	}

	/**
	 * Indent to the given depth.
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
	 * @param type
	 * @param property
	 */
	public void addOmission(Class type, String property) {
		omissions.add(key(type, property));
	}

	/**
	 * Determine if a given property should be omitted.
	 * @param type
	 * @param property
	 * @return
	 */
	public boolean isOmission(Class type, String property) {
		return omissions.contains(key(type, property));
	}

	/**
	 * Generate (or retrieve from cache) properties for a given class.
	 * @param type
	 * @return
	 */
	public Map getProperties(Class type) {
		Map props = (Map) properties.get(type);
		if (props == null) {
			props = ReflectionTool.propertyMap(type);
			properties.put(type, props);
		}
		return props;
	}
}
