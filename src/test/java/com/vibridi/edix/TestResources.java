package com.vibridi.edix;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.lexer.TokenStream;

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
	
	public static TokenStream getTokenStream() throws Exception {
		return getTokenStream("minimal-interchange.edi");
	}
	
	public static TokenStream getTokenStream(String fileName) throws Exception {
		InputStream in = getAsStream(fileName);
		
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		return lexer.setSource(new PushbackReader(new InputStreamReader(in), 256))
				.tokenize();
	}

}
