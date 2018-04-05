package com.vibridi.edix.model;

import java.util.List;

public interface EDITextNode extends EDINode {

	// TODO consider improving the defaults 
	
	default public List<EDINode> getChildren() {
		return null;
	}
	
	default public EDINode getChild(int index) {
		return null;
	}
	
	default public EDINode getFirstChild() {
		return null;
	}
	
	default public EDINode getLastChild() {
		return null;
	}
	
}
