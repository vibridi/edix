package com.vibridi.edix.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDINode;

public class EDICompositeNodeImpl extends EDISimpleTextNode implements EDICompositeNode {

	private List<EDINode> children;
	private String delimiter;
	private String repetitionSeparator;
	private boolean repeated;
	
	protected EDICompositeNodeImpl(EDINode parent) {
		super(parent, "");
		setName("");
		children = new ArrayList<>();
		delimiter = "";
		repetitionSeparator = "";
		repeated = false;
	}
	
	@Override
	public String toString() {	
		StringJoiner sj = new StringJoiner(isRepeated() ? repetitionSeparator : delimiter, "", "");
		
		if(!getName().isEmpty())
			sj.add(getName());

		for(EDINode n : getChildren())
			sj.add(n.toString());
		
		return sj.toString();
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

	@Override
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public String getRepetitionSeparator() {
		return repetitionSeparator;
	}

	@Override
	public void setRepetitionSeparator(String repetitionSeparator) {
		Objects.requireNonNull(repetitionSeparator);
		this.repetitionSeparator = repetitionSeparator;
	}
	
	@Override
	public void setRepeated(boolean repeated) {
		if(!isRepeated())
			mergeChildren();
		this.repeated = repeated;
	}

	@Override
	public boolean isRepeated() {
		return repeated;
	}

	@Override
	public void appendRepetition(EDINode repetition) {
		if(repetitionSeparator.isEmpty())
			throw new IllegalStateException("Adding a repetition but repetition separator is unspecified.");
		children.add(repetition);
	}
	
	private void mergeChildren() {
		if(children.size() <= 1)
			return;
		
		EDICompositeNode merged = EDIMessageFactory.newCompositeNode(this);
		merged.setDelimiter(this.delimiter);
		for(EDINode n : children) {
			merged.getChildren().add(n);
		}
		
		children.clear();
		children.add(merged);
	}
	
}
