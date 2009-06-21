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
		XmlParser<Date> parser = new XmlParser<Date>();
		Date d = (Date) parser.parse(xml);
		assertEquals(1226476800, d.getTime());
	}
	
	public void testDateTimeFromXml() throws Exception {
		String xml = ("<obj class=\"org.pojava.datetime.DateTime\">1226476.8</obj>\n");
		// System.out.println(xml);
		XmlParser<DateTime> parser = new XmlParser<DateTime>();
		DateTime dt = parser.parse(xml);
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
		XmlParser<Potpourri> parser = new XmlParser<Potpourri>();
		Potpourri pot=parser.parse(xml);
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
			+ "    <e>1</e>\n"
			+ "    <e>2</e>\n"
			+ "    <e>3</e>\n"
			+ "  </numbers>\n"
			+ "  <confused ref=\"1\"/>\n"
			+ "  <five>5</five>\n" + "  <dt>86400.0</dt>\n"
			+ "  <bob>\n"
			+ "    <obj class=\"Long\">9876543210</obj>\n"
			+ "  </bob>\n" + "  <str>Hello</str>\n" + "</obj>\n";
		XmlParser<Potpourri> parser = new XmlParser<Potpourri>();
		Potpourri pot=parser.parse(xml);
		assertEquals(86400000, pot.getD().getTime());
		assertEquals(pot, pot.getConfused());
		assertEquals(5, pot.getFive());
		assertEquals(86400000, pot.getDt().toMillis());
		assertEquals(new Long(9876543210L), (Long) pot.getBob());
		assertEquals("Hello", pot.getStr());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
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
		XmlParser<Map<Object,Object>> parser = new XmlParser<Map<Object,Object>>();
		Map<Object,Object> map=(Map<Object,Object>) parser.parse(sb.toString());
		assertEquals(2,map.size());
	}

	public void testArrayInt() {
		StringBuffer sb=new StringBuffer();
		sb.append("<obj class=\"[I\">\n");
		sb.append("  <e>1</e>\n");
		sb.append("  <e>2</e>\n");
		sb.append("  <e>3</e>\n");
		sb.append("</obj>\n");
		XmlParser<int[]> parser = new XmlParser<int[]>();
		int[] numbers=parser.parse(sb.toString());
		assertEquals(3,numbers.length);
	}

	public void testNulls() {
		StringBuffer sb=new StringBuffer();
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
		XmlParser<Potpourri> parser=new XmlParser<Potpourri>();
		Potpourri pojo=parser.parse(sb.toString());
		assertNull(pojo.getBob());
		assertNull(pojo.getConfused());
		assertNull(pojo.getD());
		assertNull(pojo.getDt());
		assertNull(pojo.getMap());
		assertNull(pojo.getNumbers());
		assertNull(pojo.getSet());
		assertNull(pojo.getStr());
		assertEquals(0, pojo.getFive());
	}
}
