package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;

public class EDILexerFactory {
	
	private static final int MAX_PUSHBACK = 256; 
	private static final int MAX_LOOK_AHEAD = 3; 

	public static EDILexer newFor(Reader source) throws EDISyntaxException, IOException {
		PushbackReader pbr = new PushbackReader(source, MAX_PUSHBACK);

		// Grab the first few characters
		char[] buf = new char[MAX_LOOK_AHEAD];
		if(pbr.read(buf) < buf.length)
			throw new RuntimeException(ErrorMessages.LOOK_AHEAD_FAILED);
		pbr.unread(buf);

		// Get an appropriate lexer, based on the first few characters
		String asString = new String(buf);
		EDILexer lexer = ParserRegistry.get(asString).orElseThrow(() -> {
			return new EDISyntaxException("<?xml ".startsWith(asString) 
					? ErrorMessages.XML_INSTEAD_OF_EDI 
					: ErrorMessages.NO_STANDARD_BEGINS_WITH + asString); 
		}); // TODO provide default lexer?
		lexer.setSource(pbr);
		lexer.prepare();
		return lexer;
	}

}
