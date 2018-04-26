package com.vibridi.edix.loop.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import com.vibridi.edix.error.ErrorMessages;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.util.MiscUtils;

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
	
//	@Override
//	public EDILoop copyStructure() {
//		EDILoopContainer copy = new EDILoopContainer(descriptor, this.getParent());
//		for(EDILoop child : children)
//			copy.appendChild(child.copyStructure());
//		return copy;
//	}
	
	@Override
	public boolean allowsSegment(String segmentTag) {
		return descriptor.getAllowedSegments().contains(segmentTag);
	}
	
	@Override
	public boolean allowsLoop(String loopTag) {
		return descriptor.getAllowedLoops().keySet()
				.stream()
				.anyMatch(k -> k.startsWith(loopTag));
	}

	@Override
	public void appendSegment(EDICompositeNode segment) {
		if(!allowsSegment(segment.getName()) && !getStartingSegment().orElse("").equals(segment.getName()))
			throw new IllegalArgumentException("This loop doesn't allow the segment: " + segment.getName());
		appendChild(new EDILoopLeaf(segment, this));
	}
	
	@Override
	public EDILoop appendLoop(EDICompositeNode segment) {
		String key = segment.getName();
		
		if(key.equals("HL"))
			return appendHL(segment);
		
		if(!allowsLoop(key))
			throw new IllegalArgumentException(String.format(
					"This loop %s doesn't allow the loop %s",
					this.toString(),
					segment.getName()));
		
		if(descriptor.isAmbiguous(key))
			key = MiscUtils.getDescriptorKey(segment);
		
		LoopDescriptor d = descriptor.getDescriptor(key);
		
		if(d == null)
			throw new IllegalStateException(
					String.format(ErrorMessages.LOOP_DESCRIPTOR_MISSING, key, segment, segment.getLine()));
		
		EDILoopContainer loop = new EDILoopContainer(d, this);
		loop.appendSegment(segment);
		appendChild(loop);
		return loop;
	}
	
	@Override
	public EDILoop appendHL(EDICompositeNode segment) {
		String key = segment.getName();
		
		if(!key.equals("HL"))
			throw new IllegalArgumentException("Segment is not HL");
		
		// HL segments are usually appended to other hierarchical loop instances, 
		// but generally they don't specify HL loop descriptors themselves. 
		// Therefore we must walk the tree up until we find a container that does have HL descriptors.
		EDILoopContainer hlContainer = this;
		while(hlContainer != null && !hlContainer.descriptor.hasHLDescriptors())
			hlContainer = (EDILoopContainer) hlContainer.getParent();
		
		if(hlContainer == null)
			throw new IllegalStateException("Cannot find a suitable HL container from this path: " + this.getPath());
		
		if(hlContainer.descriptor.isAmbiguous(key))
			key = MiscUtils.getDescriptorKey(segment);		
		
		LoopDescriptor hlDescriptor = hlContainer.descriptor.getDescriptor(key);
		
		if(hlDescriptor == null)
			throw new IllegalStateException(String.format(ErrorMessages.LOOP_DESCRIPTOR_MISSING, key));
		
		EDILoopContainer hl = new EDILoopContainer(hlDescriptor, this);
		hl.appendSegment(segment);
		appendChild(hl);
		return hl;
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
