package com.vibridi.edix;

import java.io.IOException;
import java.io.PushbackReader;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.model.EDIMessage;

public class EDIPlainReader extends EDIReader {

    public EDIPlainReader(PushbackReader source) throws IOException {
		super(source);
	}
	
	@Override
	public EDIMessage read() throws EDISyntaxException, IOException {
		EDILexer lexer = EDIRegistry.newLexer(standard);
		lexer.setSource(source);
		
		TokenStream ts = lexer.tokenize();
		EDIMessage message = EDIRegistry.newParser(standard).parse(ts);
		message.setControlCharacters(lexer.getControlCharacters());
		
		return message;
	}
	
	
}
