package com.vibridi.edix.parser;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathConstants;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.TestUtils;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.util.XMLUtils;
import com.vibridi.edix.writer.EDIXMLWriter;

/**
 * Compares the XML output produced by the C# library OopFactory.X12Parser with the one produced by Edix.
 * The X12Parser output must be available in the classpath. Edix output is produced at runtime.
 * The test cases pass when the segment and loop hierarchies are identical. In other words it checks for 
 * inconsistencies in how Edix builds the parent-child structure. 
 *	
 * @author gabriele.vaccari
 *
 */
public class TestStructureAgainstX12Parser {
	
	private static final String pattern = "%s/%s.%s";
	
	private static String getXmlName(String name) {
		return String.format(pattern, "benchmarks", name, "xml");
	}
	
	private static String getEdiName(String name) {
		return String.format(pattern, "transactions-x12", name, "edi");
	}

	@Test
	public void compare820() throws Exception {
		compareBenchmarkWithEdix("_820/Example1_MortgageBankers");
		compareBenchmarkWithEdix("_820/Example2_ScanaEnergy");
		compareBenchmarkWithEdix("_820/Example3_HomeDepot");
		compareBenchmarkWithEdix("_820/Example4_Safeway");
		compareBenchmarkWithEdix("_820/Example5_Wikipedia");
		compareBenchmarkWithEdix("_820/Example6");
		compareBenchmarkWithEdix("_820/Example7_FromScott");
	}
	
	@Test
	public void compare835_4010() throws Exception {
		compareBenchmarkWithEdix("_835/_4010/835_DeIdent_01");
		compareBenchmarkWithEdix("_835/_4010/835_DeIdent_02");
		compareBenchmarkWithEdix("_835/_4010/Example1_GripElements");
	}
	
	@Test
	public void compare835_5010() throws Exception {
		compareBenchmarkWithEdix("_835/_5010/Example1");
		compareBenchmarkWithEdix("_835/_5010/Example2");
	}
	
	@Test
	public void compare837P_4010() throws Exception {
		compareBenchmarkWithEdix("_837P/_4010/837_DeIdent_01");
		compareBenchmarkWithEdix("_837P/_4010/Cms1500Test");
		compareBenchmarkWithEdix("_837P/_4010/Spec_4.1.1_PatientIsSubscriber");
		compareBenchmarkWithEdix("_837P/_4010/Spec_4.1.2_PatientIsNotSubscriber");
		compareBenchmarkWithEdix("_837P/_4010/Spec_4.1.3A_COB_ClaimToPayerAFromBP");
		compareBenchmarkWithEdix("_837P/_4010/Spec_4.2.1_PayerIsPCIns");
	}
	
	@Test
	public void compare837P_5010() throws Exception {
		compareBenchmarkWithEdix("_837P/_5010/Example1_HealthInsurance");
		compareBenchmarkWithEdix("_837P/_5010/Example2_Encounter");
		compareBenchmarkWithEdix("_837P/_5010/Example3_COB");
		compareBenchmarkWithEdix("_837P/_5010/Example1_2_And_3_Combined");
		compareBenchmarkWithEdix("_837P/_5010/MedicaidExample");
		compareBenchmarkWithEdix("_837P/_5010/ST1309-XX");
	}
	
	@Test(expected = RuntimeException.class)
	public void compare837P_5010_MissingEntityCode() throws Exception {
		compareBenchmarkWithEdix("_837P/_5010/MissingEntityCode");
	}
	
	/**
	 * Compares the XML output produced by the C# library OopFactory.X12Parser with the one produced by Edix.
	 * @param source File name
	 * @throws Exception Any error
	 */
	public void compareBenchmarkWithEdix(String source) throws Exception {
		Document d1 = TestResources.getAsDOM(getXmlName(source));
		Document d2 = TestUtils.readAndThen(getEdiName(source), TestUtils::writeToDOM);
		
		NodeList s1 = (NodeList) XMLUtils.applyXPath(d1, "//Transaction", null, XPathConstants.NODESET);
		NodeList s2 = (NodeList) XMLUtils.applyXPath(d2, "//Transaction", null, XPathConstants.NODESET);
		
		assertEquals(s1.getLength(), s2.getLength());
		
		for(int i = 0; i < s1.getLength(); i++) {
			compareLoops((Element) s1.item(i), (Element) s2.item(i));
		}
	}
	
	private void compareLoops(Element loop1, Element loop2) {
		List<Node> ln1 = getChildren(loop1);
		List<Node> ln2 = getChildren(loop2);
		
		// Store and compare segments
		List<String> seg1 = ln1.stream()
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.map(Node::getNodeName)
				.filter(s -> !s.equals("Loop") && !s.equals("HierarchicalLoop"))
				.collect(Collectors.toList());
		seg1.remove("ST");
		seg1.remove("SE");
		
		List<String> seg2 = ln2.stream()
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.filter(n -> n.getNodeName().equals("Segment"))
				.map(n -> n.getAttributes().getNamedItem("id").getTextContent())
				.collect(Collectors.toList());
		
		assertEquals(seg1, seg2);
		
		// Store loops
		List<Entry<String,Element>> list1 = ln1.stream()
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.filter(n -> n.getNodeName().equals("Loop") || n.getNodeName().equals("HierarchicalLoop"))
				.map(n -> new AbstractMap.SimpleEntry<>(
						n.getAttributes().getNamedItem("LoopId").getTextContent(), 
						(Element) n))
				.collect(Collectors.toList());
		
		List<Entry<String,Element>> list2 = ln2.stream()
				.filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
				.filter(n -> n.getNodeName().equals("Loop"))
				.map(n -> new AbstractMap.SimpleEntry<>(
						n.getAttributes().getNamedItem("id").getTextContent(), 
						(Element) n))
				.collect(Collectors.toList());
		
		assertEquals("Numbers of children in " + loop1.getNodeName() + " don't match.", list1.size(), list2.size());
		
		// Compare loops
		seg1 = list1.stream()
				.map(Entry::getKey)
				.collect(Collectors.toList());
		
		seg2 = list2.stream()
				.map(Entry::getKey)
				.collect(Collectors.toList());
		
		assertEquals(String.format("Loops in %s[id=%s] don't match", 
				loop1.getNodeName(),
				loop1.getAttribute("LoopId")), seg1, seg2);
		
		// Recursively compare loop children
		for(int i = 0; i < list1.size(); i++) {
			compareLoops(list1.get(i).getValue(), list2.get(i).getValue());
		}
	}
	
	
	private List<Node> getChildren(Element e) {
		List<Node> l = new ArrayList<>();
		NodeList nl = e.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++)
			l.add(nl.item(i));
		return l;
	}
	
//	@Test
//	public void testPrint() throws Exception {
//		EDIMessage m = TestResources.getAsMessage("transactions-x12/_837P/_4010/Cms1500Test.edi");
//		EDIXMLWriter w = (EDIXMLWriter) EDIFactory.newWriter(EDIFormat.XML, m);
//		String xml = w.writeToString("UTF-8");
//		System.out.println(xml);
//	}
	
}
