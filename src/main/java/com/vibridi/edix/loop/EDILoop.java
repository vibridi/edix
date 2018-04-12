package com.vibridi.edix.loop;

import java.util.ArrayList;
import java.util.List;

import com.vibridi.edix.model.EDICompositeNode;

public class EDILoop {
	
	private LoopData data;
	private EDILoop parent;
	private List<EDILoop> children;
	private EDICompositeNode segment;
	
	public EDILoop(LoopData data, EDICompositeNode headSegment) {
		this(data, headSegment, null);
	}
	
	public EDILoop(LoopData data, EDICompositeNode headSegment, EDILoop parent) {
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
	
	public boolean singleSegment() {
		return children.size() == 1;
	}
	
	public EDILoop getParent() {
		return parent;
	}
	
	public EDICompositeNode getSegment() {
		return segment;
	}
	
	public void addLoop(EDILoop loop) {
		children.add(loop);
	}
	
	public String getName() {
		return data.name;
	}
	
	public int getNestingLevel() {
		return data.level;
	}

	public int nestingLevel() {
		if(parent == null)
			return 0;
		
		return parent.nestingLevel() + 1;
	}
	
}
