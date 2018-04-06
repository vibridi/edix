package com.vibridi.edix.model;

public interface EDICompositeNode extends EDINode {
	public EDINode appendChild(EDINode newChild);
	public String getDelimiter();
	public void setDelimiter(String delimiter);
	public String getRepetitionSeparator();
	public void setRepetitionSeparator(String repetitionSeparator);
	public boolean isRepeated();
	public int fields();
	public void appendRepetition(EDINode repetition);
}
