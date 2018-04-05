package com.vibridi.edix.model.impl;

import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.EDITextNode;

public class EDITextNodeImpl extends EDINodeImpl implements EDITextNode {

	private String text;
	
	protected EDITextNodeImpl(EDINode parent, String text) {
		super(parent);
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "[Text: " + getTextContent() + "]";
	}

	@Override
	public EDINodeType getNodeType() {
		return EDINodeType.TEXT_NODE;
	}

	@Override
	public String getTextContent() {
		return text;
	}

	@Override
	public void setTextContent(String text) {
		this.text = text;
	}
	
	@Override
	public boolean isEmpty() {
		return text == null ? true : text.isEmpty();
	}

}
