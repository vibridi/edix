package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;

import com.vibridi.edix.error.EDISyntaxException;


public abstract class EDILexer {
	
    protected PushbackReader source;
    
    private TokenType[] controlCharacters = new TokenType[256];
    private char[] controlTokens = new char[TokenType.values().length];
    
	public EDILexer() {
		Arrays.fill(controlCharacters, TokenType.E);
		Arrays.fill(controlTokens, '\0');
	}
	
	/* 	=========================
  				MEMBERS
   		========================= 	*/
	public EDILexer setSource(PushbackReader source) throws EDISyntaxException, IOException {
		this.source = source;
		findControlCharacters(controlCharacters);
		
		for(int i = 0; i < controlCharacters.length; i++) {
			controlTokens[controlCharacters[i].num] = (char)i;
		}
		return this; // Chainable
	}
	
	protected boolean isControlCharacter(char c) {
		return (c < controlCharacters.length) && (controlCharacters[(int)c] != TokenType.E);
	}
	
	/* 	=========================
			ABSTRACT MEMBERS
		========================= 	*/
	public abstract TokenStream tokenize() throws IOException;
	protected abstract void findControlCharacters(TokenType[] controlCharacters) throws EDISyntaxException, IOException;


	
	/* 	=========================
	  		GETTERS AND SETTERS
	   	========================= 	*/	
	public char getControlCharacter(TokenType type) {
		return controlTokens[type.num];
	}

	public TokenType[] getControlCharacters() {
		return Arrays.copyOf(controlCharacters, controlCharacters.length);
	}
	
}
