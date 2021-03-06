package com.vibridi.edix.loop;

import java.util.List;
import java.util.Optional;

import com.vibridi.edix.model.EDICompositeNode;

public interface EDILoop {

	public boolean isRoot();
	public boolean isTerminal();
	
	public String getPath();
	public String getName();
	public Optional<String> getDescription();
	public Optional<String> getStartingSegment();
	
	public EDILoop getParent();
	public List<EDILoop> getChildren();
	public EDICompositeNode getSegmentContent();
	
	public boolean allowsSegment(String segmentTag);
	public void appendSegment(EDICompositeNode segment);
	
	public boolean allowsLoop(String loopTag);
	public EDILoop appendLoop(EDICompositeNode segment);
	public EDILoop appendHL(EDICompositeNode segment);
}
