package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;


public abstract class EDILexer {
	
    protected PushbackReader source;
    
    private TokenType[] controlCharacters = new TokenType[256];
    
	public EDILexer() {
		Arrays.fill(controlCharacters, TokenType.E);
	}
	
	/* 	=========================
  				MEMBERS
   		========================= 	*/
	public EDILexer setSource(PushbackReader source) throws EDISyntaxException, IOException {
		this.source = source;
		findControlCharacters(controlCharacters);
		return this; // Chainable
	}
	
	protected boolean isControlCharacter(char c) {
		return (c < controlCharacters.length) && (controlCharacters[(int)c] != TokenType.E);
	}
	
	/* 	=========================
			ABSTRACT MEMBERS
		========================= 	*/
	public abstract TokenStream tokenize() throws IOException;
	public abstract EDIStandard getStandard();
	
	protected abstract void findControlCharacters(TokenType[] controlCharacters) throws EDISyntaxException, IOException;


	
	/* 	=========================
	  		GETTERS AND SETTERS
	   	========================= 	*/	
	public TokenType[] getControlCharacters() {
		return Arrays.copyOf(controlCharacters, controlCharacters.length);
	}
	
}
