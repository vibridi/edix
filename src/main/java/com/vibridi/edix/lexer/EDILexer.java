package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;


public abstract class EDILexer {
	
	private static final Map<EDIStandard, Class<? extends EDILexer>> lexers;
	
    static {
    	lexers = new HashMap<>();
        lexers.put(EDIStandard.ANSI_X12, AnsiLexer.class); // TODO
//        registered.put("UNA", EdifactReaderWithCONTRL.class);
//        registered.put("UNB", EdifactReaderWithCONTRL.class);
//        registered.put("UNH", UNHReader.class);
    }
    
    /**
     * Registers a custom lexer.
     *
     * @param standard Data standard associated to this lexer
     * @param clazz Fully qualified class name of an EDILexer subclass
     */
    public static void register(EDIStandard standard, Class<? extends EDILexer> clazz) {
    	synchronized(lexers) {
    		lexers.put(standard, clazz);
    	}
    }
    
    /**
     * Returns an instance of some EDILexer subclass based on the message standard.
     * <p>
     * Lexers for ANSI X12 and UN/EDIFACT are built-in. Other lexers can be registered with the 
     * {@code registerLexer()} method.
     *
     * @param target data standard
     * @return Subclass of EDILexer that knows how to tokenize the data
     * @throws RuntimeException if the object can't be instantiated
     */
    public static EDILexer newInstance(EDIStandard standard, PushbackReader source) {
    	Class<? extends EDILexer> clazz = lexers.get(standard);
    	
		try {
			return (EDILexer) clazz.getConstructor(PushbackReader.class).newInstance(source);
		} catch(NullPointerException e) {
			throw new RuntimeException("No lexer class specified for standard: " + standard.name());
		} catch (Throwable e) {
			throw new RuntimeException("Cannot instantiate EDI lexer for class: " + clazz.getName());
		}
    }
	
    protected PushbackReader source;
    
    private TokenType[] controlCharacters = new TokenType[256];
    
	public EDILexer(PushbackReader source) throws IOException {
		Arrays.fill(controlCharacters, TokenType.E);
		this.source = source;
		findControlCharacters(controlCharacters);
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
