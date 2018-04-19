package com.vibridi.edix.loop.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.model.EDICompositeNode;

public class EDILoopNode implements EDILoop {
	
	private LoopDescriptor data;
	
//	public final String name;
//	public final String description;
//	public final Optional<String> startingSegment;
//	public final Set<String> segments;
	
	private EDILoop parent;
	private List<EDILoop> children;
	private Set<String> allowedLoopTags;
	private EDICompositeNode segment;
	
	public EDILoopNode(LoopDescriptor data, EDILoop parent) {
		this.data = data;
		this.parent = parent;
		this.children = new ArrayList<>();
		this.allowedLoopTags = new HashSet<>();
	}
	
	@Override
	public String toString() {
		return getPath();
	}
	
	@Override
	public String getPath() {
		if(isRoot())
			return "/";
		String s = parent.getPath();
		if(s.equals("/"))
			return s.concat(getName());
		else 
			return s.concat("/").concat(getName());
	}
	
	@Override
	public boolean isRoot() {
		return parent == null;
	}
	
	@Override
	public boolean isLeaf() {
		// TODO see if there's a better way. Anyway it depends on the main loop setSegment()
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
	public boolean allowsSegment(String segmentTag) {
		return data.segments.contains(segmentTag);
	}
	
	@Override
	public EDICompositeNode getSegment() {
		return segment;
	}
	
	@Override
	public void addSegment(EDICompositeNode segment) {
		if(!allowsSegment(segment.getName()))
			throw new IllegalArgumentException("Segment not allowed in context: " + this.getPath());
		addChild(new EDILoopLeaf(segment, this));
	}
	
	@Override
	public boolean allowsLoop(String segmentTag) {
		return allowedLoopTags.contains(segmentTag);
	}
	
	@Override
	public void addChild(EDILoop loop) {
		loop.getStartingSegment().ifPresent(allowedLoopTags::add);
		children.add(loop);
	}
	
	@Override
	public EDILoop newChild(EDICompositeNode segment) {
		
		
		EDILoop child = new EDILoopNode(descriptor, segment, this);
		addChild(child);
		return child;
	}
	
	@Override
	public String getName() {
		return data.name;
	}
	
	@Override
	public String getDescription() {
		return data.description == null ? "" : data.description;
	}
	
	@Override
	public Optional<String> getStartingSegment() {
		return data.startingSegment;
	}

//	@Override
//	public int nestingLevel() {
//		if(parent == null)
//			return 0;	
//		return parent.nestingLevel() + 1;
//	}
	
	@Override
	public List<EDILoop> getChildren() {
		return new ArrayList<>(children);
	}
	
}
