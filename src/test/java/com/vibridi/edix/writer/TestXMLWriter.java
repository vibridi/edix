package com.vibridi.edix.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.util.XMLUtils;

public class TestXMLWriter {
	
	@Test
	public void writeToXMLSimple() throws Exception {
		Document docA = TestResources.getAsDOM("benchmarks/x12-110.xml");
		EDIMessage m = TestResources.getAsMessage("transactions-x12/110.edi");
				
		EDIWriter w = EDIFactory.newWriter(EDIFormat.XML, m);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		w.write(out, "UTF-8");

		Document docB = 
		DocumentBuilderFactory.newInstance()
			.newDocumentBuilder()
			.parse(new ByteArrayInputStream(out.toByteArray()));
		
		// Assert ISA XML representation
		// Must test all ISA fields, because they will be needed to read the XML back into a valid EDIMessage object
		assertXPath("/Interchange/InterchangeControlHeader/@type", docA, docB, "ISA");
		assertXPath("/Interchange/InterchangeControlHeader/@terminator", docA, docB, " ");
		assertXPath("/Interchange/InterchangeControlHeader/AuthorizationInformationQualifier", docA, docB, "01");
		assertXPath("/Interchange/InterchangeControlHeader/AuthorizationInformation", docA, docB, "0000000000");
		assertXPath("/Interchange/InterchangeControlHeader/SecurityInformationQualifier", docA, docB, "01");
		assertXPath("/Interchange/InterchangeControlHeader/SecurityInformation", docA, docB, "0000000000");
		assertXPath("/Interchange/InterchangeControlHeader/InterchangeIdQualifier", docA, docB, "ZZ");
		assertXPath("/Interchange/InterchangeControlHeader/SenderId", docA, docB, "ABCDEFGHIJKLMNO");
		assertXPath("/Interchange/InterchangeControlHeader/InterchangeIdQualifier", docA, docB, "ZZ");
		assertXPath("/Interchange/InterchangeControlHeader/ReceiverId", docA, docB, "123456789012345");
		assertXPath("/Interchange/InterchangeControlHeader/Date", docA, docB, "101127");
		assertXPath("/Interchange/InterchangeControlHeader/Time", docA, docB, "1719");
		assertXPath("/Interchange/InterchangeControlHeader/RepetitionSeparator", docA, docB, "U");
		assertXPath("/Interchange/InterchangeControlHeader/VersionNumber", docA, docB, "00400");
		assertXPath("/Interchange/InterchangeControlHeader/ControlNumber", docA, docB, "000000009");
		assertXPath("/Interchange/InterchangeControlHeader/AckRequested", docA, docB, "0");
		assertXPath("/Interchange/InterchangeControlHeader/TestIndicator", docA, docB, "P");
		assertXPath("/Interchange/InterchangeControlHeader/SubDelimiter", docA, docB, ">");
		
		// Assert GS XML representation
		// Must test all GS fields, because they will be needed to read the XML back into a valid EDIMessage object
		assertXPath("/Interchange/FunctionalGroup/@code", docA, docB, "NL");
		assertXPath("/Interchange/FunctionalGroup/@controlNumber", docA, docB, "1");
		assertXPath("/Interchange/FunctionalGroup/ApplicationSenderCode", docA, docB, "1234567890 ");
		assertXPath("/Interchange/FunctionalGroup/ApplicationReceiverCode", docA, docB, "999999999 ");
		assertXPath("/Interchange/FunctionalGroup/Date", docA, docB, "20120126");
		assertXPath("/Interchange/FunctionalGroup/Time", docA, docB, "1211");
		assertXPath("/Interchange/FunctionalGroup/ResponsibleAgency", docA, docB, "T");
		assertXPath("/Interchange/FunctionalGroup/VersionIdCode", docA, docB, "004010");
		
		// Assert ST XML representation
		// Must test all ST fields, because they will be needed to read the XML back into a valid EDIMessage object
		assertXPath("/Interchange/FunctionalGroup/TransactionSet/@code", docA, docB, "110");
		assertXPath("/Interchange/FunctionalGroup/TransactionSet/@controlNumber", docA, docB, "000000001");
		
		// Assert document
		assertXPath("//TransactionSet/Segment[@id='B3']/Field[@id='02']", docA, docB, "88799686");
		assertXPath("//TransactionSet/Segment[@id='B3A']/Field[@id='01']", docA, docB, "SS");
		assertXPath("//TransactionSet/Loop[@id='N1'][2]/Segment[@id='N1']/Field[@id='02']", docA, docB, "CUSTOMER NAME");
		assertXPath("//TransactionSet/Loop[@id='N1'][3]/Segment[@id='N3']/Field[@id='02']", docA, docB, "ELECTRONICS CITY RD");
		assertXPath("//TransactionSet/Loop[@id='LX']/Loop[@id='L5']/Segment[@id='L0']/Field[@id='04']", docA, docB, "99.5");
		assertXPath("//TransactionSet//Loop[@id='L1'][3]/Segment[@id='L1']/Field[@id='08']", docA, docB, "WAR");
	}
	
