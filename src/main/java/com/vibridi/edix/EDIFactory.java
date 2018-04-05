package com.vibridi.edix;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public class EDIFactory {
	
	public static enum EDIFormat {
		PLAIN_TEXT,
		XML
	}
	
	private static final int MAX_PUSHBACK = 256;

	public static EDIReader newReader(EDIFormat sourceFormat, InputStream in) throws IOException {
		
		EDIReader reader = null;
		PushbackReader source = new PushbackReader(new InputStreamReader(in), MAX_PUSHBACK);
		
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
	
	
	
	
	
}
