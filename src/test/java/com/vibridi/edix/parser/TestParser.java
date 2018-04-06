package com.vibridi.edix.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.vibridi.edix.EDIRegistry;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.path.EDIPath;

public class TestParser {

	@Test
	public void createAndUseParser() throws Exception {
		// GS~AG~04000~58401~040714~1003~38327~X~002040CHRY$;
		EDILexer lx = TestResources.getLexer("test-interchange-regular.edi");		
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		assertEquals(m.getTextAt(EDIPath.of("GS")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("GS.1")), "AG");
	}
	
	@Test
	public void parseSubFields() throws Exception {
		// GS~AG<sub1<sub2~04000~58401~040714~1003~38327~X~002040CHRY$;
		EDILexer lx = TestResources.getLexer("test-interchange-sub.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		assertEquals(m.getTextAt(EDIPath.of("GS")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.1")), "AG");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.2")), "sub1");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.3")), "sub2");
	}
	
	@Test
	public void parseSubFieldsFirstEmpty() throws Exception {
		// GS~<AG<sub1<sub2~04000~58401~040714~1003~38327~X~002040CHRY$
		//    ^ empty first sub-field
		EDILexer lx = TestResources.getLexer("test-interchange-sub-first-empty.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		assertEquals(m.getTextAt(EDIPath.of("GS")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.1")), "");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.2")), "AG");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.3")), "sub1");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.4")), "sub2");		
	}
	
	@Test
	public void parseMultipleSegments() throws Exception {
		EDILexer lx = TestResources.getLexer("test-interchange-multi.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		assertEquals(m.getTextAt(EDIPath.of("GS[1]")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("GS[1].6")), "38327");
		assertEquals(m.getTextAt(EDIPath.of("GS[2]")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("GS[2].6")), "38328");
		assertEquals(m.getTextAt(EDIPath.of("BGN[3].1.3")), "15");
	}
	
	@Test
	public void validateX12() throws Exception {
		EDILexer lx = TestResources.getLexer("test-interchange-multi.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		parser.validate(m);
	}
	
	@Test
	public void parseRepetitions() throws Exception {
		EDILexer lx = TestResources.getLexer("test-interchange-rep.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);

	}
}
