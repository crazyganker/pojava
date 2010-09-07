package org.pojava.persistence.factories;

import java.util.Map;

/**
 * A SerialFactory defines the interface for serialization and
 * deserialization for a set of types.
 * 
 * @author John Pile
 *
 */
public interface SerialFactory {

	/**
	 * Construct an object from an array.
	 * @param type
	 * @param params
	 * @return
	 */
	Object construct(Class type, Object[] params);
	
	/**
	 * Construct an object from a property map.
	 * @param type
	 * @param params
	 * @return
	 */
	Object construct(Class type, Map params);
	
	/**
	 * Serialize an object to XML.
	 * @param obj
	 * @return
	 */
	String serialize(Object obj);
	
}
