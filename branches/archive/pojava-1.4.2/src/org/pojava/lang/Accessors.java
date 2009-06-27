package org.pojava.lang;

import java.util.HashMap;
import java.util.Map;

public class Accessors {

    Class type;
    Map getters = new HashMap();
    Map setters = new HashMap();

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Map getGetters() {
        return getters;
    }

    public Map getSetters() {
        return setters;
    }

}
