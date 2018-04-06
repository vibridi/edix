package com.vibridi.edix.model;

public interface EDICompositeNode extends EDINode {
	public EDINode appendChild(EDINode newChild);
	public void setDelimiter(String delimiter);
	public String getDelimiter();
	public int fields();
}
