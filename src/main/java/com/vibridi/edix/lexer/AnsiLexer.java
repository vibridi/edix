package com.vibridi.edix.lexer;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;
import com.vibridi.edix.util.StringUtils;

public class AnsiLexer extends EDILexer {
	
	public static final int ISA_LENGTH = 106;
	
	public static final int DELIMITER_POS = 3;			// 0-based
	public static final int SUB_DELIMITER_POS = 104;		// 0-based
	public static final int TERMINATOR_POS = 105;		// 0-based

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
			throw new EDISyntaxException(ErrorMessages.INCOMPLETE_X12);
		source.unread(buf);

		if (!"ISA".equals(String.copyValueOf(buf, 0, 3)))
			throw new EDISyntaxException(ErrorMessages.X12_MISSING_ISA);

		// ISA*
		// ...^ (offset 3)
		char d = buf[DELIMITER_POS];
		controlCharacters[d] = TokenType.DELIMITER;
		
//		int indexOf16thFieldSeparator = StringUtils.nthIndexOf(buf, getDelimiter(), 16);
//		if (indexOf16thFieldSeparator < 0)
//			throw new EDISyntaxException(ErrorMessages.ISA_SEGMENT_HAS_TOO_FEW_FIELDS);

		char c = buf[SUB_DELIMITER_POS];
		if(c != d)
			controlCharacters[c] = TokenType.SUB_DELIMITER;

		char t = buf[TERMINATOR_POS];
		if(c != d)
			controlCharacters[t] = TokenType.TERMINATOR;
		else
			throw new EDISyntaxException(ErrorMessages.INVALID_SEGMENT_TERMINATOR);

		//setTerminatorSuffix(findTerminatorSuffix(buf, TERMINATOR_POS + 3, 128));

		// Determine the repetition character. This changed in 4.6.5 to support repetition chars
		// introduced in version 4020 of the ANSI X12 standard.
		int indexOf11thFieldSeparator = StringUtils.nthIndexOf(buf, d, 11);
		char repetitionChar = buf[indexOf11thFieldSeparator + 1];
		
		if (Character.isLetterOrDigit(repetitionChar)
			||	repetitionChar == t
			||	repetitionChar == d
			||	Character.isWhitespace(repetitionChar)) {
			// This is not a suitable repetition character.
			// It may be desirable to further check to see if the version number
			// in the next ISA element indicates 4020 or later; if not, then
			// repetition characters were not used.
			
		} else {
			controlCharacters[repetitionChar] = TokenType.REPETITION_SEPARATOR;
		}

		//setFirstSegment(new String(buf, 0, indexOf16thFieldSeparator + 3)); // TODO see if needed
		//setPreviewed(true);

	}

	@Override
	public TokenStream tokenize() throws IOException {
		StreamTokenizer st = new StreamTokenizer(source);
		st.resetSyntax();
		st.wordChars(0, 255);
		
		TokenType[] cc = getControlCharacters();
		for(int i = 0; i < cc.length; i++) {
			if(cc[i] != TokenType.E)
				st.ordinaryChar((char)i);
		}	
		
		TokenStream tks = new TokenStream();
		
		while(st.nextToken() != StreamTokenizer.TT_EOF) {
			
			if(st.ttype == StreamTokenizer.TT_WORD) {
				tks.add(new Token(TokenType.WORD, st.sval));
				
			} else if(st.ttype == getControlCharacter(TokenType.TERMINATOR)) {
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

}
