package com.vibridi.edix.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.EDIPlainReader;
import com.vibridi.edix.EDIReader;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.EDIXMLReader;
import com.vibridi.edix.TestResources;

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
