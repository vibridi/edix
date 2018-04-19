package com.vibridi.edix.loop;

import java.util.List;
import java.util.Optional;

import com.vibridi.edix.model.EDICompositeNode;

public interface EDILoop {

	public boolean isRoot();
	public boolean isLeaf();
	
	public boolean allowsSegment(String segmentTag);
	public boolean allowsLoop(String segmentTag);
	
	public String getName();
	public String getPath();
	public String getDescription();
	//public int nestingLevel();
	public Optional<String> getStartingSegment();
	
	public EDILoop getParent();
	public EDILoop getAncestor(int upToNestingLevel);
	public EDICompositeNode getSegment();
	
	public void addSegment(EDICompositeNode segment);
	public void addChild(EDILoop loop);
	public EDILoop newChild(LoopDescriptor descriptor, EDICompositeNode segment);
	
	public List<EDILoop> getChildren();
}
