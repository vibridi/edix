package com.vibridi.edix.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDINode;

public class EDICompositeNodeImpl extends EDISimpleTextNode implements EDICompositeNode {

	private List<EDINode> children;
	private List<EDINode> repetitions;
	private String delimiter;
	private String repetitionSeparator;
	
	protected EDICompositeNodeImpl(EDINode parent) {
		super(parent, "");
		setName("");
		children = new ArrayList<>();
		repetitions = new ArrayList<>();
		delimiter = "";
		repetitionSeparator = "";
	}
	
	@Override
	public String toString() {	
		StringJoiner sj = new StringJoiner(delimiter, "", "");
		
		if(!getName().isEmpty())
			sj.add(getName());
		
		for(EDINode n : getChildren())
			sj.add(n.toString());
		
		if(isRepeated()) {
			String r = sj.toString();
			sj = new StringJoiner(repetitionSeparator, "", "");
			sj.add(r);
			for(EDINode n : repetitions)
				sj.add(n.toString());
			return sj.toString();
		}
		
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
	public boolean isRepeated() {
		return repetitions.size() > 0;
	}

	@Override
	public void appendRepetition(EDINode repetition) {
		if(repetitionSeparator.isEmpty())
			throw new IllegalStateException("Adding a repetition but repetition separator is unspecified.");
		repetitions.add(repetition);
	}
	
}
