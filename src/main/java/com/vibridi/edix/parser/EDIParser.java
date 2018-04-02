package com.vibridi.edix.parser;

import java.io.IOException;
import java.io.InputStream;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDIMessage;

public abstract class EDIParser {

	public EDIParser() {
		// TODO Auto-generated constructor stub
	}


	public abstract EDIMessage parse(InputStream source) throws EDISyntaxException, IOException;
	
	
}
