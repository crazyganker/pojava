package org.pojava.lang;

/*
 Copyright 2008 John Pile

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.pojava.datetime.DateTime;
import org.pojava.datetime.DateTimeFormat;

/**
 * A BoundString represents a String with Bindings. Each binding is represented by a placeholder
 * mapped sequentially to a list of Binding objects.
 * 
 * @author John Pile
 * 
 */
public class BoundString {

    private final char placeholder = '?';
    private static final char quot = '\'';
    private final StringBuffer sb = new StringBuffer();
    private final List bindings = new ArrayList();
    private static final String date2ms = "MM/dd/yyyy HH:mm:ss.SSS";

    /**
     * Construct an empty BoundString.
     */
    public BoundString() {
    }

    /**
     * Construct a BoundString from a String.
     */
    public BoundString(String str) {
        this.sb.append(str);
    }

    /**
     * Append to the existing string.
     * 
     * @param str
     */
    public void append(String str) {
        this.sb.append(str);
    }

    /**
     * Insert in front of the existing string.
     * 
     * @param str
     */
    public void insert(String str) {
        this.sb.insert(0, str);
    }

    /**
     * Append another bound string including both string and bindings.
     * 
     * @param bstr
     */
    public void append(BoundString bstr) {
        this.sb.append(bstr.getString());
        this.bindings.addAll(bstr.getBindings());
    }

    /**
     * Insert another bound string including both string and bindings.
     * 
     * @param bstr
     */
    public void insert(BoundString bstr) {
        this.sb.insert(0, bstr.getString());
        this.bindings.addAll(0, bstr.getBindings());
    }

    /**
     * Return the bindings bound to the string.
     * 
     * @return List of Binding objects.
     */
    public List getBindings() {
        return this.bindings;
    }

    /**
     * Return the string being bound
     * 
     * @return String into which Binding objects are bound.
     */
    public String getString() {
        return this.sb.toString();
    }

    /**
     * Add a binding
     * 
     * @param type
     *            class of object to bind.
     * @param obj
     *            object to bind.
     */
    public void addBinding(Class type, Object obj) {
        this.bindings.add(new Binding(type, obj));
    }

    /**
     * Add a collection of bindings.
     * 
     * @param bindings
     */
    public void addBindings(Collection bindings) {
        this.bindings.addAll(bindings);
    }

    /**
     * Clear both string and bindings.
     */
    public void clear() {
        this.sb.setLength(0);
        this.bindings.clear();
    }

    /**
     * Verify placeholder count against bindings count.
     * 
     * @return True if placeholder count mismatches binding count.
     */
    public boolean isImbalanced() {
        int placeholders = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == placeholder) {
                placeholders++;
            }
        }
        return placeholders != this.bindings.size();
    }

    /**
     * Chop last ct characters off of string
     * 
     * @param ct
     */
    public void chop(int ct) {
        this.sb.setLength(Math.max(0, this.sb.length() - ct));
    }

    /**
     * Display an unbound equivalent of this BoundString with String versions of each bound
     * value represented in-line in place of the markers.
     * 
     * @return String with toString versions of bound values inserted back into the String.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        int marker = 0;
        char quot = '\'';
        char[] sql = this.getString().toCharArray();
        for (int i = 0; i < sql.length; i++) {
            char c = sql[i];
            if (c == '?') {
                Binding binding = (Binding) getBindings().get(marker++);
                if (binding.getObj() == null) {
                    sb.append("null");
                } else {
                    Object obj = binding.getObj();
                    Class type = binding.getType();
                    if (type == Integer.class || type == int.class || type == Long.class
                            || type == long.class) {
                        sb.append(obj.toString());
                    } else if (DateTime.class == obj.getClass()) {
                        sb.append(formatDate((DateTime) obj));
                    } else if (Date.class.isAssignableFrom(obj.getClass())) {
                        sb.append(formatDate(new DateTime(((Date) obj).getTime())));
                    } else {
                        sb.append(quot);
                        sb.append(binding.getObj().toString());
                        sb.append(quot);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Format a date, chopping off zeros.
     * 
     * @param date
     * @return a string version of a date with zeros truncated.
     */
    private String formatDate(DateTime dt) {
        StringBuffer sb = new StringBuffer();
        sb.append(quot);
        sb.append(DateTimeFormat.format(BoundString.date2ms, dt));
        if (sb.substring(11).equals(" 00:00:00.000")) {
            sb.setLength(11);
        } else if (sb.substring(20).equals(".000")) {
            sb.setLength(20);
        }
        sb.append(quot);
        return sb.toString();
    }

}
