package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDILexerException;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;

public class AnsiLexer extends EDILexer {
	
	public static final int ISA_LENGTH = 106;
	
	// Positions are 0-indexed
	public static final int DELIMITER_POS = 3;
	public static final int REPETITION_SEPARATOR_POS = 82;
	public static final int SUB_DELIMITER_POS = 104;
	public static final int TERMINATOR_POS = 105;

	@Override
	public EDIStandard getStandard() {
		return EDIStandard.ANSI_X12;
	}
	
	/**
	 * Preview the ANSI X.12 input before attempting to tokenize it in order to
	 * discover syntactic details including segment terminator and field
	 * delimiter. Upon return, the input stream has been re-positioned so that
	 * the tokenizer can read from the beginning of the interchange.
	 *
	 * @throws EDISyntaxException if invalid EDI is detected
	 * @throws IOException        for problem reading EDI data
	 */
	@Override
	public void findControlCharacters(TokenType[] controlCharacters) throws EDISyntaxException, IOException {

		// No release character is supported for ANSI X.12

		char[] buf = new char[ISA_LENGTH];
		if(source.read(buf) < ISA_LENGTH)
			throw new EDISyntaxException(ErrorMessages.INCOMPLETE_X12.toString());
		source.unread(buf);

		if (!"ISA".equals(String.copyValueOf(buf, 0, 3)))
			throw new EDISyntaxException(ErrorMessages.X12_MISSING_ISA.toString());

		// ISA*
		// ...^ (offset 3)
		char d = buf[DELIMITER_POS];
		controlCharacters[d] = TokenType.DELIMITER;

		char c = buf[SUB_DELIMITER_POS];
		if(c != d)
			controlCharacters[c] = TokenType.SUB_DELIMITER;

		char t = buf[TERMINATOR_POS];
		if(c != d)
			controlCharacters[t] = TokenType.TERMINATOR;
		else
			throw new EDISyntaxException(ErrorMessages.INVALID_SEGMENT_TERMINATOR.withArgs(""+c).toString());

		
		c = buf[REPETITION_SEPARATOR_POS];
		String x12version = String.valueOf(buf, 84, 5);
		
		// Repetition character wasn't used before version 4020 of the ANSI X12 standard
		if(x12version.compareTo("00402") >= 0) {
			
			// It also can't be a letter, digit, whitespace and must differ from other control chars
			if (!Character.isLetterOrDigit(c)
					&&	!Character.isWhitespace(c)
					&&	c != t
					&&	c != d) {
				
				controlCharacters[c] = TokenType.REPETITION_SEPARATOR;
			}
		}
	}

	@Override
	public TokenStream tokenize() throws IOException {
		StreamTokenizer st = new StreamTokenizer(source);
		initSyntax(st);
		
		TokenStream tks = new TokenStream();
		
		while(st.nextToken() != StreamTokenizer.TT_EOF) {
			
			if(st.ttype == StreamTokenizer.TT_WORD) {
				tks.add(new Token(TokenType.WORD, st.sval));
				
				if(st.sval.equals("BIN") && tks.get(tks.size() - 2).type == TokenType.TERMINATOR)
					tokenizeBIN(st, tks);
				
			} else if(getControlCharacters()[st.ttype] == TokenType.TERMINATOR) {
				tks.add(new Token(getControlCharacters()[st.ttype], Character.toString((char)st.ttype)));
				// Skip trailer characters. 
				// After a terminator, we can resume tokenizing as soon as we encounter an alphabetic char
				int i = -1;
				do {
					i = source.read();
				} while(i != -1 && !Character.isAlphabetic(i)); // While not EOF and char isn't alphabetic
				
				if(i != -1)
					source.unread(i);
				
			} else {
				tks.add(new Token(getControlCharacters()[st.ttype], Character.toString((char)st.ttype)));
			}
		}
		
		return tks;
	}

	protected static String findTerminatorSuffix(char[] buf, int i, int j) {
		// AAAAA<------       M
		//      ^ i          ^ j
		// between the line terminator and the beginning of the next segment there could be any number
		// of non-letter characters

		int n = i;
		do {
			n++;
		} while(n < j && !Character.isLetter(buf[n]));

		return String.copyValueOf(buf, i, n);
	}

	private void initSyntax(StreamTokenizer st) {
		st.resetSyntax();
		st.wordChars(0, 255);
		
		TokenType[] cc = getControlCharacters();
		for(int i = 0; i < cc.length; i++) {
			if(cc[i] != TokenType.E)
				st.ordinaryChar((char)i);
		}	
	}
	
	private void tokenizeBIN(StreamTokenizer st, TokenStream tks) throws IOException {
		// BIN and BIN01 Delimiter
		st.nextToken();
		tks.add(new Token(getControlCharacters()[st.ttype], Character.toString((char)st.ttype)));
		
		// BIN01
		st.nextToken();
		tks.add(new Token(TokenType.WORD, st.sval));
		int binLength = Integer.parseUnsignedInt(st.sval);
		
		// BIN01 and BIN02 Delimiter
		st.nextToken();
		tks.add(new Token(getControlCharacters()[st.ttype], Character.toString((char)st.ttype)));
		
		// BIN02
		st.resetSyntax();
		st.wordChars(0, 255);
		TokenType[] cc = getControlCharacters();
		for(int i = 0; i < cc.length; i++) {
			if(cc[i] == TokenType.TERMINATOR)
				st.ordinaryChar((char)i);
		}	
		
		st.nextToken();
		if(st.sval.getBytes().length != binLength)
			throw new EDILexerException(String.format("BIN01 [%d] doesn't equal BIN02 length [%d]", binLength, st.sval.getBytes().length));
		tks.add(new Token(TokenType.WORD, st.sval));
		
		initSyntax(st);
	}

}
