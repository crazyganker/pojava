package org.pojava.persistence.serial;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.pojava.datetime.DateTime;
import org.pojava.persistence.examples.Person;
import org.pojava.persistence.examples.Potpourri;

public class XmlSerializerTester extends TestCase {

	public void testInteger() {
		Integer i = new Integer(42);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(i);
		assertEquals("<obj class=\"Integer\">42</obj>\n", xml);
	}

	public void testString() {
		String hello = "Say \"hello\".";
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(hello);
		assertEquals("<obj class=\"String\">Say &quot;hello&quot;.</obj>\n",
				xml);
	}

	public void testByte() {
		Byte b = new Byte((byte) 255);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(b);
		assertEquals("<obj class=\"Byte\">-1</obj>\n", xml);
	}

	public void testCharacter() {
		Character c = new Character('A');
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(c);
		assertEquals("<obj class=\"Character\">A</obj>\n", xml);
	}

	public void testArrayInt() throws Exception {
		int[] numbers = new int[3];
		numbers[0] = 1;
		numbers[1] = 2;
		numbers[2] = 3;
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(numbers);
		StringBuffer sb = new StringBuffer();
		sb.append("<obj class=\"[I\">\n");
		sb.append("  <e>1</e>\n");
		sb.append("  <e>2</e>\n");
		sb.append("  <e>3</e>\n");
		sb.append("</obj>\n");
		assertEquals(sb.toString(), xml);
	}

	public void testArrayInteger() {
		Integer[] numbers = new Integer[3];
		numbers[0] = new Integer(1);
		numbers[1] = new Integer(2);
		numbers[2] = new Integer(3);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(numbers);
		StringBuffer sb = new StringBuffer();
		sb.append("<obj class=\"[Ljava.lang.Integer;\">\n");
		sb.append("  <e>1</e>\n");
		sb.append("  <e>2</e>\n");
		sb.append("  <e>3</e>\n");
		sb.append("</obj>\n");
		assertEquals(sb.toString(), xml);
	}

	public void testSet() {
		Set set = new HashSet();
		set.add(new Integer(3));
		set.add(new Object());
		set.add("Yarn");
		XmlSerializer serializer = new XmlSerializer();
		List list = new ArrayList();
		list.add(set);
		String xml = serializer.toXml(list);
		assertTrue(xml.indexOf("<obj class=\"java.util.ArrayList\">\n") >= 0);
		assertTrue(xml.indexOf("  <obj class=\"java.util.HashSet\">\n") >= 0);
		assertTrue(xml.indexOf("  <obj class=\"Object\"/>\n") >= 0);
		assertTrue(xml.indexOf("  <obj class=\"java.util.HashSet\">\n") >= 0);
		assertTrue(xml.indexOf("  <obj class=\"Integer\">3</obj>\n") >= 0);
		assertTrue(xml.indexOf("    <obj class=\"String\">Yarn</obj>\n") >= 0);
	}

	public void testMap() {
		Map map = new HashMap();
		map.put("one", new Integer(1));
		map.put(new Integer(2), "two");
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(map);
		StringBuffer sb = new StringBuffer();
		sb.append("<obj class=\"java.util.HashMap\">\n");
		sb.append("  <map>\n");
		sb.append("    <obj class=\"Integer\">2</obj>\n");
		sb.append("    <obj class=\"String\">two</obj>\n");
		sb.append("  </map>\n");
		sb.append("  <map>\n");
		sb.append("    <obj class=\"String\">one</obj>\n");
		sb.append("    <obj class=\"Integer\">1</obj>\n");
		sb.append("  </map>\n");
		sb.append("</obj>\n");
		assertEquals(sb.toString(), xml);
	}

	public void testUtilDate() {
		Date d = new Date(123456789);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(d);
		assertEquals("<obj class=\"java.util.Date\">123456789</obj>\n", xml);
	}

	public void testSqlDate() {
		java.sql.Date d = new java.sql.Date(123456789);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(d);
		assertEquals("<obj class=\"java.sql.Date\">123456789</obj>\n", xml);
	}

