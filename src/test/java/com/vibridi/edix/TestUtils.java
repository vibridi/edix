package com.vibridi.edix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.writer.EDIWriter;

public class TestUtils {

	@FunctionalInterface
	public interface EDIMessageProcessorDelegate<T> {
		public T process(EDIMessage m);
	}
	
	public static Document writeToDOM(EDIMessage m) {
		try {
			EDIWriter w = EDIFactory.newWriter(EDIFormat.XML, m);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			w.write(out, "UTF-8");
	
			return DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(out.toByteArray()));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T readAndThen(String edi, EDIMessageProcessorDelegate<T> delegate) throws Exception {
		EDIMessage m = TestResources.getAsMessage(edi);
		return delegate.process(m);
	}
	
	
}