	@Test
	public void writeToXMLCompositeAndRepetitions() throws Exception {
		Document docA = TestResources.getAsDOM("benchmarks/x12-824-fake.xml");		
		EDIMessage m = TestResources.getAsMessage("test-interchange-rep.edi");		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.XML, m);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		Document docB = 
		DocumentBuilderFactory.newInstance()
			.newDocumentBuilder()
			.parse(new ByteArrayInputStream(out.toByteArray()));
		
		// Assert ISA XML representation
		assertXPath("/Interchange/InterchangeControlHeader/@type", docA, docB, "ISA");
		assertXPath("/Interchange/InterchangeControlHeader/@terminator", docA, docB, "$");
		assertXPath("/Interchange/InterchangeControlHeader/RepetitionSeparator", docA, docB, "^");
		assertXPath("/Interchange/InterchangeControlHeader/SubDelimiter", docA, docB, "<");
		
		// Assert repeated field
		Node n = (Node) XMLUtils.applyXPath(docB, "//TransactionSet/Segment[@id='DMG']/Field[@id='05']/@repeated", null, XPathConstants.NODE);
		assertNotNull(n);
		assertEquals(n.getTextContent(), "true");
		
		// Assert empty first field of composite field (as in ~<RET<R5~)
		// 													   ^ empty first field
		assertXPath("//TransactionSet/Segment[@id='DMG']/Field[@id='05']/Field[@id='01']/Field[@id='01']", docA, docB, "");
		
		// Assert regular fields of composite field
		assertXPath("//TransactionSet/Segment[@id='DMG']/Field[@id='05']/Field[@id='02']/Field[@id='03']", docA, docB, "E1.01");
		
		// Assert fields before and after the repeated field
		assertXPath("//TransactionSet/Segment[@id='DMG']/Field[@id='04']", docA, docB, "");
		assertXPath("//TransactionSet/Segment[@id='DMG']/Field[@id='06']", docA, docB, "");
		
		// Assert repeated field with simple text elements
		assertXPath("//TransactionSet/Segment[@id='MSG'][1]/Field[@id='01']/@repeated", docA, docB, "true");
		assertXPath("//TransactionSet/Segment[@id='MSG'][1]/Field[@id='01']/Field[@id='01']", docA, docB, "test1");
		assertXPath("//TransactionSet/Segment[@id='MSG'][1]/Field[@id='01']/Field[@id='02']", docA, docB, "test-rep");
		
		// Assert field with sub-elements
		assertXPath("//TransactionSet/Segment[@id='MSG'][2]/Field[@id='01']/@repeated", docA, docB, "");
		assertXPath("//TransactionSet/Segment[@id='MSG'][2]/Field[@id='01']/Field[@id='01']", docA, docB, "test2");
		assertXPath("//TransactionSet/Segment[@id='MSG'][2]/Field[@id='01']/Field[@id='02']", docA, docB, "test3");
		assertXPath("//TransactionSet/Segment[@id='MSG'][2]/Field[@id='01']/Field[@id='03']", docA, docB, "test4");
		assertXPath("//TransactionSet/Segment[@id='MSG'][2]/Field[@id='02']", docA, docB, "test5");
	}
	
	public void assertXPath(String xpath, Document A, Document B, String control) throws XPathExpressionException {
		String sA = (String) XMLUtils.applyXPath(A, xpath, null, XPathConstants.STRING);
		String sB = (String) XMLUtils.applyXPath(B, xpath, null, XPathConstants.STRING);
		assertEquals(sA, sB);
		assertEquals(sA, control);
	}
}
