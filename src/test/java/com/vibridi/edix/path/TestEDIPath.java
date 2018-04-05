package com.vibridi.edix.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.vibridi.edix.EDIRegistry;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.error.EDIPathException;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class TestEDIPath {

	private EDIMessage m;
	
	@Before
	public void setup() throws Exception {
		TokenStream ts = TestResources.getTokenStream("test-interchange-complex.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		m = parser.parse(ts);
		assertNotNull(m);
	}
	
	
	@Test
	public void testSimplePath() throws EDIPathException {
		assertEquals(m.getTextAt(EDIPath.of("GS")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.1")), "");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.2")), "AG");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.3")), "sub1");
		assertEquals(m.getTextAt(EDIPath.of("GS.1.4")), "sub2");	
	}
	
	
	
	
}
