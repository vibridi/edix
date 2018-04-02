package com.vibridi.edix.model;

import java.util.List;
import java.util.Optional;

public interface EDINode {
	
	public Optional<EDINode> getParent();
	public List<EDINode> getChildren();
	public EDINode getChild(int index);
	public String getName();
	
}
