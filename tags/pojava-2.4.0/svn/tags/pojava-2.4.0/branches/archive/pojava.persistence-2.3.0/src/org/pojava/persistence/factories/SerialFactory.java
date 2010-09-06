package org.pojava.persistence.factories;

import java.util.Map;

/**
 * A SerialFactory defines the interface for serialization and deserialization for a set of
 * types.
 * 
 * @author John Pile
 * 
 */
public interface SerialFactory<T> {

    /**
     * Construct an object from an array.
     * 
     * @param type
     * @param params
     * @return typed object constructed from parameters
     */
    T construct(Class<T> type, Object[] params);

    /**
     * Construct an object from a property map.
     * 
     * @param type
     * @param params
     * @return typed object constructed from mapped parameters
     */
    T construct(Class<T> type, Map<String, ?> params);

    /**
     * Serialize an object to XML.
     * 
     * @param obj
     * @return xml string from object
     */
    String serialize(T obj);

}