	public void testTimestamp() {
		Timestamp ts = new Timestamp(-86400123);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(ts);
		assertEquals("<obj class=\"java.sql.Timestamp\">-86400.877</obj>\n",
				xml);
	}

	/**
	 * Objects that are not traditional POJO's pose some interesting challenges.
	 * The DTO pattern, for example, might define an immutable object with a big
	 * constructor and no setters, but all getters. This can serialize easily
	 * enough, but parsing and reconstructing the object requires custom code.
	 * 
	 * If one of the parameters of such an object is itself an object referenced
	 * elsewhere in the xml, then this can get sticky. When the referenced
	 * object is reached in the xml, it can reference an object in its own
	 * ancestry whose parameters haven't all been reached, so the parent object
	 * hasn't been constructed yet. An ordinary POJO doesn't have this problem,
	 * because the parent is constructed with a default constructor, so it's
	 * memory address is fixed. An immutable object, however, is still waiting
	 * to be defined, so the reference needs to be stored in some abstract way
	 * that is accessible to the custom constructor of the object that it will
	 * reference even before it is constructed.
	 */
	public void testPojo() {
		// This "POJO" is a little less plain than the typical "generate getters
		// and setters" variety.
		Object bob = new Long(9876543210L);
		Set set = new HashSet();
		set.add(new Integer(42));
		set.add("What is six times seven?");
		Map map = new HashMap();
		map.put("reset", set);
		map.put(new Object(), new Integer(1));
		map.put(new Date(1234), new DateTime(1234));
		Potpourri pojo = new Potpourri("hello", 5, new Date(86400000),
				new DateTime(86400, 0), bob, null, set, map);
		int[] numbers = { 1, 2, 3 };
		pojo.setNumbers(numbers);
		pojo.setConfused(pojo);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(pojo);
		// System.out.println(xml);
		assertTrue(xml
				.startsWith("<obj class=\"org.pojava.persistence.examples.Potpourri\" mem=\"1\">\n"));
		assertTrue(xml.indexOf("  <d>86400000</d>\n") >= 0);
		assertTrue(xml.indexOf("  <numbers>\n" + "    <e>1</e>\n"
				+ "    <e>2</e>\n" + "    <e>3</e>\n" + "  </numbers>\n") > 0);
		assertTrue(xml.indexOf("  <confused ref=\"1\"/>\n") > 0);
		assertTrue(xml.indexOf("  <set mem=\"2\">\n") > 0);
		assertTrue(xml.indexOf("    <obj class=\"Integer\">42</obj>\n") > 0);
		assertTrue(xml
				.indexOf("    <obj class=\"String\">What is six times seven?</obj>\n") > 0);
		assertTrue(xml.indexOf("  </set>\n") > 0);
		assertTrue(xml.indexOf("  <five>5</five>\n") > 0);
		assertTrue(xml.indexOf("  <dt>86400.0</dt>\n") > 0);
		assertTrue(xml.indexOf("  <bob>\n"
				+ "    <obj class=\"Long\">9876543210</obj>\n" + "  </bob>\n") > 0);
		assertTrue(xml.indexOf("  <str>hello</str>\n") > 0);
		assertTrue(xml.indexOf("</obj>\n") > 0);
	}

	public void testPerson() {
		Person bob = new Person();
		bob.setName("Bob");
		bob.setParent(bob);
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(bob);
		StringBuffer sb = new StringBuffer();
		sb
				.append("<obj class=\"org.pojava.persistence.examples.Person\" mem=\"1\">\n");
		sb.append("  <name>Bob</name>\n");
		sb.append("  <parent ref=\"1\"/>\n");
		sb.append("</obj>\n");
		assertEquals(sb.toString(), xml);
	}

