package com.vibridi.edix.model.impl;

import com.vibridi.edix.model.EDINode;

public abstract class EDINodeImpl implements EDINode {

	private EDINode parent;
	private String name;
	
	protected EDINodeImpl(EDINode parent) {
		this.parent = parent;
		this.name = "";
	}
	
	public EDINodeImpl(EDINodeImpl that, EDINode parent) {
		this.parent = parent;
		this.name = that.name;
	}
	
	protected void checkOwnership(EDINode node, EDINode parent) {
		if(node.getParent() == null || node.getParent() != parent)
			throw new IllegalArgumentException("Node owner and parent don't match");
	}

	@Override
	public EDINode getParent() {
		return parent;
	}
	
	@Override
	public EDINode getOwnerInterchange() {
		EDINode tmp = parent;
		while(tmp != null)
			tmp = tmp.getParent();
		return tmp;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isEndOfComposite() {
		return parent == null ? false : (this == parent.getLastChild());
	}
		
}
