package org.pojava.persistence.serial;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple bean for storing two-way renames.
 * 
 * @author John Pile
 * 
 */
public class RenamedFields {

    private final Map<String, String> fromJavaName = new HashMap<String, String>();
    private final Map<String, String> fromXmlName = new HashMap<String, String>();

    /**
     * Double-map the java-to-xml and xml-to-java entries.
     * 
     * @param javaName
     * @param xmlName
     */
    public void rename(String javaName, String xmlName) {
        fromJavaName.put(javaName, xmlName);
        fromXmlName.put(xmlName, javaName);
    }

    /**
     * Return true if a map entry exists for the given java name.
     * 
     * @param javaName reference
     * @return true if a map entry exists for the given java name.
     */
    public boolean hasJavaName(String javaName) {
        return fromJavaName.containsKey(javaName);
    }

    /**
     * Return true if a map entry exists for the given xml name.
     * 
     * @param xmlName 
     * @return true if a map entry exists for the given xml name.
     */
    public boolean hasXmlName(String xmlName) {
        return fromXmlName.containsKey(xmlName);
    }

    /**
     * Return the xml name mapped to the given java name
     * 
     * @param javaName
     * @return the tag used to represent the mapped property
     */
    public String xmlNameFor(String javaName) {
        return (String) fromJavaName.get(javaName);
    }

    /**
     * Return the java name mapped to the given xml name
     * 
     * @param xmlName
     * @return the property name mapped from the given tag
     */
    public String javaNameFor(String xmlName) {
        return (String) fromXmlName.get(xmlName);
    }
}
