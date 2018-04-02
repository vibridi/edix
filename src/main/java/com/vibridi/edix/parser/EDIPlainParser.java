package com.vibridi.edix.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.lexer.EDILexerFactory;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.model.EDIMessage;

public class EDIPlainParser extends EDIParser {

	
	@Override
	public EDIMessage parse(InputStream source) throws EDISyntaxException, IOException {
		
		TokenStream ts = EDILexerFactory.newFor(new InputStreamReader(source))
				.tokenize();

		
		while(ts.hasNext()) {
			
			
			
			
			
			
		}
		
		return null; // TODO
	}
	
}
