package com.vibridi.edix.loop.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.model.EDICompositeNode;

public class EDILoopContainer extends EDILoopNode {

	private LoopDescriptor descriptor;
	private List<EDILoop> children;	
	
	public EDILoopContainer(LoopDescriptor descriptor, EDILoop parent) {
		super(descriptor.getName(), parent);
		this.descriptor = descriptor;
		this.children = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "[", "]");
		children.forEach(e -> sj.add(e.toString()));
		return sj.toString();
	}
	
	@Override
	public boolean allowsSegment(String segmentTag) {
		return descriptor.getAllowedSegments().contains(segmentTag);
	}
	
	@Override
	public boolean allowsLoop(String segmentTag) {
		return descriptor.getAllowedLoops().keySet().contains(segmentTag);
	}

	@Override
	public void appendSegment(EDICompositeNode segment) {
		if(!allowsSegment(segment.getName()) && !getStartingSegment().orElse("").equals(segment.getName()))
			throw new IllegalArgumentException("This loop doesn't allow the segment: " + segment.getName());
		appendChild(new EDILoopLeaf(segment, this));
	}
	
	@Override
	public EDILoop appendLoop(EDICompositeNode segment) {
		if(!allowsLoop(segment.getName()))
			throw new IllegalArgumentException(String.format(
					"This loop %s doesn't allow the loop %s",
					this.toString(),
					segment.getName()));
		
		LoopDescriptor d = descriptor.getDescriptor(segment.getName());
		EDILoopContainer loop = new EDILoopContainer(d, this);
		loop.appendSegment(segment);
		appendChild(loop);
		return loop;
	}
	
	@Override
	public EDILoop appendHL(EDICompositeNode segment) {
		// If this is a HL too, just copy the descriptor
		if(this.getChildren().get(0).getName().equals("HL")) {
			EDILoopContainer hl = new EDILoopContainer(descriptor, this);
			hl.appendSegment(segment);
			appendChild(hl);
			return hl;
		}
		
		return appendLoop(segment);
	}
	
	private void appendChild(EDILoop loop) {
		children.add(loop);
	}
	
	@Override
	public Optional<String> getDescription() {
		return Optional.ofNullable(descriptor.getDescription()).filter(s -> !s.isEmpty());
	}
	
	@Override
	public Optional<String> getStartingSegment() {
		return Optional.ofNullable(descriptor.getStartingSegment()).filter(s -> !s.isEmpty());
	}
	
	@Override
	public List<EDILoop> getChildren() {
		// Defensive copy. The tree structure must be unmodifiable TODO document better
		return new ArrayList<>(children);
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public EDICompositeNode getSegmentContent() {
		if(children.size() > 0)
			return children.get(0).getSegmentContent();
		return null;
	}
	

}
