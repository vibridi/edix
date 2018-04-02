package com.vibridi.edix.read;

import java.io.IOException;
import java.io.Reader;

import com.vibridi.edix.error.EDISyntaxException;


public abstract class EDILexer {
	
    protected enum CharacterType {
        DATA, DELIMITER, SUB_DELIMITER, RELEASE, TERMINATOR, REPEAT_DELIMITER, EOF
    }
	
    public enum TokenType {
    	DATA,
    	DELIMITER,
    	SUB_DELIMITER,
    	TERMINATOR,
    	TRAILER,
    	EOF
    	
       // UNKNOWN, SEGMENT_START, SIMPLE, EMPTY, SUB_ELEMENT, SUB_EMPTY, SEGMENT_END, END_OF_DATA
    }

    private char delimiter;					// Character marking the boundary between fields
    private char subDelimiter;				// Character marking the boundary between sub-fields
    private char subSubDelimiter;			// Character marking the boundary between sub-sub-fields
    private char decimalMark;				// Character used as a decimal point ("." or ",")
    private char repetitionSeparator;		// Character marking the boundary between repeating fields
    private char terminator;				// Character marking the boundary between segments
    private int release;					// The byte value used as a release or escape character.
    private String terminatorSuffix;		// Whitespace characters observed to follow the formal segment terminator.
	
	protected Reader source;	// TODO consider making PushbackReader
	
	
	public EDILexer() {
	}
	
	public void setSource(Reader source) {
		this.source = source;
	}
	
	public abstract void prepare() throws EDISyntaxException, IOException;
	public abstract TokenStream getTokenStream() throws IOException;

	
	
	/* =========================
	  	GETTERS AND SETTERS
	   ========================= */
	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public char getSubDelimiter() {
		return subDelimiter;
	}

	public void setSubDelimiter(char subDelimiter) {
		this.subDelimiter = subDelimiter;
	}

	public char getSubSubDelimiter() {
		return subSubDelimiter;
	}

	public void setSubSubDelimiter(char subSubDelimiter) {
		this.subSubDelimiter = subSubDelimiter;
	}

	public char getDecimalMark() {
		return decimalMark;
	}

	public void setDecimalMark(char decimalMark) {
		this.decimalMark = decimalMark;
	}

	public char getRepetitionSeparator() {
		return repetitionSeparator;
	}

	public void setRepetitionSeparator(char repetitionSeparator) {
		this.repetitionSeparator = repetitionSeparator;
	}

	public char getTerminator() {
		return terminator;
	}

	public void setTerminator(char terminator) {
		this.terminator = terminator;
	}

	public int getRelease() {
		return release;
	}

	public void setRelease(int release) {
		this.release = release;
	}

	public String getTerminatorSuffix() {
		return terminatorSuffix;
	}

	public void setTerminatorSuffix(String terminatorSuffix) {
		this.terminatorSuffix = terminatorSuffix;
	}
	
	
	
}
