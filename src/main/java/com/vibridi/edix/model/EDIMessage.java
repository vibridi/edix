package com.vibridi.edix.model;

import com.vibridi.edix.lexer.TokenType;

public interface EDIMessage extends EDICompositeNode {
	public void addSegment(String name, EDICompositeNode node);
	public EDICompositeNode getSegment(String name);
	public TokenType[] getControlCharacters();
	public void setControlCharacters(TokenType[] controlCharacters);
	public String getTextAt(String path);
}
