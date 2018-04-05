package com.vibridi.edix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import org.junit.Test;

import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class TestReader {

	
	@Test
	public void detectStandard() throws IOException {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		EDIReader reader = EDIFactory.newReader(EDIFormat.PLAIN_TEXT, in);
		EDIStandard std = reader.getStandard();
		assertEquals(std, EDIStandard.ANSI_X12);
	}
	
	@Test
	public void instantiate() throws Exception {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		EDIReader reader = EDIFactory.newReader(EDIFormat.PLAIN_TEXT, in);
		assertTrue(reader instanceof EDIPlainReader);
		
		in = TestResources.getAsStream("minimal-interchange.edi");
		reader = EDIFactory.newReader(EDIFormat.XML, in);
		assertTrue(reader instanceof EDIXMLReader);
	}
	
	
	@Test
	public void createAndUseLexer() throws EDISyntaxException, IOException {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		lexer.setSource(new PushbackReader(new InputStreamReader(in), 256));
		TokenType[] cc = lexer.getControlCharacters();
		
		assertEquals(cc[(int)'$'], TokenType.TERMINATOR);
		assertEquals(cc[(int)'~'], TokenType.DELIMITER);
		assertEquals(cc[(int)'<'], TokenType.SUB_DELIMITER);
	}
	
	@Test
	public void tokenize() throws Exception {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		lexer.setSource(new PushbackReader(new InputStreamReader(in), 256));
		TokenStream ts = lexer.tokenize();
		assertNotNull(ts);
		assertTrue(ts.hasNext());
	}
	
	@Test
	public void createAndUseParser() throws Exception {
		// GS~AG~04000~58401~040714~1003~38327~X~002040CHRY$;
		TokenStream ts = TestResources.getTokenStream("test-interchange-regular.edi");		
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(ts);
		assertNotNull(m);
		assertEquals(m.getTextAt("GS"), "GS");
		assertEquals(m.getTextAt("GS.1"), "AG");
	}
	
	@Test
	public void parseSubFields() throws Exception {
		// GS~AG<sub1<sub2~04000~58401~040714~1003~38327~X~002040CHRY$;
		TokenStream ts = TestResources.getTokenStream("test-interchange-sub.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(ts);
		assertNotNull(m);
		assertEquals(m.getTextAt("GS"), "GS");
		assertEquals(m.getTextAt("GS.1.1"), "AG");
		assertEquals(m.getTextAt("GS.1.2"), "sub1");
		assertEquals(m.getTextAt("GS.1.3"), "sub2");
//		
		m.walk(n -> {
			if(!n.getTextContent().isEmpty())
				System.out.println(n.getTextContent());
		});
		
	}
	
	@Test
	public void parseSubFieldsFirstEmpty() throws Exception {
		// GS~<AG<sub1<sub2~04000~58401~040714~1003~38327~X~002040CHRY$
		//    ^ empty first sub-field
		TokenStream ts = TestResources.getTokenStream("test-interchange-sub-first-empty.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(ts);
		assertNotNull(m);
		assertEquals(m.getTextAt("GS"), "GS");
		assertEquals(m.getTextAt("GS.1.1"), "");
		assertEquals(m.getTextAt("GS.1.2"), "AG");
		assertEquals(m.getTextAt("GS.1.3"), "sub1");
		assertEquals(m.getTextAt("GS.1.4"), "sub2");
		
		m.walk(n -> {
			if(!n.getTextContent().isEmpty())
				System.out.println(n.getTextContent());
		});
		
	}
	
}
