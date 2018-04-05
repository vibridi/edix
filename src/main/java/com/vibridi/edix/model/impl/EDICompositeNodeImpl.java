package com.vibridi.edix.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDINode;

public class EDICompositeNodeImpl extends EDITextNodeImpl implements EDICompositeNode {

	private List<EDINode> children;
	
	protected EDICompositeNodeImpl(EDINode parent) {
		super(parent, "");
		setName("");
		children = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return "[EDICompositeNode\n"
				+ "Name: " + getName() + "\n"
				+ "Fields: " + getChildren().size() + "\n"
				+ getChildren().toString() + "\n"
				+ "]";
	}

	@Override
	public EDINode getFirstChild() {
		return children.isEmpty() ? null : children.get(0);
	}


	@Override
	public EDINode getLastChild() {
		return children.isEmpty() ? null : children.get(children.size() - 1);
	}

	@Override
	public EDINodeType getNodeType() {
		return EDINodeType.COMPOSITE_NODE;
	}

	@Override
	public List<EDINode> getChildren() {
		return children;
	}


	@Override
	public EDINode getChild(int index) {
		return children.get(index);
	}

	@Override
	public boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	public EDINode appendChild(EDINode newChild) {
		if(newChild.getParent() == null || newChild.getParent() != this)
			throw new IllegalArgumentException(); // TODO make own exception
		children.add(newChild);
		return newChild;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public int fields() {
		return children.size();
	}
	
}
