package com.vibridi.edix.model.impl;

import java.util.List;

import com.vibridi.edix.model.EDINode;

public class EDISimpleTextNode extends EDINodeImpl implements EDINode {

	private String text;
	
	protected EDISimpleTextNode(EDINode parent, String text) {
		super(parent);
		this.text = text;
	}
	
	@Override
	public String toString() {
		return getTextContent();
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
	
	@Override
	public List<EDINode> getChildren() {
		return null;
	}
	
	@Override
	public EDINode getChild(int index) {
		return null;
	}
	
	@Override
	public EDINode getFirstChild() {
		return null;
	}
	
	@Override
	public EDINode getLastChild() {
		return null;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

}
