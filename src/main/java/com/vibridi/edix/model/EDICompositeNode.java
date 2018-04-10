package com.vibridi.edix.model;

public interface EDICompositeNode extends EDINode {
	public EDINode appendChild(EDINode newChild);
	public String getDelimiter();
	public void setDelimiter(String delimiter);
	public String getRepetitionSeparator();
	public int fields();
	public void setRepetitionSeparator(String repetitionSeparator);
	public void setRepeated(boolean repeated);
	public boolean isRepeated();
	public void appendRepetition(EDINode repetition);

}
