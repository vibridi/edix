package com.vibridi.edix.lexer;

public enum TokenType {
	E,									// Null or empty token
	WORD,
	DELIMITER,							// Character marking the boundary between fields
	SUB_DELIMITER,						// Character marking the boundary between sub-fields
	SUB_SUB_DELIMITER,					// Character marking the boundary between sub-sub-fields
	DECIMAL_MARKER,						// Character used as a decimal point (typically "." or ",")
	REPETITION_SEPARATOR,				// Character marking the boundary between repeating fields
	TERMINATOR,							// Character marking the boundary between segments
	RELEASE								// The byte value used as a release or escape character.
}