package org.pojava.persistence.serial;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pojava.datetime.DateTime;
import org.pojava.exception.PersistenceException;
import org.pojava.util.ReflectionTool;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parse an XML document into an Object.
 * 
 * This class is reusable, but was not designed for concurrency. You should construct a new
 * XmlParser instance for each thread.
 * 
 * @author John Pile
 * 
 */
@SuppressWarnings("unchecked")
public class XmlParser<T> implements ContentHandler {
    
    private static Logger logger=Logger.getLogger("org.pojava.persistence.serial.XmlParser");

    private static final String SAX_DRIVER = "org.xml.sax.driver";
    private static final String XERCES_PARSER = "org.apache.xerces.parsers.SAXParser";
    private static final String CRIMSON_PARSER = "org.apache.crimson.parser.XMLReaderImpl";
    private static final String AELFRED_PARSER = "gnu.xml.aelfred2.XmlReader";
    private static final String PICCOLO_PARSER = "com.bluecast.xml.Piccolo";
    private static final Integer ZERO = Integer.valueOf(0);
    private static final Integer ONE = Integer.valueOf(1);

    private int depth = 0;
    private int size = 8;
    Map<Object, Object>[] objs = new Map[size];
    StringBuffer[] buffers = new StringBuffer[size];
    Class<?>[] types = new Class[size];
    XmlDefs defs;
    /**
     * Map ID's to Fully qualified paths down to a property
     */
    Map<String, String> refNames = new HashMap<String, String>();
    /**
     * Map ID's to a registered value
     */
    Map<String, String> refValues = new HashMap<String, String>();
    /**
     * Keep track of which property we're working on
     */
    String[] fqprops = new String[size];

    public XmlParser() {
        this.defs = new XmlDefs();
    }

    public XmlParser(XmlDefs defs) {
        this.defs = defs;
    }

    public T parse(String xml) {
        XMLReader parser;
        InputSource inp = new InputSource(new StringReader(xml));
        try {
            // Prefer the XML SAX Parser specified as a parameter to the
            // java binary
            parser = XMLReaderFactory.createXMLReader();
        } catch (SAXException e1) {
            // If unspecified, try a list of popular parsers.
            try {
                parser = XMLReaderFactory.createXMLReader(XERCES_PARSER);
                System.setProperty(SAX_DRIVER, XERCES_PARSER);
            } catch (SAXException e2) {
                try { // Crimson
                    parser = XMLReaderFactory.createXMLReader(CRIMSON_PARSER);
                    System.setProperty(SAX_DRIVER, CRIMSON_PARSER);
                } catch (SAXException e3) {
                    try { // Aelfred
                        parser = XMLReaderFactory.createXMLReader(AELFRED_PARSER);
                        System.setProperty(SAX_DRIVER, AELFRED_PARSER);
                    } catch (SAXException e4) {
                        try { // Piccolo
                            parser = XMLReaderFactory.createXMLReader(PICCOLO_PARSER);
                            System.setProperty(SAX_DRIVER, PICCOLO_PARSER);
                        } catch (SAXException e5) {
                            throw new PersistenceException("No SAX parser is available", e5);
                        }
                    }
                }
            }
        }
        parser.setContentHandler(this);
        try {
            parser.parse(inp);
        } catch (SAXException ex1) {
            throw new PersistenceException("Parse failure: " + ex1.getMessage(), ex1);
        } catch (IOException ex2) {
            throw new PersistenceException("Parse failure: " + ex2.getMessage(), ex2);
        }
        return (T) objs[0].values().toArray()[0];
    }

