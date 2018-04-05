package com.vibridi.edix.parser;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.model.EDIMessage;

public abstract class EDIParser {
	
	protected boolean strict;
	
	public EDIParser() {
		strict = true;
	}
	
	public abstract EDIMessage parse(TokenStream tokens) throws EDISyntaxException;
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
}
