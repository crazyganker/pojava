package org.pojava.persistence.serial;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.pojava.persistence.factories.SerialFactory;
import org.pojava.util.ReflectionTool;

public class XmlSerializer {

	private int depth = 0;
	private Class baseClass = null;
	private XmlDefs config;

	public XmlSerializer() {
		config = new XmlDefs();
	}

	public XmlSerializer(Class baseClass) {
		this.baseClass = baseClass;
		config = new XmlDefs();
	}

	public XmlSerializer(Class type, XmlDefs config) {
		this.baseClass = type;
		this.config = config;
	}

	public XmlSerializer newXmlSerializer(Class keyClass, int depth) {
		XmlSerializer xmlSerializer = new XmlSerializer(keyClass);
		xmlSerializer.depth = depth;
		xmlSerializer.config = config;
		return xmlSerializer;
	}

	/**
	 * Walk the tree of objects, gathering types and checking for circular
	 * references.
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

	public String toXml(Object obj) {
		walk(obj);
		config.resetRegistry();
		return toXml(obj, null, null);
	}

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
		SerialFactory override=config.override(type);
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

	public String toXml2(Object pojo, String name, String attribs) {
		StringBuffer sb = new StringBuffer();
		StringBuffer attribSb = new StringBuffer();
		if (attribs != null) {
			attribSb.append(attribs);
		}
		if (pojo == null) {
			sb.append(config.indent(depth));
			sb.append('<');
			sb.append(name);
			sb.append("><null/></");
			sb.append(name);
			sb.append(">\n");
			return sb.toString();
		}
		Class childClass = pojo.getClass();
		if (attribs == null) {
			attribs = "";
		}

		// An unnamed object must specify its type
		if (name == null || name.length() == 0) {
			name = "obj";
			attribs += " class=\"" + className(pojo) + "\"";
			if (!(this.baseClass == childClass) && !isBasic(childClass)) {
				XmlSerializer serializer = config.getXmlSerializer(childClass);
				return serializer.toXml(pojo, "obj", attribs);
			}
		}
		// Repeated objects are output as references
		Integer reference = config.getReferenceId(pojo);
		if (reference == null) {
			reference = config.register(pojo);
			attribSb.append(" type=\"mem\" id=\"");
			attribSb.append(reference);
			attribSb.append("\"");
		} else {
			sb.append(config.indent(depth));
			sb.append("<");
			sb.append(name);
			sb.append(" type=\"ref\" id=\"");
			sb.append(reference);
			sb.append("\"/>\n");
			return sb.toString();
		}
		String rename = config.renamedJava(pojo.getClass(), name);
		if (rename != null) {
			name = rename;
		}
		if (isBasic(childClass)) {
			sb.append(config.indent(depth));
			sb.append(simpleElement(pojo, name, attribs));
		} else if (childClass.equals(Object.class)) {
			sb.append(snippetFromUntyped(pojo, name, attribs));
		} else if (baseClass.equals(Object.class)
				&& java.util.Collection.class.isAssignableFrom(childClass)) {
			sb.append(snippetFromUntyped(pojo, name, attribs));
		} else if (java.util.Collection.class.isAssignableFrom(childClass)) {
			sb.append(snippetFromCollection(pojo, name, attribs));
		} else if (java.util.AbstractMap.class.isAssignableFrom(childClass)
				|| childClass == Map.class) {
			sb.append(snippetFromMap(pojo, name, attribs));
		} else if (childClass.isArray()) {
			sb.append(snippetFromArray(pojo, name, attribs));
		} else {
			sb.append(snippetFromPojo(pojo, name, attribs));
		}
		return sb.toString();
	}

	private String className(Object obj) {
		String name = obj.getClass().getName();
		return name.startsWith("java.lang.") ? name.substring(10) : name;
	}

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
					// if (java2xml.containsKey(key)) {
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

	private void openTag(StringBuffer sb, String name, String attribs) {
		sb.append(config.indent(depth));
		sb.append('<');
		sb.append(name);
		if (attribs != null) {
			sb.append(attribs);
		}
		sb.append(">");
	}

	private void closeTag(StringBuffer sb, String name) {
		sb.append("</");
		sb.append(name);
		sb.append(">\n");
	}

}
