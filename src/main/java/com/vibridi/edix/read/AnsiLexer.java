package com.vibridi.edix.read;

import java.io.IOException;
import java.io.PushbackReader;
import java.nio.CharBuffer;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;
import com.vibridi.util.StringUtils;

public class AnsiLexer extends EDILexer {
	
	public static final int ISA_LENGTH = 106;
	
	public static final int DELIMITER_POS = 3;			// 0-based
	public static final int SUBDELIMITER_POS = 104;		// 0-based
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
	public void prepare() throws EDISyntaxException, IOException {
		PushbackReader pbr = new PushbackReader(source);

		//        if (isPreviewed())
		//            throw new EDISyntaxException(INTERNAL_ERROR_MULTIPLE_EOFS);

		// No release character is supported for ANSI X.12
		setRelease(-1);

		char[] buf = new char[ISA_LENGTH];
		if(pbr.read(buf) < ISA_LENGTH)
			throw new EDISyntaxException(ErrorMessages.INCOMPLETE_X12);
		pbr.unread(buf);

		if (!"ISA".equals(String.copyValueOf(buf, 0, 3)))
			throw new EDISyntaxException(ErrorMessages.X12_MISSING_ISA);

		// ISA*
		// ...^ (offset 3)
		setDelimiter(buf[DELIMITER_POS]);

//		int indexOf16thFieldSeparator = StringUtils.nthIndexOf(buf, getDelimiter(), 16);
//		if (indexOf16thFieldSeparator < 0)
//			throw new EDISyntaxException(ErrorMessages.ISA_SEGMENT_HAS_TOO_FEW_FIELDS);

		char c = buf[SUBDELIMITER_POS];
		if(c != getDelimiter())
			setSubDelimiter(c);

		c = buf[TERMINATOR_POS];
		if(c != getDelimiter())
			setTerminator(c);
		else
			throw new EDISyntaxException(ErrorMessages.INVALID_SEGMENT_TERMINATOR);

		//setTerminatorSuffix(findTerminatorSuffix(buf, TERMINATOR_POS + 3, 128));

		// Determine the repetition character. This changed in 4.6.5 to support repetition chars
		// introduced in version 4020 of the ANSI X12 standard.
		int indexOf11thFieldSeparator = StringUtils.nthIndexOf(buf, getDelimiter(), 11);
		char repetitionChar = buf[indexOf11thFieldSeparator + 1];
		
		if (Character.isLetterOrDigit(repetitionChar)
			||	repetitionChar == getTerminator()
			||	repetitionChar == getDelimiter()
			||	Character.isWhitespace(repetitionChar)) {
			// This is not a suitable repetition character.
			// It may be desirable to further check to see if the version number
			// in the next ISA element indicates 4020 or later; if not, then
			// repetition characters were not used.
			setRepetitionSeparator('\000');
		} else {
			setRepetitionSeparator(repetitionChar);
		}

		//setFirstSegment(new String(buf, 0, indexOf16thFieldSeparator + 3)); // TODO see if needed
		//setPreviewed(true);

	}

	@Override
	public TokenStream getTokenStream() throws IOException {
		
		CharBuffer buf = CharBuffer.wrap(new char[1024]);
		while(source.read(buf) != -1) {
			
			while(buf.hasRemaining()) {
				
				
				
				
			}
			
			
			
			
		}
		
		
		//char c = (char) source.read();
		
		/*
		 *         if (endOfFile) {
            cClass = CharacterClass.EOF;
            if (EDIReader.debug)
                trace("end-of-file encountered");
        } else {
            cChar = charBuffer.get();
            if (cChar == delimiter)
                cClass = CharacterClass.DELIMITER;
            else if (cChar == subDelimiter)
                cClass = CharacterClass.SUB_DELIMITER;
            else if (cChar == release)
                cClass = CharacterClass.RELEASE;
            else if (cChar == terminator)
                cClass = CharacterClass.TERMINATOR;
            else if (cChar == repetitionSeparator)
                cClass = CharacterClass.REPEAT_DELIMITER;
            else
                cClass = CharacterClass.DATA;
        }
		 * 
		 * 
		 */
		
		while(source.ready()) {
			
			
			
			
		}
		
		
		
		return null;
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
