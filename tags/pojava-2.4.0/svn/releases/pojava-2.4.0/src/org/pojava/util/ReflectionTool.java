package org.pojava.util;

/*
 Copyright 2008-09 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.pojava.exception.ReflectionException;
import org.pojava.lang.Accessors;

/**
 * The ReflectionTool class provides static methods for accessing an object's properties.
 * 
 * @author John Pile
 * 
 */
public class ReflectionTool {

    /**
     * Returns true if class derives from Collection
     * 
     * @param type
     *            Class to test.
     * @return True if class is a type of Collection, else false.
     */
    public static boolean isCollection(Class<?> type) {
        return java.util.Collection.class.isAssignableFrom(type);
    }

    /**
     * Returns true if class derives from Set
     * 
     * @param type
     *            Class to test.
     * @return True if object is Map, else false.
     */
    public static boolean isMap(Class<?> type) {
        return java.util.AbstractMap.class.isAssignableFrom(type) || type == Map.class;
    }

    /**
     * Return true if object is equivalent to a primitive type.
     * 
     * @param propClass
     * @return true if class is primitive or an object equivalent
     */
    public static boolean isBasic(Class<?> propClass) {
        return propClass.isPrimitive() || propClass == String.class
                || propClass == Integer.class || propClass == Double.class
                || propClass == Boolean.class || propClass == Long.class
                || propClass == Float.class || propClass == Short.class
                || propClass == Byte.class || propClass == Character.class;
    }

    /**
     * Make content safe for XML or URI by encoding illegal characters.
     * 
     * @param obj
     *            Object being encoded
     * @return A non-null string.
     */
    public static String clean(String obj) {
        if (obj == null) {
            return "";
        }
        return obj.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    /**
     * Extract a property Map from a class by its getters.
     * 
     * @param baseClass
     *            Class of properties to harvest.
     * @return Map of names to classes.
     */
    public static Map<String, Class<?>> propertyMap(Class<?> baseClass) {
        Map<String, Class<?>> map = new HashMap<String, Class<?>>();
        Method[] methods = baseClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if (name.startsWith("get")) {
                if (!name.equals("getClass")) {
                    String shortName = name.substring(3, 4).toLowerCase() + name.substring(4);
                    map.put(shortName, method.getReturnType());
                }
            } else if (name.startsWith("is")) {
                String shortName = name.substring(2, 3).toLowerCase() + name.substring(3);
                map.put(shortName, method.getReturnType());
            }
        }
        return map;
    }

