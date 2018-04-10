package com.vibridi.edix.error;

import java.io.IOException;

public class EDILexerException extends IOException {
	private static final long serialVersionUID = 1L;

	public EDILexerException(String desc) {
		super(desc);
	}
}
