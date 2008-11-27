package org.pojava.persistence.serial;

import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;

import org.pojava.datetime.DateTime;
import org.pojava.persistence.examples.Potpourri;

public class XmlParserTester extends TestCase {

	public void testDateFromXml() throws Exception {
		String xml = ("<obj class=\"java.util.Date\">1226476800</obj>\n");
		// System.out.println(xml);
		XmlParser parser = new XmlParser();
		Date d = (Date) parser.parse(xml);
		assertEquals(1226476800, d.getTime());
	}
	
	public void testDateTimeFromXml() throws Exception {
		String xml = ("<obj class=\"org.pojava.datetime.DateTime\">1226476.8</obj>\n");
		// System.out.println(xml);
		XmlParser parser = new XmlParser();
		DateTime dt = (DateTime) parser.parse(xml);
		assertEquals(1226476800, dt.toMillis());
	}
	
	public void testPotpourri() throws Exception {
		String xml=""
			+ "<obj class=\"org.pojava.persistence.examples.Potpourri\" mem=\"1\">\n"
			+ "  <d>86400000</d>\n"
			+ "  <confused ref=\"1\"/>\n"
			+ "  <five>5</five>\n"
			+ "  <dt>86400.0</dt>\n"
			+ "  <bob>\n"
			+ "    <obj class=\"Long\">9876543210</obj>\n"
			+ "  </bob>\n"
			+ "  <str>hello</str>\n"
			+ "</obj>\n";
		XmlParser parser = new XmlParser();
		Potpourri pot=(Potpourri) parser.parse(xml);
		assertEquals(86400000, pot.getD().getTime());
		assertEquals(pot, pot.getConfused());
		assertEquals(5, pot.getFive());
		assertEquals(86400000, pot.getDt().toMillis());
		assertEquals(new Long(9876543210L), (Long) pot.getBob());
		assertEquals("hello", pot.getStr());
	}

	public void testPotpourri2() throws Exception {
		String xml=				"<obj class=\"org.pojava.persistence.examples.Potpourri\" mem=\"1\">\n"
			+ "  <d>86400000</d>\n"
			+ "  <numbers>\n"
			+ "    <obj class=\"Integer\">1</obj>\n"
			+ "    <obj class=\"Integer\">2</obj>\n"
			+ "    <obj class=\"Integer\">3</obj>\n"
			+ "  </numbers>\n"
			+ "  <confused ref=\"1\"/>\n"
			+ "  <five>5</five>\n" + "  <dt>86400.0</dt>\n"
			+ "  <bob>\n"
			+ "    <obj class=\"Long\">9876543210</obj>\n"
			+ "  </bob>\n" + "  <str>Hello</str>\n" + "</obj>\n";
		XmlParser parser = new XmlParser();
		Potpourri pot=(Potpourri) parser.parse(xml);
		assertEquals(86400000, pot.getD().getTime());
		assertEquals(pot, pot.getConfused());
		assertEquals(5, pot.getFive());
		assertEquals(86400000, pot.getDt().toMillis());
		assertEquals(new Long(9876543210L), (Long) pot.getBob());
		assertEquals("Hello", pot.getStr());
	}
	
	public void testMap() throws Exception {
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
		XmlParser parser = new XmlParser();
		Map map=(Map) parser.parse(sb.toString());
		assertEquals(2,map.size());
	}
}
