package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;

import com.vibridi.edix.error.EDISyntaxException;


public abstract class EDILexer {
	
	
	public static EDILexer instanceOf(Class<? extends EDILexer> clazz) {
		try {
			return (EDILexer) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot instantiate EDI lexer for class: " + clazz.getName());
		}
	}
	
    protected PushbackReader source;
    				
    private String terminatorSuffix;		// Whitespace characters observed to follow the formal segment terminator.
    private TokenType[] controlCharacters = new TokenType[256];
    private char[] controlTokens = new char[TokenType.values().length];
    
	public EDILexer() {
		Arrays.fill(controlCharacters, TokenType.E);
		Arrays.fill(controlTokens, '\0');
	}
	
	/* 	=========================
  				MEMBERS
   		========================= 	*/
	public void setSource(PushbackReader source) {
		this.source = source;
	}
	
	protected boolean isControlCharacter(char c) {
		return (c < controlCharacters.length) && (controlCharacters[(int)c] != TokenType.E);
	}
	
	protected void prepare() throws EDISyntaxException, IOException {
		findControlCharacters(controlCharacters);
		
		for(int i = 0; i < controlCharacters.length; i++) {
			controlTokens[controlCharacters[i].num] = (char)i;
		}
			
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
	
	public String getTerminatorSuffix() {
		return terminatorSuffix;
	}

	public void setTerminatorSuffix(String terminatorSuffix) {
		this.terminatorSuffix = terminatorSuffix;
	}

	public TokenType[] getControlCharacters() {
		return Arrays.copyOf(controlCharacters, controlCharacters.length);
	}
	
}
