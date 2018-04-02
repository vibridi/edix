package com.vibridi.edix.lexer;

public enum TokenType {
	E(0),									// Null or empty token
	WORD(1),
	DELIMITER(2),							// Character marking the boundary between fields
	SUB_DELIMITER(3),						// Character marking the boundary between sub-fields
	SUB_SUB_DELIMITER(4),					// Character marking the boundary between sub-sub-fields
	DECIMAL_MARKER(5),						// Character used as a decimal point (typically "." or ",")
	REPETITION_SEPARATOR(6),				// Character marking the boundary between repeating fields
	TERMINATOR(7),							// Character marking the boundary between segments
	RELEASE(8),								// The byte value used as a release or escape character.
	TERMINATOR_SUFFIX(9);
	
	protected int num;
	
	private TokenType(int num) {
		this.num = num;
	}
	
	public int numValue() {
		return num;
	}
}