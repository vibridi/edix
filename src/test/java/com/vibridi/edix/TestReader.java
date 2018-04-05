package com.vibridi.edix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;
import com.vibridi.edix.path.EDIPath;

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
	
}
