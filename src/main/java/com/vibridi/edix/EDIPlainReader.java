package com.vibridi.edix;

import java.io.IOException;
import java.io.PushbackReader;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class EDIPlainReader extends EDIReader {

    public EDIPlainReader(PushbackReader source) throws IOException {
		super(source);
	}
	
	@Override
	public EDIMessage read() throws EDISyntaxException, IOException {
		return EDIParser.newInstance(standard).parse(lexer);
	}
	
}