    /**
     * Drill down to a nested property and set its value
     */
    public static void setNestedValue(String path, Object parent, Object child)
            throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Object innerObject = parent;
        Object tempObject = null;
        Class<? extends Object> propClass = null;
        Class<? extends Object> innerObjectClass = null;
        Method method = null;
        Object[] params = new Object[1];
        Class<?>[] parameterTypes = new Class<?>[1];
        if (path.startsWith("./")) {
            path = path.substring(2);
        }
        String[] pathPart = path.split("[./]");
        try {
            // Get or Create (where necessary) our way down to a method
            for (int p = 0; p < pathPart.length - 1; p++) {
                innerObjectClass = innerObject.getClass();
                Integer offset = null;
                try {
                    if (pathPart[p].indexOf('[') >= 0) {
                        offset = new Integer(pathPart[p].substring(
                                pathPart[p].indexOf('[') + 1, pathPart[p].indexOf(']')));
                    } else {
                        method = innerObjectClass.getMethod(getter(pathPart[p]),
                                (Class<?>[]) null);
                    }
                } catch (NoSuchMethodException ex) {
                    method = innerObject.getClass().getMethod(pathPart[p], (Class<?>[]) null);
                }
                if (offset == null) {
                    innerObject = method.invoke(innerObject, (Object[]) null);
                } else {
                    if (!(innerObjectClass.isArray() || isCollection(innerObjectClass) || isMap(innerObjectClass))) {
                        method = innerObjectClass.getMethod(getter(pathPart[p].substring(0,
                                pathPart[p].indexOf('['))), (Class<?>[]) null);
                        tempObject = method.invoke(innerObject, (Object[]) null);
                        // If a collection offset is requested for a null
                        // collection, construct one.
                        if (tempObject == null) {
                            tempObject = innerObjectClass.newInstance();
                            Class<?>[] classes = { innerObjectClass };
                            Object[] objects = { tempObject };
                            method = innerObjectClass.getMethod(setter(pathPart[p]), classes);
                            method.invoke(innerObject, objects);
                        }
                        innerObject = tempObject;
                        innerObjectClass = innerObject.getClass();
                    }
                    if (innerObjectClass.isArray()) {
                        innerObject = Array.get(innerObject, offset.intValue());
                    } else if (isCollection(innerObjectClass)) {
                        innerObject = ((Collection<?>) innerObject).toArray()[offset.intValue()];
                    } else if (isMap(innerObjectClass)) {
                        // Order is not guaranteed, but a map can be fully
                        // iterated.
                        innerObject = ((Map<?, ?>) innerObject).values().toArray()[offset
                                .intValue() - 1];
                    } else {
                        throw new IllegalStateException("Failed to extract '" + path
                                + "' from object of class " + child.getClass().getName());
                    }
                }
            }
            // If you haven't thrown an exception, you're at the final call.
            // You need to know the class of the parameter to pass to the
            // setter. If unknown, infer it to be the same class returned by
            // the getter.
            try {
                method = innerObject.getClass().getMethod(
                        getter(pathPart[pathPart.length - 1]), (Class<?>[]) null);
            } catch (NoSuchMethodException ex) {
                method = innerObject.getClass().getMethod(pathPart[pathPart.length - 1],
                        (Class<?>[]) null);
            }
            propClass = method.getReturnType();
            parameterTypes[0] = propClass;
            // Next, fetch the setter method matching the signature of that
            // class
            try {
                method = innerObject.getClass().getMethod(
                        setter(pathPart[pathPart.length - 1]), parameterTypes);
            } catch (NoSuchMethodException ex) {
                // Method may be named setBool if isBool is boolean
                // Method may be named setIsBool if isBool is Boolean
                if (pathPart[pathPart.length - 1].startsWith("is")) {
                    method = innerObject.getClass().getMethod(
                            setter(pathPart[pathPart.length - 1].substring(2)), parameterTypes);
                } else {
                    throw ex;
                }
            }
            params[0] = child;
            method.invoke(innerObject, params);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(ex.getMessage(), ex);
        }
    }

    /**
     * Drill down to a nested property and set its value
     */
    public static void setNestedValue(Method[] getters, Method[] setters, Object parent,
            Object value) throws NoSuchMethodException, IllegalAccessException,
            InstantiationException {
        Object innerObject = parent;
        Object tempObject = null;
        Object[] params = new Object[1];
        try {
            // Get or Create our way down to a method
            for (int p = 0; p < getters.length - 1; p++) {
                tempObject = getters[p].invoke(innerObject, (Object[]) null);
                if (tempObject == null) {
                    // The requested child object doesn't exist yet and we
                    // need to drill down to one of its properties...
                    // so we instantiate and attach a new child instance.
                    tempObject = getters[p].getReturnType().newInstance();
                    params[0] = tempObject;
                    // Pass in the new object into the setter method.
                    setters[p].invoke(innerObject, params);
                }
                innerObject = tempObject;
            }
            params[0] = value;
            setters[setters.length - 1].invoke(innerObject, params);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectionException(ex.getMessage(), ex);
        }
    }

    /**
     * Determine get accessor name from property name
     * 
     * @param name
     * @return name of get accessor derived from property name.
     */
    private static String getter(String name) {
        return "get" + StringTool.capitalize(name);
    }

    /**
     * Determine set accessor name from property name
     * 
     * @param name
     * @return name of set accessor derived from property name
     */
    private static String setter(String name) {
        return "set" + StringTool.capitalize(name);
    }

    /**
     * Determine the class of a property
     * 
     * @param baseClass
     *            of object serving as root of property reference
     * @param property
     *            reference to an object's property
     * @return Class of the specified property.
     */
    public static Class<?> propertyType(Class<?> baseClass, String property)
            throws NoSuchMethodException {
        Class<?> innerClass = baseClass;
        Method method = null;
        if (property.startsWith("./")) {
            property = property.substring(2);
        }
        String[] path = property.split("[./]");
        for (int p = 0; p < path.length; p++) {
            try {
                method = innerClass.getMethod(getter(path[p]), (Class<?>[]) null);
            } catch (NoSuchMethodException ex) {
                method = innerClass.getMethod(path[p], (Class<?>[]) null);
            }
            innerClass = method.getReturnType();
        }
        return innerClass;
    }

    /**
     * Array of setter methods that drill down to a property.
     * 
     * @param getterMethods
     * @return array of setter methods that drill down to a property.
     * @throws NoSuchMethodException
     */
    public static Method[] setterMethodDrilldown(Method[] getterMethods)
            throws NoSuchMethodException {
        Method[] setters = new Method[getterMethods.length];
        for (int i = 0; i < setters.length; i++) {
            Method getter = getterMethods[i];
            Class<?> parent = getter.getDeclaringClass();
            String getterName = getter.getName();
            String setterName = "set"
                    + getterName.substring(getterName.charAt(0) == 'i' ? 2 : 3);
            Class<?>[] params = { getter.getReturnType() };
            setters[i] = parent.getMethod(setterName, params);
        }
        return setters;
    }

    /**
     * Extract the getters and setters for a class.
     * 
     * @param type
     *            hold class of object containing the get accessors
     * @return array of getter methods drilling down to a property
     */
    public static Accessors accessors(Class<?> type) {
        Method[] allMethods = type.getMethods();
        Accessors accessors = new Accessors();
        accessors.setType(type);
        Map<String, Method> getters = accessors.getGetters();
        Map<String, Method> setters = accessors.getSetters();
        for (int i = 0; i < allMethods.length; i++) {
            Method meth = allMethods[i];
            String methName = meth.getName();
            char c = methName.charAt(0);
            if (c == 's') {
                if (meth.getParameterTypes().length == 1) {
                    setters.put(propertyFor(methName), meth);
                }
            } else if (c == 'g' || c == 'i' || c == 'h') {
                if (meth.getParameterTypes().length == 0) {
                    if (!"getClass".equals(methName) && !"hashCode".equals(methName)) {
                        getters.put(propertyFor(methName), meth);
                    }
                }
            }
        }
        return accessors;
    }

    /**
     * Specify the getters and setters for a class.
     * 
     * @param type
     *            class of object containing the accessors
     * @return referenced getters and setters for a class
     */
    public static Accessors accessors(Class<?> type, Map<String, Method> getterNames,
            Map<String, Method> setterNames) throws NoSuchMethodException {
        Method[] allMethods = type.getMethods();
        Accessors accessors = new Accessors();
        for (int i = 0; i < allMethods.length; i++) {
            Method meth = allMethods[i];
            String name = meth.getName();
            if (getterNames.containsKey(name) && meth.getParameterTypes().length == 0) {
                getterNames.put(name, meth);
            } else if (setterNames.containsKey(name) && meth.getParameterTypes().length == 1) {
                setterNames.put(name, meth);
            }
        }
        return accessors;
    }

    /**
     * Array of getter methods that drill down to a nested bean property
     * 
     * @param type
     *            hold class of object containing the get accessors
     * @param property
     *            hold a reference to a bean property
     * @return array of getter methods drilling down to a property
     */
    public static Method[] getterMethodDrilldown(Class<?> type, String property)
            throws NoSuchMethodException {
        Class<?> innerClass = type;
        Method method = null;
        if (property.startsWith("./")) {
            property = property.substring(2);
        }
        String[] path = property.split("[./]");
        Method[] methods = new Method[path.length];
        for (int p = 0; p < path.length; p++) {
            if (path[p].indexOf('[') >= 0) {
                method = innerClass.getMethod(
                        getter(path[p].substring(0, path[p].indexOf('['))), (Class<?>[]) null);
            } else {
                try {
                    method = innerClass.getMethod(getter(path[p]), (Class<?>[]) null);
                } catch (NoSuchMethodException ex) {
                    method = innerClass.getMethod("is" + StringTool.capitalize(path[p]),
                            (Class<?>[]) null);
                }
            }
            methods[p] = method;
            innerClass = method.getReturnType();
        }
        return methods;
    }

    /**
     * Drill down to a nested bean property from predetermined methods.
     * 
     * @param getters
     *            A trail of nested methods leading to the property.
     * 
     * @param bean
     *            Root bean
     * @return Result from getter method represented by the inner-most nested property.
     */
    public static Object getNestedValue(Method[] getters, Object bean) {
        Object innerObject = bean;
        int i = 0;
        try {
            for (i = 0; i < getters.length; i++) {
                innerObject = getters[i].invoke(innerObject, (Object[]) null);
            }
        } catch (IllegalAccessException ex) {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to invoke getter ");
            sb.append(getters[i].getName());
            sb.append(".");
            throw new ReflectionException(sb.toString(), ex);
        } catch (InvocationTargetException ex) {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to invoke getter ");
            sb.append(getters[i].getName());
            sb.append(".");
            throw new ReflectionException(sb.toString(), ex);
        }
        return innerObject;
    }

    /**
     * Drill down to a nested bean property
     * 
     * @param relativePath
     *            Property specifier for relative path starting at bean as the root node. Both
     *            property.property and ./xpath/to/property are interpreted.
     * @param bean
     *            Root bean
     * @return Result from getter method represented by dottedShortName
     */
    public static Object getNestedValue(String relativePath, Object bean) {
        Object innerObject = bean;
        Class<?> innerObjectClass = null;
        Method method = null;
        if (relativePath.startsWith("./")) {
            relativePath = relativePath.substring(2);
        }
        String[] path = relativePath.split("[./]");
        try {
            for (int p = 0; p < path.length; p++) {
                innerObjectClass = innerObject.getClass();
                Integer offset = null;
                try {
                    if (path[p].indexOf('[') >= 0) {
                        offset = new Integer(path[p].substring(path[p].indexOf('[') + 1,
                                path[p].indexOf(']')));
                    } else {
                        method = innerObjectClass.getMethod(getter(path[p]), (Class<?>[]) null);
                    }
                } catch (NoSuchMethodException ex) {
                    method = innerObjectClass.getMethod(path[p], (Class<?>[]) null);
                }
                if (offset == null) {
                    innerObject = method.invoke(innerObject, (Object[]) null);
                } else {
                    if (!(innerObjectClass.isArray() || isCollection(innerObjectClass) || isMap(innerObjectClass))) {
                        method = innerObjectClass.getMethod(getter(path[p].substring(0, path[p]
                                .indexOf('['))), (Class<?>[]) null);
                        innerObject = method.invoke(innerObject, (Object[]) null);
                        innerObjectClass = innerObject.getClass();
                    }
                    if (innerObjectClass.isArray()) {
                        innerObject = Array.get(innerObject, offset.intValue());
                    } else if (isCollection(innerObjectClass)) {
                        innerObject = ((Collection<?>) innerObject).toArray()[offset.intValue() - 1];
                    } else if (isMap(innerObjectClass)) {
                        // Order is not guaranteed, but a map can be fully
                        // iterated.
                        innerObject = ((Map<?, ?>) innerObject).values().toArray()[offset
                                .intValue() - 1];
                    } else {
                        throw new IllegalStateException("Failed to extract '" + relativePath
                                + "' from object of class " + bean.getClass().getName());
                    }
                }
            }
        } catch (NoSuchMethodException ex) {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to invoke getter ");
            sb.append(relativePath);
            sb.append(".");
            throw new ReflectionException(sb.toString(), ex);
        } catch (InvocationTargetException ex) {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to invoke getter ");
            sb.append(relativePath);
            sb.append(".");
            throw new ReflectionException(sb.toString(), ex);
        } catch (IllegalAccessException ex) {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to invoke getter ");
            sb.append(relativePath);
            sb.append(".");
            throw new ReflectionException(sb.toString(), ex);
        }
        return innerObject;
    }

    public static String propertyFor(Method method) {
        if (method == null) {
            return null;
        }
        char[] chars = method.getName().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c < 'a' || c > 'z') {
                StringBuffer sb = new StringBuffer();
                sb.append(Character.toLowerCase(c));
                sb.append(method.getName().substring(1 + i));
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * Return the property name for a getter or setter name.
     * 
     * @param accessorName
     * @return Property name derived from getter or setter name.
     */
    public static String propertyFor(String accessorName) {
        char[] chars = accessorName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c < 'a' || c > 'z') {
                StringBuffer sb = new StringBuffer();
                sb.append(Character.toLowerCase(c));
                sb.append(accessorName.substring(1 + i));
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * Invoke any setters of an object whose properties are reference in a map, passing the
     * values of that map into their respective setters.
     * 
     * @param obj
     * @param properties
     * @param setters
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void populateFromMap(Object obj, Map<String, Object[]> properties,
            Map<String, Method> setters) throws IllegalAccessException,
            InvocationTargetException {
        if (obj == null || setters == null || setters.isEmpty()) {
            return;
        }
        for (Iterator<Map.Entry<String, Method>> it = setters.entrySet().iterator(); it
                .hasNext();) {
            Method setter = it.next().getValue();
            String prop = propertyFor(setter);
            if (properties.containsKey(prop)) {
                Object[] args = { properties.get(prop) };
                setter.invoke(obj, args);
            }
        }
    }

    /**
     * Return a map of all get accessors for a class
     * 
     * @param pojoClass
     * @return Map of all getters for a class
     */
    public static Map<String, Method[]> getterChains(Class<?> pojoClass) {
        Map<String, Method[]> getterChains = new HashMap<String, Method[]>();
        Method[] methods = pojoClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if (name.startsWith("get") && methods[i].getParameterTypes().length == 0) {
                Method[] getterChain = { methods[i] };
                getterChains.put(name, getterChain);
            }
        }
        return getterChains;
    }

    /**
     * Return a map of all set accessors for a class determined from the name and type of a list
     * of get accessors.
     * 
     * @param getterChains
     * @return Map of all setters for a class matching the getters
     */
    public static Map<String, Method[]> setterChains(Map<String, Method[]> getterChains) {
        Map<String, Method[]> setterChains = new HashMap<String, Method[]>();
        for (Iterator<Map.Entry<String, Method[]>> it = getterChains.entrySet().iterator(); it
                .hasNext();) {
            Map.Entry<String, Method[]> entry = it.next();
            Method[] getters = entry.getValue();
            Method[] setters = new Method[getters.length];
            try {
                for (int i = 0; i < getters.length; i++) {
                    String name = "set" + getters[i].getName().substring(3);
                    setters[i] = getters[i].getReturnType().getMethod(name, (Class<?>[]) null);
                }
            } catch (NoSuchMethodException ex) {
                // ignore
            }
        }
        return setterChains;
    }
}