    /**
     * Content Handler Methods
     */

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        fqprops[0] = "";
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }
    
    public void startElement(String uri, String localName, String name, Attributes atts)
            throws SAXException {
        try {
            depth++;
            // Ensure array is large enough for this additional node
            if (depth >= size) {
                Map<Object, Object>[] moreObjs = new Map[size * 2];
                Class<?>[] moreTypes = new Class[size * 2];
                StringBuffer[] moreBuffers = new StringBuffer[size * 2];
                String[] moreFqprops = new String[size * 2];
                System.arraycopy(objs, 0, moreObjs, 0, size);
                System.arraycopy(buffers, 0, moreBuffers, 0, size);
                System.arraycopy(types, 0, moreTypes, 0, size);
                System.arraycopy(fqprops, 0, moreFqprops, 0, size);
                objs = moreObjs;
                types = moreTypes;
                buffers = moreBuffers;
                fqprops = moreFqprops;
                size *= 2;
            }
            if (buffers[depth] == null) {
                buffers[depth] = new StringBuffer();
            } else {
                buffers[depth].setLength(0);
            }
            if (fqprops[depth] != null) {
                fqprops[depth] = null;
            }
            if ("null".equals(localName)) {
                objs[depth - 1] = new HashMap<Object, Object>();
                return;
            }
            StringBuffer sb = buffers[depth];
            if (depth > 1) {
                sb.append(fqprops[depth - 1]);
                sb.append('.');
            }
            sb.append(localName);
            fqprops[depth] = sb.toString();
            sb.setLength(0);
            if (!("obj".equals(localName))) {
                if (Map.class.isAssignableFrom(types[depth - 1])) {
                    types[depth] = Object[].class;
                } else if (types[depth - 1].isArray()) {
                    types[depth] = types[depth - 1].getComponentType();
                } else {
                    types[depth] = ReflectionTool.propertyType(types[depth - 1], localName);
                }
            }
            boolean isMem = false;
            boolean isRef = false;
            String id = null;
            // Currently, we care about "class", "mem" and "ref".
            for (int i = 0; i < atts.getLength(); i++) {
                String attribName = atts.getLocalName(i);
                if ("class".equals(attribName) || "type".equals(attribName)) {
                    if (!(atts.getValue(i).indexOf('.') > 0)) {
                        if (atts.getValue(i).equals("DateTime")) {
                            types[depth] = DateTime.class;
                        } else {
                            char c = atts.getValue(i).charAt(0);
                            if (c >= 'A' && c <= 'Z')
                                sb.append("java.lang.");
                            sb.append(atts.getValue(i));
                            types[depth] = Class.forName(sb.toString());
                        }
                        sb.setLength(0);
                    } else {
                        types[depth] = Class.forName(atts.getValue(i));
                    }
                } else if ("mem".equals(attribName)) {
                    isMem = true;
                    id = atts.getValue(i);
                } else if ("ref".equals(attribName)) {
                    isRef = true;
                    id = atts.getValue(i);
                }
            }
            // This might not be needed, derivable from objs
            if (isMem) {
                refNames.put(id, fqprops[depth]);
            } else if (isRef) {
                refValues.put(fqprops[depth], refNames.get(id));
            }
            if (objs[depth - 1] == null) {
                objs[depth - 1] = new HashMap<Object, Object>();
            }
        } catch (ClassNotFoundException ex) {
            throw new PersistenceException("ClassNotFound: " + ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw new PersistenceException("NoSuchMethod: " + ex.getMessage(), ex);
        }
    }

    private void giveParent(Object key, Object value) {
        if ("obj".equals(key)) {
            objs[depth - 1].put(Integer.valueOf(objs[depth - 1].size()), value);
        } else {
            objs[depth - 1].put(key, value);
        }
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
        Object key;
        if (types[depth - 1] != null && types[depth - 1].isArray() && !"null".equals(localName)) {
            key = Integer.valueOf(objs[depth - 1].size());
        } else {
            key = localName;
        }
        if ("null".equals(key)) {
            objs[depth - 1].put(key, null);
        } else if (defs.isValue(types[depth])) {
            // A value bean like Integer, Long, etc., is immediately constructed.
            if (objs[depth] != null && objs[depth].containsKey(Integer.valueOf(0))
                    && buffers[depth].length() == 0) {
                objs[depth - 1].put(key, objs[depth].get(Integer.valueOf(0)));
            } else {
                if (objs[depth]!=null && objs[depth].containsKey("null")) {
                    giveParent(key, null);
                } else {
                    Object[] params = new Object[1];
                    params[0] = buffers[depth].toString();
                    giveParent(key, defs.construct(types[depth], params));
                }
            }
        } else if (types[depth] == null) {
            // If type is null, then append null to parent list.
            objs[depth - 1].put(key, null);
        } else if (types[depth].isArray()) {
            Object array;
            if (objs[depth] == null) {
                // Construct and populate an empty array.
                array = Array.newInstance(types[depth].getComponentType(), 0);
                if (types[depth - 1] == Object.class) {
                    objs[depth - 1].put(ZERO, array);
                } else {
                    objs[depth - 1].put(key, array);
                }
            } else if (types[depth-1]!=null && Map.class.isAssignableFrom(types[depth-1])) {
                // Assign an object pair to a Map entry
                Map<Object, Object> map;
                if (objs[depth-1].containsKey(ZERO)) {
                    map=(Map<Object, Object>)objs[depth-1].get(ZERO);
                } else {
                    map=new HashMap<Object, Object>();
                    objs[depth-1].put(ZERO, map);
                }
                if (objs[depth]!=null && objs[depth].containsKey(ZERO)) {
                    map.put(objs[depth].get(ZERO), objs[depth].get(ONE));
                }
            } else {
                // Construct and populate an array.
                if (!objs[depth].containsKey("null")) {
                    array = Array.newInstance(types[depth].getComponentType(), objs[depth]
                            .size());
                    for (int i = 0; i < objs[depth].size(); i++) {
                        Array.set(array, i, objs[depth].get(Integer.valueOf(/* 1 + */i)));
                    }
                    if (types[depth - 1] == Object.class) {
                        objs[depth - 1].put(ZERO, array);
                    } else {
                        objs[depth - 1].put(key, array);
                    }
                }
            }
        } else if (types[depth].isEnum()) {
            try {
            	@SuppressWarnings("rawtypes")Class pc=types[depth];
                if (Object.class.equals(types[depth-1])) {
                    objs[depth-1].put(ZERO, Enum.valueOf(pc, buffers[depth].toString()));
                } else {
                    objs[depth-1].put(key, Enum.valueOf(pc, buffers[depth].toString()));
                }
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        } else if (!refValues.containsKey(fqprops[depth])) {
            if (types[depth] == Object.class) {
                if (objs[depth] == null || objs[depth].size() == 0) {
                    giveParent(key, new Object());
                } else {
                    giveParent(key, objs[depth].get(ZERO));
                    objs[depth].clear();
                }
            } else if (Map.class.isAssignableFrom(types[depth])) {
                objs[depth - 1].put(key,objs[depth].get(ZERO));
            } else if (objs[depth]==null || objs[depth].containsKey("null")) {
                if (Object.class.isAssignableFrom(types[depth])) {
                    objs[depth -1].put(key, null);
                }
            } else {
                if (types[depth-1]!=null && Collection.class.isAssignableFrom(types[depth-1])){
                    key=Integer.valueOf(objs[depth-1].size());
                }
                objs[depth - 1].put(key, defs.construct(types[depth], objs[depth]));
            }
        }
        if (objs[depth] != null) {
            objs[depth].clear();
            types[depth] = null;
        }
        depth--;
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void endDocument() throws SAXException {
        if (refValues.size() > 0) {
            Resolver resolver = new Resolver(objs[0].values().toArray()[0]);
            for (Iterator<String> it = refValues.keySet().iterator(); it.hasNext();) {
                String ref = it.next();
                String mem = refValues.get(ref);
                Object memObj = ReflectionTool.getNestedValue(mem, resolver);
                try {
                    ReflectionTool.setNestedValue(ref, resolver, memObj);
                } catch (Exception ex) {
                    throw new PersistenceException("Could not resolve " + ref + " into " + mem,
                            ex);
                }
            }
        }
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        char[] buf = new char[length];
        System.arraycopy(ch, start, buf, 0, length);
        buffers[depth].append(buf);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    /**
     * Resolver is an envelope used for resolving referenced objects.
     * 
     * @author John Pile
     * 
     */
    protected static class Resolver {
        Object obj;

        public Resolver(Object obj) {
            this.obj = obj;
        }

        public Object getObj() {
            return obj;
        }

    }

}
