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

import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.impl.x12.X12Interchange;
import com.vibridi.edix.parser.EDIParser;

public class TestResources {
	
	public static String getAsString(String name) throws IOException, URISyntaxException {
		URI uri = TestResources.class.getResource("/" + name).toURI();
		return new String(Files.readAllBytes(Paths.get(uri)));
	}
	
	public static File getAsFile(String name) throws URISyntaxException {
		return new File(TestResources.class.getResource("/" + name).toURI());
	}
	
	public static InputStream getAsStream(String name) throws FileNotFoundException {
		return TestResources.class.getResourceAsStream("/" + name);
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
	
	public static EDILexer getAsLexer() throws Exception {
		return getAsLexer("minimal-interchange.edi");
	}
	
	public static EDILexer getAsLexer(String fileName) throws Exception {
		InputStream in = getAsStream(fileName);
		return EDIFactory.newReader(in).getLexer();
	}
	
	public static EDIMessage getAsMessage(String fileName) throws Exception {
		EDILexer lx = TestResources.getAsLexer(fileName);
		EDIParser parser = EDIParser.newInstance(EDIStandard.ANSI_X12);
		return parser.parse(lx);
	}
	
	public static X12Interchange getAsX12Interchange(String fileName) throws Exception {
		return new X12Interchange(getAsMessage(fileName));
	}

}
