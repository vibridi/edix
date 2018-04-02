package com.vibridi.edix.model;

public class EDISegmentNode extends EDIBaseNode {

	private String name;
	
	public EDISegmentNode(String name) {
		super(null);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
