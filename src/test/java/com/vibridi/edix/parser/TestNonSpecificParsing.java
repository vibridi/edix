package com.vibridi.edix.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathConstants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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

public class TestNonSpecificParsing {

	@Rule public TemporaryFolder tmpfolder = new TemporaryFolder();
	
	@Test
	public void parse820() throws Exception {
		String ediResource = "transactions-x12/_820/Example1_MortgageBankers.edi";
		String bmk = getBenchmark(ediResource);
		Document d1 = XMLUtils.stringToDocument(bmk);
		Document d2 = TestUtils.readAndThen(ediResource, TestUtils::writeToDOM);
		
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
		
		// Store and compare loop names
		
		
		
	}
	
	
	private List<Node> getChildren(Element e) {
		List<Node> l = new ArrayList<>();
		NodeList nl = e.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++)
			l.add(nl.item(i));
		return l;
	}
	
	@Test
	public void testBenchmark() throws Exception {
		String s = getBenchmark("transactions-x12/278.edi");
		System.out.println(s);
	}
	
	@Test
	public void testPrint() throws Exception {
		EDIMessage m = TestResources.getAsMessage("transactions-x12/_820/Example1_MortgageBankers.edi");
		EDIXMLWriter w = (EDIXMLWriter) EDIFactory.newWriter(EDIFormat.XML, m);
		String xml = w.writeToString("UTF-8");
		System.out.println(xml);
	}
	
	
	private String getBenchmark(String inf) throws Exception {
		String in = TestResources.getAsFile(inf).getAbsolutePath();
		
		File tmpf = tmpfolder.newFile(UUID.randomUUID().toString());
		String out = tmpf.getAbsolutePath();
		
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"x12parser.bat", in, out};
		Process proc = rt.exec(commands);
		
		proc.waitFor();
		
		if(proc.exitValue() != 0)
			throw new IOException("X12Parser.exe didn't terminate correctly.");
		
		return new String(Files.readAllBytes(Paths.get(tmpf.toURI())));
	}
	
}
