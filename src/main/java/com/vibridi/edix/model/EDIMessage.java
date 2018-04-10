package com.vibridi.edix.model;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.path.EDIPath;

public interface EDIMessage extends EDICompositeNode {
	public void addSegment(String name, EDICompositeNode node);
	public EDICompositeNode getSegment(String name, int i);
	public TokenType[] getControlCharacters();
	public void setControlCharacters(TokenType[] controlCharacters);
	public String getTextAt(EDIPath path);
	public void setTextAt(EDIPath path, String text);
	public EDINode getNodeAt(EDIPath path);
	public void setStandard(EDIStandard standard);
	public EDIStandard getStandard();
}
