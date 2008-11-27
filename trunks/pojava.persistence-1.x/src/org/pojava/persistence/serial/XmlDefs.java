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

public class XmlDefs {

	public Map factories = new HashMap();
	private SerialFactory defaultFactory = new DefaultFactory();
	private Map xmlSerializers = new HashMap();
	private int referenceId = 0;
	private Map referenced = new HashMap();
	private Map serialized = new HashMap();
	private Map renamedXml = new HashMap();
	private Map renamedJava = new HashMap();
	private Set omissions = new HashSet();
	private Map properties = new HashMap();
	private int padSize = 2;
	private String pad = "                                                               ";
	
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

	public void add(Class type, SerialFactory factory) {
		factories.put(type, factory);
	}

	public SerialFactory override(Class type) {
		return (SerialFactory) factories.get(type);
	}

	public Object construct(Class type, Object[] params) {
		SerialFactory factory = (SerialFactory) factories.get(type);
		if (factory == null) {
			factory = defaultFactory;
		}
		return factory.construct(type, params);
	}
	
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
	
	public Object construct(Class type, StringBuffer param) {
		SerialFactory factory = (SerialFactory) factories.get(type);
		if (factory == null) {
			factory = defaultFactory;
		}
		Object[] params = new Object[1];
		params[0]=param.toString();
		return factory.construct(type, params);
	}

	
	/*
	public boolean isValueBean(String name) {
		return valueBeans.contains(name);
	}
*/
	public boolean isValue(Class type) {
		if (type==null) {
			return false;
		}
		return type.isPrimitive() || factories.containsKey(type);
	}

	public void addXmlSerializer(Class type, XmlSerializer serializer) {
		xmlSerializers.put(type, serializer);
	}

	public XmlSerializer getXmlSerializer(Class type) {
		XmlSerializer serializer = (XmlSerializer) xmlSerializers.get(type);
		if (serializer == null) {
			serializer = new XmlSerializer(type, this);
			xmlSerializers.put(type, serializer);
			return serializer;
		}
		return (XmlSerializer) xmlSerializers.get(type);
	}

	public boolean isReferenced(Object obj) {
		return referenced.containsKey(obj);
	}

	public Integer getReferenceId(Object obj) {
		return (Integer) referenced.get(obj);
	}

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

	public void resetRegistry() {
		serialized.clear();
		referenceId=1;
	}

	public String renamedXml(Class type, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(type.getName());
		sb.append(' ');
		sb.append(name);
		return (String) renamedXml.get(sb.toString());
	}

	public String renamedJava(Class type, String name) {
		return (String) renamedJava.get(key(type, name));
	}

	public static String key(Class type, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(type.getName());
		sb.append(' ');
		sb.append(name);
		return sb.toString();
	}

	public String indent(int depth) {
		if (pad.length() < depth) {
			pad += pad;
		}
		return pad.substring(0, depth * padSize);
	}

	public void addOmission(Class type, String property) {
		omissions.add(key(type, property));
	}

	public boolean isOmission(Class type, String property) {
		return omissions.contains(key(type, property));
	}

	public Map getProperties(Class type) {
		Map props = (Map) properties.get(type);
		if (props == null) {
			props = ReflectionTool.propertyMap(type);
			properties.put(type, props);
		}
		return props;
	}
}
