package com.vibridi.edix.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIRegistry;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class TestXMLWriter {
	
	@Test
	public void writeToXML() throws Exception {
		EDILexer lx = TestResources.getAsLexer("transactions-x12/110.edi");		
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		assertNotNull(parser);
		
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.XML, m);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		//System.out.println(out.toString().trim());
	}
	
	@Test
	public void writeToTextSimple() throws Exception {
		String interchange = TestResources.getAsString("minimal-interchange.edi");
		EDILexer lx = TestResources.getAsLexer("minimal-interchange.edi");		
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
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
		// String interchange = TestResources.getAsString("test-interchange-sub.edi");
		
		EDIMessage m = TestResources.getAsMessage("test-interchange-rep.edi");		
		
		EDIWriter w = EDIFactory.newWriter(EDIFormat.XML, m);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		w.write(out, "UTF-8");
		
		out.writeTo(System.out);
		
	}
	
	@Test
	public void writeToTextCompositeFirstSubEmpty() throws Exception {
		String interchange = TestResources.getAsString("test-interchange-sub-first-empty.edi");
		EDILexer lx = TestResources.getAsLexer("test-interchange-sub-first-empty.edi");		
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
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
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
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
