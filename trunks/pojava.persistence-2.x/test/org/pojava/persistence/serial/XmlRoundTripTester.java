package org.pojava.persistence.serial;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.pojava.persistence.examples.Objet;
import org.pojava.persistence.examples.Person;
import org.pojava.persistence.examples.Potpourri;

public class XmlRoundTripTester extends TestCase {
    
    private void roundTrip(Objet obj) {
        XmlDefs defs = new XmlDefs();
        XmlSerializer serializer = new XmlSerializer(defs);
        String xml=serializer.toXml(obj);
        XmlParser<Objet> parser=new XmlParser<Objet>(defs);
        // System.out.println(xml);
        Objet obj2=parser.parse(xml);
        String xml2=serializer.toXml(obj2);
        assertEquals(xml,xml2);        
    }

    private void roundTrip(Object obj) {
        XmlDefs defs = new XmlDefs();
        XmlSerializer serializer = new XmlSerializer(defs);
        String xml=serializer.toXml(obj);
        XmlParser<Object> parser=new XmlParser<Object>(defs);
        // System.out.println(xml);
        Object obj2=parser.parse(xml);
        String xml2=serializer.toXml(obj2);
        // System.out.println(xml2);
        assertEquals(xml,xml2);        
    }

    public void testObjetNull() {
        Objet obj=new Objet();
        obj.setMisc(null);
        roundTrip(obj);
    }

    public void testObjetObject() {
        Objet obj=new Objet();
        obj.setMisc(new Object());
        roundTrip(obj);
    }

    public void testObjetLong() {
        Objet obj=new Objet();
        obj.setMisc(new Long(123));
        roundTrip(obj);
    }

    public void testObjetIntArray0() {
        Objet obj=new Objet();
        int[] ints={};
        obj.setMisc(ints);
        roundTrip(obj);
    }

    public void testObjetIntArray1() {
        Objet obj=new Objet();
        int[] ints={1};
        obj.setMisc(ints);
        roundTrip(obj);
    }

    public void testObjetObjectArray0() {
        Objet obj=new Objet();
        Object[] objs={};
        obj.setMisc(objs);
        roundTrip(obj);
    }

    public void testObjetObjectArray1() {
        Objet obj=new Objet();
        Object[] objs={new Object()};
        obj.setMisc(objs);
        roundTrip(obj);
    }

    public void testObjetObjectArray1Null() {
        Objet obj=new Objet();
        Object[] objs={null};
        obj.setMisc(objs);
        roundTrip(obj);
    }

    public void testList() {
        Person p1=new Person();
        Person p2=new Person();
        Person p3=new Person();
        p1.setName("One");
        p2.setName("Two");
        p3.setName("Three");
        List<Person> list=new ArrayList<Person>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        Potpourri pot=new Potpourri();
        pot.setList(list);
        roundTrip(pot);
    }
    
    public void testChained() {
        Person p1=new Person();
        Person p2=new Person();
        Person p3=new Person();
        p1.setName("One");
        p2.setName("Two");
        p3.setName("Three");
        p2.setParent(p1);
        p3.setParent(p2);
        p1.setParent(p3); // Circular is allowed.
        roundTrip(p1);
        roundTrip(p2);
        roundTrip(p3);
    }
    


}