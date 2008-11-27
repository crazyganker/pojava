package org.pojava.persistence.serial;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
	 * Depth keeps track of nested depth in the XML.
	 */
	private int depth = 0;
	/**
	 * The current class being operated upon by this XmlSerializer.
	 */
	private Class baseClass = null;
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
	 * Construct a new serializer for a particular type.
	 * @param type
	 */
	public XmlSerializer(Class type) {
		this.baseClass = type;
		config = new XmlDefs();
	}

	/**
	 * Construct an XmlSerializer using a custom configuration object.
	 * 
	 * Be careful not to use the same configuration object concurrently.
	 * @param type
	 * @param config
	 */
	public XmlSerializer(Class type, XmlDefs config) {
		this.baseClass = type;
		this.config = config;
	}

	/**
	 * You can use this method to create a serializer with a different indentation.
	 * @param keyClass
	 * @param depth
	 * @return
	 */
	public XmlSerializer newXmlSerializer(Class keyClass, int depth) {
		XmlSerializer xmlSerializer = new XmlSerializer(keyClass);
		xmlSerializer.depth = depth;
		xmlSerializer.config = config;
		return xmlSerializer;
	}

	/**
	 * Walk the tree of objects, gathering types and checking for circular
	 * references.  Circular references are supported, but must be handled correctly.
	 * 
	 * @param pojo
	 */
	private void walk(Object pojo) {
		if (pojo == null) {
			return;
		}
		if (!isBasic(pojo.getClass())) {
			Integer ref = config.register(pojo);
			if (ref != null) {
				return;
			}
		}
		Class type = pojo.getClass();
		if (isBasic(type) || type.equals(Object.class)) {
			return;
		} else if (java.util.Collection.class.isAssignableFrom(type)) {
			Collection collection = (Collection) pojo;
			for (Iterator listIter = collection.iterator(); listIter.hasNext();) {
				walk(listIter.next());
			}
		} else if (java.util.AbstractMap.class.isAssignableFrom(type)) {
			Map map = (Map) pojo;
			for (Iterator mapIter = map.keySet().iterator(); mapIter.hasNext();) {
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
			Map props = config.getProperties(type);
			for (Iterator mapIter = props.keySet().iterator(); mapIter
					.hasNext();) {
				String key = (String) mapIter.next();
				if (!config.isOmission(pojo.getClass(), key)) {
					walk(ReflectionTool.getNestedValue(key, pojo));
				}
			}
		}
	}

	/**
	 * Convert an Object tree to XML.
	 * @param obj
	 * @return
	 */
	public String toXml(Object obj) {
		walk(obj);
		config.resetRegistry();
		return toXml(obj, null, null);
	}

	/**
	 * This performs the actual work assuming preparation has been done.
	 * @param pojo
	 * @param name
	 * @param attribs
	 * @return XML document as a String.
	 */
	private String toXml(Object pojo, String name, String attribs) {
		if (pojo==null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer attribSb = new StringBuffer();
		Class type = pojo.getClass();
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
		// A null object gets an early out
		if (pojo == null) {
			sb.append(config.indent(depth));
			sb.append('<');
			sb.append(name);
			sb.append("><null/></");
			sb.append(name);
			sb.append(">\n");
			return sb.toString();
		}
		if (baseClass==null) {
			baseClass=pojo.getClass();
		}
		Integer refId = null;
		if (!isBasic(pojo.getClass())) {
			refId=config.register(pojo);
		}
		// Objects referenced multiple times get special attention
		if (!isBasic(type)) {
			refId=config.getReferenceId(pojo);
			if (refId!=null) {
				if (refId!=config.register(pojo)) {
					refId=config.getReferenceId(pojo);
					attribSb.append(" mem=\"");
					attribSb.append(refId);
					attribSb.append("\"");
				} else {
					// Subsequent references are referenced.
					sb.append(config.indent(depth));
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
		SerialFactory override=config.factory(type);
		if (override!=null) {
			openTag(sb, name, attribSb.toString());
			sb.append(override.serialize(pojo));
			closeTag(sb, name);
			return sb.toString();
		}
		if (isBasic(type)) {
			sb.append(config.indent(depth));
			sb.append(simpleElement(pojo, name, attribSb.toString()));
		} else if (type.equals(Object.class)) {
			sb.append(snippetFromUntyped(pojo, name, attribSb.toString()));
		} else if (baseClass.equals(Object.class)
				&& java.util.Collection.class.isAssignableFrom(type)) {
			sb.append(snippetFromUntyped(pojo, name, attribSb.toString()));
		} else if (java.util.Collection.class.isAssignableFrom(type)) {
			sb.append(snippetFromCollection(pojo, name, attribSb.toString()));
		} else if (java.util.AbstractMap.class.isAssignableFrom(type)) {
			sb.append(snippetFromMap(pojo, name, attribSb.toString()));
		} else if (type.isArray()) {
			sb.append(snippetFromArray(pojo, name, attribSb.toString()));
		} else {
			sb.append(snippetFromPojo(pojo, name, attribSb.toString()));
		}
		return sb.toString();
	}

	/**
	 * Return class type, abbreviating for common classes.
	 * @param obj
	 * @return
	 */
	private String className(Object obj) {
		String name = obj.getClass().getName();
		if (name.startsWith("java.lang."))
			return name.substring(10);
		if (name.equals("org.pojava.datetime.DateTime")) {
			return "DateTime";
		}
		return name;
	}

	/**
	 * Return true if object is equivalent to a primitive type.
	 * @param propClass
	 * @return
	 */
	private static boolean isBasic(Class propClass) {
		return propClass.isPrimitive() || propClass == String.class
				|| propClass == Integer.class || propClass == Double.class
				|| propClass == Boolean.class || propClass == Long.class
				|| propClass == Float.class || propClass == Short.class
				|| propClass == Byte.class || propClass == Character.class;
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
		String inner = cleanAttribute(value.toString());
		StringBuffer sb = new StringBuffer();
		sb.append('<');
		sb.append(name);
		sb.append(attribs);
		if (inner.length() == 0) {
			sb.append("/>\n");
		} else {
			sb.append('>');
			sb.append(cleanAttribute(inner));
			closeTag(sb, name);
		}
		return sb.toString();
	}

	/**
	 * Make an attribute safe by converting illegal characters.
	 * @param attrib
	 * @return
	 */
	private String cleanAttribute(String attrib) {
		if (attrib == null) {
			return "";
		}
		return attrib.trim().replaceAll("&", "&amp;")
				.replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(
						">", "&gt;");
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
	private String snippetFromUntyped(Object pojo, String name, String attribs) {
		StringBuffer sb = new StringBuffer();
		boolean isColl = pojo != null
				&& java.util.Collection.class.isAssignableFrom(pojo.getClass());
		if (isColl) {
			if (attribs == null || attribs.length() == 0) {
				openTag(sb, name, null);
				sb.append('\n');
			}
		} else {
			if (pojo.getClass()==Object.class) {
				sb.append(config.indent(depth));
				sb.append('<');
				sb.append(name);
				sb.append(attribs);
				sb.append("/>\n");
				return sb.toString();
			}
			openTag(sb, name, attribs);
			sb.append('\n');
		}
		int saveDepth=depth++;
		if (pojo == null) {
			sb.append(config.indent(depth));
			sb.append("<obj class=\"null\"/>\n");
		} else {
			Class memberClass = pojo.getClass();
			if (memberClass != Object.class) {
				XmlSerializer subSerializer = config
						.getXmlSerializer(memberClass);
				subSerializer.depth=depth;
				sb.append(subSerializer.toXml(pojo, null, null));
			}
		}
		depth=saveDepth;
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
	private String snippetFromCollection(Object pojo, String name,
			String attribs) {
		StringBuffer sb = new StringBuffer();
		openTag(sb, name, attribs);
		sb.append('\n');
		Collection collection = (Collection) pojo;
		int counter = 1;
		for (Iterator listIter = collection.iterator(); listIter.hasNext();) {
			Object member = listIter.next();
			if (member == null) {
				sb.append("<obj class=\"null\"/>\n");
			} else {
				Class memberClass = member.getClass();
				XmlSerializer subSerializer = (XmlSerializer) config
						.getXmlSerializer(memberClass);
				if (subSerializer == null) {
					subSerializer = newXmlSerializer(memberClass,depth);
					config.addXmlSerializer(memberClass, subSerializer);
				}
				subSerializer.depth=depth+1;
				sb.append(subSerializer.toXml(member, null, null));
			}
			counter++;
		}
		sb.append(config.indent(depth));
		closeTag(sb,name);
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
	private String snippetFromMap(Object pojo, String name, String attribs) {
		StringBuffer sb = new StringBuffer();
		openTag(sb, name, attribs);
		sb.append('\n');
		Map map = (Map) pojo;
		for (Iterator listIter = map.keySet().iterator(); listIter.hasNext();) {
			sb.append(config.indent(depth+1));
			sb.append("<map>\n");
			// Map the key
			Object mapKey = listIter.next();
			Class keyClass = mapKey.getClass();
			XmlSerializer subSerializer = (XmlSerializer) config
					.getXmlSerializer(keyClass);
			if (subSerializer == null) {
				subSerializer = newXmlSerializer(keyClass, depth+2);
				config.addXmlSerializer(keyClass, subSerializer);
			}
			subSerializer.depth=depth+2;
			sb.append(subSerializer.toXml(mapKey, null, null));
			// Map the value
			Object mapValue = map.get(mapKey);
			if (mapValue == null) {
				sb.append(config.indent(depth+2));
				sb.append("<null/>\n");
			} else {
				Class valueClass = mapValue.getClass();
				subSerializer = (XmlSerializer) config
						.getXmlSerializer(valueClass);
				if (subSerializer == null) {
					subSerializer = newXmlSerializer(valueClass,depth+1);
					config.addXmlSerializer(valueClass, subSerializer);
				}
				subSerializer.depth=depth+2;
				sb.append(subSerializer.toXml(mapValue, null, null));
			}
			sb.append(config.indent(depth+1));
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
	private String snippetFromArray(Object pojo, String name, String attribs) {
		StringBuffer sb = new StringBuffer();
		int length = Array.getLength(pojo);
		if (length > 0) {
			openTag(sb, name, attribs);
			sb.append('\n');
			for (int i = 0; i < length; i++) {
				Object member = Array.get(pojo, i);
				Class memberClass = member.getClass();
				XmlSerializer subSerializer = (XmlSerializer) config
						.getXmlSerializer(memberClass);
				if (subSerializer == null) {
					subSerializer = newXmlSerializer(memberClass, depth+1);
					config.addXmlSerializer(memberClass, subSerializer);
				}
				sb.append(config.indent(depth+1));
				sb.append(subSerializer.toXml(member, null, null));
			}
			sb.append(config.indent(depth));
			closeTag(sb, name);
		}
		return sb.toString();
	}

	/**
	 * Returns an xml snippet from a pojo.
	 * 
	 * @param pojo
	 *            Object to render as xml.
	 * @param heap
	 *            Index of rendered objects.
	 * @param name
	 *            Name of object
	 * @param attribs
	 *            Blank or " type=\"dot\""
	 * @return
	 */
	private String snippetFromPojo(Object pojo, String name, String attribs) {
		StringBuffer sb = new StringBuffer();
		int saveDepth=depth;
		openTag(sb, name, attribs);
		sb.append('\n');
		depth++;
		Map properties = config.getProperties(pojo.getClass());
		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (!config.isOmission(pojo.getClass(), key)) {
				Class fieldClass = (Class) properties.get(key);
				XmlSerializer childMapper; //  = this;
				// if (!isBasic(fieldClass)) {
					childMapper = (XmlSerializer) config
							.getXmlSerializer(fieldClass);
					childMapper.depth=depth;
					if (childMapper == null) {
						childMapper = newXmlSerializer(fieldClass,depth+1);
						config.addXmlSerializer(fieldClass, childMapper);
					}
				// }
				// renames might need to occur here.
				Object innerPojo = ReflectionTool.getNestedValue(key, pojo);
				String renamed = config.renamedJava(fieldClass, key);
				if (renamed != null) {
					key = renamed;
				}
				if (fieldClass == Object.class) {
					sb.append(snippetFromUntyped(innerPojo, key, ""));
				} else {
					sb.append(childMapper.toXml(innerPojo, key, null));
				}
			}
		}
		closeTag(sb, name);
		depth=saveDepth;
		return sb.toString();
	}

	/**
	 * Append indentation and an open tag to StringBuffer.
	 * @param sb
	 * @param name
	 * @param attribs
	 */
	private void openTag(StringBuffer sb, String name, String attribs) {
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
	 * @param sb
	 * @param name
	 */
	private void closeTag(StringBuffer sb, String name) {
		sb.append("</");
		sb.append(name);
		sb.append(">\n");
	}

}
