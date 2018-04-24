package com.vibridi.edix.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class TestWriter {

	@Test
	public void createAndUseWriter() throws Exception {
		EDIMessage m = TestResources.getAsMessage("minimal-interchange.edi");		
		assertNotNull(m);
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, m);
		assertNotNull(w);
	}
	
	@Test
	public void writeToTextSimple() throws Exception {
		String interchange = TestResources.getAsString("minimal-interchange.edi");
		EDILexer lx = TestResources.getAsLexer("minimal-interchange.edi");		
		EDIParser parser = EDIParser.newInstance(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, m);
		w.setPrettyPrint(true);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		assertEquals(interchange, out.toString().trim());
	}
	
	@Test
	public void writeToTextComposite() throws Exception {
		String interchange = TestResources.getAsString("test-interchange-sub.edi");
		EDILexer lx = TestResources.getAsLexer("test-interchange-sub.edi");		
		EDIParser parser = EDIParser.newInstance(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, m);
		w.setPrettyPrint(true);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		assertEquals(interchange, out.toString().trim());
	}
	
	@Test
	public void writeToTextCompositeFirstSubEmpty() throws Exception {
		String interchange = TestResources.getAsString("test-interchange-sub-first-empty.edi");
		EDILexer lx = TestResources.getAsLexer("test-interchange-sub-first-empty.edi");		
		EDIParser parser = EDIParser.newInstance(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, m);
		w.setPrettyPrint(true);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		assertEquals(interchange, out.toString().trim());
	}
	
	@Test
	public void writeToTextRepetitions() throws Exception {
		String interchange = TestResources.getAsString("test-interchange-rep.edi");
		EDILexer lx = TestResources.getAsLexer("test-interchange-rep.edi");		
		EDIParser parser = EDIParser.newInstance(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, m);
		w.setPrettyPrint(true);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		assertEquals(interchange, out.toString().trim());
	}
	
}
