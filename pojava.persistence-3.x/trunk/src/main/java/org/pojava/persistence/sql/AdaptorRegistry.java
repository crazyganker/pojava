package org.pojava.persistence.sql;

import org.pojava.transformation.BindingAdaptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * BindingAdaptors are registered by Method
 *
 * @author John Pile
 */
public class AdaptorRegistry {

    private static Map<Method, BindingAdaptor<?, ?>> adaptorMap = new HashMap<Method, BindingAdaptor<?, ?>>();

    public static void put(Method method, BindingAdaptor<?, ?> adaptor) {
        adaptorMap.put(method, adaptor);
    }

    public static BindingAdaptor<?, ?> get(Method method) {
        return adaptorMap.get(method);
    }

    public static boolean containsKey(Method method) {
        return adaptorMap.containsKey(method);
    }
}
