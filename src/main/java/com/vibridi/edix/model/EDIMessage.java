package com.vibridi.edix.model;

import java.util.Map;

public class EDIMessage {

	private EDINode root;
	
	private Map<String,EDINode> segments;
	
	
	public EDIMessage() {
		root = new EDIBaseNode(null);
	}

	public EDINode getRoot() {
		return root;
	}
	
	public EDINode getSegment(String name) {
		return segments.get(name);
	}
	
}
