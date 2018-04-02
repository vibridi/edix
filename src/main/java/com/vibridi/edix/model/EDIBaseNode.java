package com.vibridi.edix.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class EDIBaseNode implements EDINode {

	private EDINode parent;
	private List<EDINode> children;
	
	public EDIBaseNode(EDINode parent) {
		this.parent = parent;
		this.children = new ArrayList<>();
	}

	@Override
	public Optional<EDINode> getParent() {
		return Optional.ofNullable(parent);
	}

	@Override
	public List<EDINode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public EDINode getChild(int index) {
		return children.get(index);
	}

}
