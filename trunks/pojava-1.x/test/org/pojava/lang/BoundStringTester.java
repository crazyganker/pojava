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
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.pojava.datetime.DateTime;

public class BoundStringTester extends TestCase {

    public void testEmpty() {
        BoundString bs = new BoundString();
        assertEquals(new ArrayList(), bs.getBindings());
        assertEquals("", bs.getString());
    }

    public void testAppendString() {
        BoundString bs = new BoundString();
        bs.append("one");
        bs.append(",2");
        bs.append(",III");
        assertEquals("one,2,III", bs.getString());
    }

    public void testInsertString() {
        BoundString bs = new BoundString();
        bs.append("one");
        bs.insert("two, ");
        bs.insert("three, ");
        assertEquals("three, two, one", bs.getString());
    }

    public void testAppendBoundString() {
        BoundString bs1 = new BoundString();
        BoundString bs2 = new BoundString();
        bs1.append("easy as ?");
        bs1.addBinding(String.class, "ABC");
        // Append to empty BoundString
        bs2.append(bs1);
        assertEquals(bs1.getString(), bs2.getString());
        assertEquals(bs1.getBindings(), bs2.getBindings());
        // Append to populated BoundString
        bs2.append(bs1);
        assertEquals("easy as ?easy as ?", bs2.getString());
        assertEquals(2, bs2.getBindings().size());
    }

    public void testdInsertBoundString() {
        BoundString bs1 = new BoundString();
        BoundString bs2 = new BoundString();
        BoundString bs3 = new BoundString();
        bs1.append("easy as ?");
        bs1.addBinding(String.class, "ABC");
        // Insert into empty BoundString
        bs2.insert(bs1);
        assertEquals(bs1.getString(), bs2.getString());
        assertEquals(bs1.getBindings(), bs2.getBindings());
        bs3.insert("It's as ");
        // Insert into populated BoundString
        bs2.insert(bs3);
        assertEquals("It's as easy as ?", bs2.getString());
        assertEquals(1, bs2.getBindings().size());
    }

    public void testClear() {
        BoundString bs = new BoundString();
        bs.addBinding(DateTime.class, new DateTime("26-Jan-1969"));
        bs.append("dob");
        assertEquals("dob", bs.getString());
        assertEquals(1, bs.getBindings().size());
        bs.clear();
        assertEquals("", bs.getString());
        assertEquals(0, bs.getBindings().size());
        bs.clear(); // idempotent
        assertEquals("", bs.getString());
        assertEquals(0, bs.getBindings().size());
    }

    public void testChop() {
        BoundString bs = new BoundString();
        String[] words = { "one", "two", "three", "four" };
        for (int i = 0; i < words.length; i++) {
            bs.append(words[i]);
            bs.append(", ");
        }
        bs.chop(2);
        assertEquals("one, two, three, four", bs.getString());
        bs.clear();
        assertEquals("", bs.getString());
        // Forgive chop if nothing to chop
        bs.chop(2);
        assertEquals("", bs.getString());
    }

    public void testImbalance() {
        BoundString bs = new BoundString();
        assertFalse(bs.isImbalanced());
        bs.append("one=?");
        assertTrue(bs.isImbalanced());
        bs.addBinding(Integer.class, new Integer(1));
        assertFalse(bs.isImbalanced());
        bs.addBinding(Integer.class, new Integer(2));
        assertTrue(bs.isImbalanced());
        bs.append(", two=?");
        assertFalse(bs.isImbalanced());
    }

    public void testAddBindings() {
        List bindings = new ArrayList();
        bindings.add(new Binding(String.class, "two"));
        bindings.add(new Binding(Integer.class, new Integer(3)));
        BoundString bs = new BoundString();
        bs.append("(?, ?, ?)");
        bs.addBinding(DateTime.class, new DateTime("1/1/1"));
        bs.addBindings(bindings);
        assertEquals(DateTime.class, ((Binding) bs.getBindings().get(0)).getType());
        assertEquals("two", ((Binding) bs.getBindings().get(1)).getObj().toString());
        assertEquals(3, bs.getBindings().size());
    }

    public void testUnbind() {
        List bindings = new ArrayList();
        bindings.add(new Binding(String.class, "two"));
        bindings.add(new Binding(Integer.class, new Integer(3)));
        BoundString bs = new BoundString("(?, ?, ?, ?)");
        bs.addBinding(DateTime.class, new DateTime("1/1/1"));
        bs.addBindings(bindings);
        bs.addBinding(Date.class, new DateTime("2003/02/01"));
        assertEquals(4, bs.getBindings().size());
        assertEquals("('01/01/2001', 'two', 3, '02/01/2003')", bs.toString());
    }
}
