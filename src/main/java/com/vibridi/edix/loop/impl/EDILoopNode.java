package com.vibridi.edix.loop.impl;

import java.util.ArrayList;
import java.util.List;

import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.model.EDICompositeNode;

public class EDILoopNode implements EDILoop {
	
	private LoopDescriptor data;
	private EDILoop parent;
	private List<EDILoop> children;
	private EDICompositeNode segment;
	
	public EDILoopNode(EDICompositeNode headSegment, EDILoopNode parent) {
		this(new LoopDescriptor("", 0, ""), headSegment, parent);
	}
	
	public EDILoopNode(LoopDescriptor data, EDICompositeNode headSegment, EDILoopNode parent) {
		this.data = data;
		this.segment = headSegment;
		this.parent = parent;
		this.children = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		if(parent == null)
			return "/";
		
		return parent.toString() + getName() + "/";
	}
	
	@Override
	public boolean isRoot() {
		return segment == null;
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	@Override
	public EDILoop getParent() {
		return parent;
	}
	
	@Override
	public EDILoop getAncestor(int upToNestingLevel) {
		EDILoop ancestor = this;
		for(int j = nestingLevel(); j >= upToNestingLevel; j--) {
			ancestor = ancestor.getParent();
		}
		return ancestor;
	}
	
	@Override
	public EDICompositeNode getSegment() {
		return segment;
	}
	
	@Override
	public void addSegment(EDICompositeNode segment) {
		addChild(new EDILoopLeaf(segment, this));
	}
	
	@Override
	public void addChild(EDILoop loop) {
		children.add(loop);
	}
	
	@Override
	public EDILoop newChild(LoopDescriptor descriptor, EDICompositeNode segment) {
		EDILoop child = new EDILoopNode(descriptor, segment, this);
		addChild(child);
		return child;
	}
	
	@Override
	public String getName() {
		return data.name;
	}

	@Override
	public int nestingLevel() {
		if(parent == null)
			return 0;	
		return parent.nestingLevel() + 1;
	}
	
	@Override
	public List<EDILoop> getChildren() {
		return new ArrayList<>(children);
	}
	
}
