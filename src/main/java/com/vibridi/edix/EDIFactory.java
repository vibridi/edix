package com.vibridi.edix;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.Objects;

import org.apache.commons.io.input.BOMInputStream;

import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.writer.EDIPlainWriter;
import com.vibridi.edix.writer.EDIWriter;
import com.vibridi.edix.writer.EDIXMLWriter;

public class EDIFactory {
	
	public static enum EDIFormat {
		PLAIN_TEXT,
		XML
	}
	
	private static final int MAX_PUSHBACK = 256;
	
	public static EDIReader newReader(InputStream in) throws IOException {
		return newReader(EDIFormat.PLAIN_TEXT, in);
	}

	public static EDIReader newReader(EDIFormat sourceFormat, InputStream in) throws IOException {
		Objects.requireNonNull(in);
		EDIReader reader = null;
		
		PushbackReader source = wrap(in);
		
		switch(sourceFormat) {
		case PLAIN_TEXT:
			reader = new EDIPlainReader(source);
			break;

		case XML:
			reader = new EDIXMLReader(source);
			break;

		default:
			throw new IllegalStateException("Unsupported EDI format: " + sourceFormat);	

		}		
		return reader;
		
	}
	
	
	public static EDIWriter newWriter(EDIFormat sourceFormat, EDIMessage message) {
		Objects.requireNonNull(message);
		switch(sourceFormat) {
		case PLAIN_TEXT:
			return new EDIPlainWriter(message);
			
		case XML:
			return new EDIXMLWriter(message);
			
		default:
			throw new IllegalStateException("Unsupported EDI format: " + sourceFormat);	
			
		}		
	}
	
	public static PushbackReader wrap(InputStream in) {
		return new PushbackReader(new InputStreamReader(new BOMInputStream(in)), MAX_PUSHBACK);
	}
}
