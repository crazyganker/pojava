package org.pojava.util;

import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

import org.pojava.datetime.DateTime;
import org.pojava.examples.People;
import org.pojava.examples.Person;

public class ReflectionToolTester extends TestCase {

    public Person newPerson(int i) {
        Person person = new Person();
        person.setId(i);
        person.setName("Person " + new Integer(i));
        person.setBirth(new DateTime(i));
        return person;
    }

    public void testClean() {
        assertEquals("", ReflectionTool.clean(null));
        assertEquals(" 123 ", ReflectionTool.clean(" 123 "));
        assertEquals("&lt;div class=&quot;custom&quot; /&gt;", ReflectionTool
                .clean("<div class=\"custom\" />"));
    }

    public void testGetNestedValueSimple() {
        Person person = new Person();
        person.setId(123);
        person.setName("Bob");
        person.setBirth(new DateTime("2008-08-08 08:08:08"));
        assertEquals(new Integer(123), ReflectionTool.getNestedValue("id", person));
        assertEquals("Bob", ReflectionTool.getNestedValue("name", person));
        assertEquals(new DateTime("2008-08-08 08:08:08"), ReflectionTool.getNestedValue(
                "birth", person));
    }

    public void testGetNestedValueInArray() {
        Person[] people = new Person[3];
        people[0] = new Person(1, "one", new DateTime(1));
        people[1] = new Person(2, "two", new DateTime(2));
        people[2] = new Person(3, "three", new DateTime(3));
        assertEquals(new Integer(1), ReflectionTool.getNestedValue("[0].id", people));
        assertEquals("two", ReflectionTool.getNestedValue("[1].name", people));
        assertEquals(new DateTime(3), ReflectionTool.getNestedValue("[2].birth", people));
    }

    public void testGetNestedValue() {
        People people = new People();
        people.addPerson(new Person(1, "one", new DateTime(1)));
        people.addPerson(new Person(2, "two", new DateTime(2)));
        assertEquals("two", ReflectionTool.getNestedValue("people[2].name", people));
    }

    public void testPropertyMap() {
        Map<String, Class<?>> propMap = ReflectionTool.propertyMap(Person.class);
        assertTrue(propMap.get("birth") == DateTime.class);
        assertTrue(propMap.get("id") == int.class);
        assertTrue(propMap.get("name") == String.class);
        assertTrue(propMap.get("red.herring") == null);
    }

    public void testSetNestedValue() throws Exception {
        DateTime testDate = new DateTime(123);
        Person person = newPerson(1);
        ReflectionTool.setNestedValue("birth", person, testDate);
        assertEquals(person.getBirth(), testDate);
    }

    public void testSetNestedValueLayered() throws Exception {
        Person person1 = newPerson(1);
        Person person2 = newPerson(2);
        People people = new People();
        people.addPerson(person2);
        people.setLeader(person1);
        ReflectionTool.setNestedValue("leader.id", people, new Integer(111));
        ReflectionTool.setNestedValue("people[0].name", people, "test");
        assertEquals(111, people.getLeader().getId());
        assertEquals("test", ((Person) people.getPeople().get(0)).getName());
    }

    public void testGetterMethods() throws Exception {
        Method[] methods = ReflectionTool.getterMethodDrilldown(People.class, "leader.id");
        assertEquals(methods[0], People.class.getMethod("getLeader", (Class<?>[]) null));
        assertEquals(methods[1], Person.class.getMethod("getId", (Class<?>[]) null));
    }

    public void testNestedValue() throws Exception {
        Person person1 = newPerson(1);
        Person person2 = newPerson(2);
        People people = new People();
        people.addPerson(person2);
        people.setLeader(person1);
        Method[] getters = ReflectionTool.getterMethodDrilldown(People.class, "leader.id");
        Object obj = ReflectionTool.getNestedValue(getters, people);
        assertEquals(new Integer(1), obj);
    }

    public void testNestedValueNotExisting() throws Exception {
        try {
            ReflectionTool.getterMethodDrilldown(People.class, "leader.none");
            fail("Expecting NoSuchMethodException.");
        } catch (NoSuchMethodException ex) {
            // expected
        }
    }

    public void testPropertyType() {
        try {
            assertEquals(int.class, ReflectionTool.propertyType(Person.class, "id"));
            assertEquals(DateTime.class, ReflectionTool.propertyType(Person.class, "birth"));
            assertEquals(DateTime.class, ReflectionTool.propertyType(People.class,
                    "leader.birth"));

            assertEquals(null, ReflectionTool.propertyType(Person.class, "does.not.exist"));
            fail("Expecting NoSuchMethodException.");
        } catch (NoSuchMethodException ex) {
            // expected
        }
    }

    public void testAccessors() throws Exception {
        Method[] getters;
        Method[] setters;
        getters = ReflectionTool.getterMethodDrilldown(People.class, "leader.name");
        setters = ReflectionTool.setterMethodDrilldown(getters);
        assertEquals(2, getters.length);
        assertEquals(2, setters.length);
        assertEquals("getLeader", getters[0].getName());
        assertEquals("getName", getters[1].getName());
        assertEquals("setLeader", setters[0].getName());
        assertEquals("setName", setters[1].getName());
        assertEquals(Person.class, getters[0].getReturnType());
        assertEquals(String.class, getters[1].getReturnType());
    }

    public void testSetNestedValueFromMethod() throws Exception {
        People peeps = new People();
        Method[] getters = ReflectionTool.getterMethodDrilldown(People.class, "leader.name");
        Method[] setters = ReflectionTool.setterMethodDrilldown(getters);
        ReflectionTool.setNestedValue(getters, setters, peeps, "Alvin");
        assertEquals("Alvin", peeps.getLeader().getName());

    }
}
