package com.vibridi.edix.lexer;

import java.util.Objects;

public class Token implements Cloneable {

	public TokenType type;
	public String value;
	
	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "[" + type.name() + ": " + value + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Token) {
			Token that = (Token) obj;
			return this.type == that.type && this.value.equals(that.value);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}
	
	@Override
	public Token clone() {
		return new Token(type, value);
	}
	
}
