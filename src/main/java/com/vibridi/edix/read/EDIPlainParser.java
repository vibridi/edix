package com.vibridi.edix.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.vibridi.edix.error.EDISyntaxException;

public class EDIPlainParser /*extends EDIParser*/ {

	
	
//	@Override
	public void parse(InputStream source) throws EDISyntaxException, IOException {
		
		InputStreamReader in = new InputStreamReader(source);
		EDILexer lx = EDILexerFactory.newFor(in);
		
		
		
		//TokenStream ts = lx.getTokenStream();
		
	//	source.
		
	}
	
}
