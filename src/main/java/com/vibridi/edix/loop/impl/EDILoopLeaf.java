package com.vibridi.edix.loop.impl;

import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.model.EDICompositeNode;

public class EDILoopLeaf extends EDILoopNode {

	public EDILoopLeaf(EDICompositeNode headSegment, EDILoopNode parent) {
		super(headSegment, parent);
	}

	@Override
	public void addSegment(EDICompositeNode segment) {
		throw new UnsupportedOperationException("Attempting to add a segment to a loop leaf.");
	}
	
	@Override
	public void addChild(EDILoop loop) {
		throw new UnsupportedOperationException("Attempting to add a child loop to a loop leaf.");
	}
	
	@Override
	public EDILoop newChild(LoopDescriptor descriptor, EDICompositeNode segment) {
		throw new UnsupportedOperationException("Attempting to create a child loop on a loop leaf.");
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
}
