package com.vibridi.edix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SpecConverter {

	// READ FROM
	private static final String tset = "820";
	private static final String version = "4010";
	private static final String source = "xml-descriptors/Ansi-%s-%sSpecification.xml";
	
	
	// WRITE TO
	private static final String dstVersion = "all";
	private static final String desc = "Payment Order/Remittance Advice";
	private static final String fileOut ="src/main/resources/transaction-descriptors/ANSI_X12_%s.json";
	
	public static void main(String[] args) throws Exception {
		Document d = SpecConverter.getAsDOM(String.format(source, tset, version));
		ObjectMapper om = new ObjectMapper();
		
		
		File f = new File(String.format(fileOut, tset));
		if(!f.exists()) {
			ObjectNode root = om.createObjectNode();
			createSpec(d, root);
			om.writerWithDefaultPrettyPrinter().writeValue(f, root);
			
		} else {
			JsonNode root = om.readTree(f);
			ObjectNode versions = (ObjectNode) root.get("versions");		
			ObjectNode vobj = versions.putObject(dstVersion);
			vobj.put("name", tset);
			vobj.put("description", desc);
			
			Node rootn = d.getElementsByTagName("TransactionSpecification").item(0);
			doStuff(vobj, (Element) rootn);
			om.writerWithDefaultPrettyPrinter().writeValue(f, root);
		}
		
		
		System.out.println("Done!");
	}
	
	public static void createSpec(Document d, ObjectNode root) throws Exception {
		root.put("transactionSet", tset);
		ObjectNode versions = root.putObject("versions");
		ObjectNode vobj = versions.putObject(dstVersion);
		vobj.put("name", tset);
		vobj.put("description", desc);
		
		Node rootn = d.getElementsByTagName("TransactionSpecification").item(0);
		doStuff(vobj, (Element) rootn);
	}

	public static void doStuff(ObjectNode vobj, Element el) throws Exception {		
		Node fc = el.getFirstChild();
		ArrayNode an = vobj.putArray("segments");
		ArrayNode al = vobj.putArray("loops");
		while(fc != null) {
			if (fc.getNodeType() == Node.ELEMENT_NODE) {         
				Element e = (Element) fc; 
				
				if(e.getNodeName().equals("Segment"))
					an.add(e.getAttribute("SegmentId"));
				
				if(e.getNodeName().equals("HierarchicalLoop")) {
					ObjectNode loop = al.addObject();
					loop.put("name", e.getAttribute("LoopId"));
					loop.put("description", e.getElementsByTagName("Name").item(0).getTextContent());
					loop.put("startingSegment", "HL");
					
					if(e.hasAttribute("LevelCode")) {
						ArrayNode codes = loop.putArray("codes");
						codes.add(e.getAttribute("LevelCode"));
					}
					doStuff(loop, e);
				}
				
				if(e.getNodeName().equals("Loop")) {
					ObjectNode loop = al.addObject();
					loop.put("name", e.getAttribute("LoopId"));
					loop.put("description", e.getElementsByTagName("Name").item(0).getTextContent());
					Element ss =  (Element) e.getElementsByTagName("StartingSegment").item(0);
					loop.put("startingSegment", ss.getAttribute("SegmentId"));
					
					NodeList eid = ss.getElementsByTagName("EntityIdentifier");
					if(eid.getLength() > 0) {
						ArrayNode codes = loop.putArray("codes");
						for(int j = 0; j < eid.getLength(); j++) {
							Element ne = (Element) eid.item(j);
							codes.add(ne.getAttribute("Code"));
						}
						
					}
					
					doStuff(loop, e);
				}
			}   

			fc = fc.getNextSibling();
		}			
	}
	
	
	public static String getAsString(String name) throws IOException, URISyntaxException {
		URI uri = SpecConverter.class.getResource("/" + name).toURI();
		return new String(Files.readAllBytes(Paths.get(uri)));
	}
	
	public static File getAsFile(String name) throws URISyntaxException {
		return new File(SpecConverter.class.getResource("/" + name).toURI());
	}
	
	public static InputStream getAsStream(String name) throws FileNotFoundException {
		return SpecConverter.class.getResourceAsStream("/" + name);
	}
	
	public static Document getAsDOM(String file) throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder parser = fac.newDocumentBuilder();
		InputStream fis = getAsStream(file);
		Document doc = parser.parse(fis);
		fis.close();
		return doc;
	}
	
}
