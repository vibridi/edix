package com.vibridi.edix.loop.impl;

import com.vibridi.edix.loop.EDILoop;

public abstract class EDILoopNode implements EDILoop {
	
	private String name;
	private EDILoop parent;
	
	// build a complete tree with all nodes: structure children
	// every node can store one segment
	// nodes can be marked as white (segment) or blue (loops)
	// later print only nodes with the segment
	// if white, print as segment, if blue print as loop with segment as first child.
	
	// loop, needs id, description
	// segment always needed.
	
	// Actual children
	// This tree is populated at construction time. Later we will print only nodes that have a segment
	
	
	// always needed
	
	public EDILoopNode(String name, EDILoop parent) {
		this.name = name;
		this.parent = parent;
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
	public EDILoop getParent() {
		return parent;
	}
	
//	@Override
//	public EDILoop getAncestor(int upToNestingLevel) {
//		EDILoop ancestor = this;
//		for(int j = nestingLevel(); j >= upToNestingLevel; j--) {
//			ancestor = ancestor.getParent();
//		}
//		return ancestor;
//	}
	
	@Override
	public String getName() {
		return name;
	}

}