	public void testNulls() {
		Potpourri pojo = new Potpourri();
		XmlSerializer serializer = new XmlSerializer();
		String xml = serializer.toXml(pojo);
		StringBuffer sb = new StringBuffer();
		sb.append("<obj class=\"org.pojava.persistence.examples.Potpourri\">\n");
		sb.append("  <dt><null/></dt>\n");
		sb.append("  <d><null/></d>\n");
		sb.append("  <bob><null/></bob>\n");
		sb.append("  <five>0</five>\n");
		sb.append("  <str><null/></str>\n");
		sb.append("  <numbers><null/></numbers>\n");
		sb.append("  <set><null/></set>\n");
		sb.append("  <confused><null/></confused>\n");
		sb.append("  <map><null/></map>\n");
		sb.append("</obj>\n");
		assertEquals(sb.toString(), xml);
	}
	
	public void testNullsByOmission() {
		// If omittingNulls is set true, nulls are left out of xml.
		// This works only if the default values are also null.
		Potpourri pojo = new Potpourri();
		XmlDefs defs=new XmlDefs();
		defs.setOmittingNulls(true);
		XmlSerializer serializer = new XmlSerializer(defs);
		String xml = serializer.toXml(pojo);
		StringBuffer sb = new StringBuffer();
		sb.append("<obj class=\"org.pojava.persistence.examples.Potpourri\">\n");
		sb.append("  <five>0</five>\n");
		sb.append("</obj>\n");
		assertEquals(sb.toString(), xml);
	}

	public void testOmission() {
		Set set = new HashSet();
		set.add(new Integer(42));
		set.add("What is six times seven?");
		Map map = new HashMap();
		map.put("reset", set);
		map.put(new Object(), new Integer(1));
		map.put(new Date(1234), new DateTime(1234));
		Potpourri pojo = new Potpourri("hello", 5, new Date(86400000),
				new DateTime(86400, 0), null, null, set, map);
		int[] numbers = { 1, 2, 3 };
		pojo.setNumbers(numbers);
		pojo.setConfused(pojo);
		XmlDefs defs = new XmlDefs();
		XmlSerializer serializer = new XmlSerializer(defs);
		String xml = serializer.toXml(pojo);
		assertTrue(xml.indexOf("<numbers>") > 0);
		defs.addOmission(pojo.getClass(), "numbers");
		xml = serializer.toXml(pojo);
		assertTrue(xml.indexOf("<numbers>") < 0);
	}

	public void testRename() {
		Potpourri pojo = new Potpourri("hello", 5, new Date(86400000),
				new DateTime(86400, 0), null, null, null, null);
		int[] numbers = { 1, 2, 3 };
		pojo.setNumbers(numbers);
		pojo.setConfused(pojo);
		XmlDefs defs = new XmlDefs();
		XmlSerializer serializer = new XmlSerializer(defs);
		String xml = serializer.toXml(pojo);
		// System.out.println(xml);
		assertTrue(xml.indexOf("<numbers>") > 0);
		assertTrue(xml.indexOf("<confused") > 0);
		assertTrue(xml.indexOf("<dt>") > 0);
		assertTrue(xml.indexOf("<d>") > 0);
		assertTrue(xml.indexOf("<five>") > 0);
		assertTrue(xml.indexOf("<str>") > 0);
		defs.rename(pojo.getClass(), "numbers", "numerals");
		defs.rename(pojo.getClass(), "confused", "cornfused");
		defs.rename(pojo.getClass(), "dt", "datetime");
		defs.rename(pojo.getClass(), "d", "date");
		defs.rename(pojo.getClass(), "five", "numberFive");
		defs.rename(pojo.getClass(), "str", "string");
		xml = serializer.toXml(pojo);
		// System.out.println(xml);
		assertTrue(xml.indexOf("<numerals>") > 0);
		assertTrue(xml.indexOf("<cornfused") > 0);
		assertTrue(xml.indexOf("<datetime>") > 0);
		assertTrue(xml.indexOf("<date>") > 0);
		assertTrue(xml.indexOf("<string>") > 0);
		assertTrue(xml.indexOf("<numberFive>5</numberFive>") > 0);

	}

}
