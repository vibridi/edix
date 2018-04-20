package com.vibridi.edix.loop.impl;

import java.util.List;
import java.util.Optional;

import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.model.EDICompositeNode;

public class EDILoopLeaf extends EDILoopNode {

	private EDICompositeNode segment;

	public EDILoopLeaf(EDICompositeNode segment, EDILoopNode parent) {
		super(segment.getName(), parent);
		this.segment = segment;
	}
	
	@Override
	public String toString() {
		return segment.getName();
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.ofNullable(null);
	}

	@Override
	public Optional<String> getStartingSegment() {
		return Optional.of(segment.getName());
	}

	@Override
	public boolean allowsSegment(String segmentTag) {
		return segment.getName().equals(segmentTag);
	}

	@Override
	public boolean allowsLoop(String segmentTag) {
		return false;
	}

	@Override
	public List<EDILoop> getChildren() {
		return null;
	}

	@Override
	public void appendSegment(EDICompositeNode segment) {
		throw new UnsupportedOperationException("Can't add children to a leaf node");
	}

	@Override
	public EDILoop appendLoop(EDICompositeNode segment) {
		throw new UnsupportedOperationException("Can't add children to a leaf node");
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public EDICompositeNode getSegmentContent() {
		return segment;
	}

	@Override
	public EDILoop appendHL(EDICompositeNode segment) {
		throw new UnsupportedOperationException("Can't add children to a leaf node");
	}

}
