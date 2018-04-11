package com.vibridi.edix.model;

import java.util.List;

public interface EDINode {
	
	public enum EDINodeType {
		INTERCHANGE,
		COMPOSITE_NODE,
		TEXT_NODE
	}
	
	public String getName();
	public void setName(String name);
	public String getTextContent();
	public void setTextContent(String textContent);
	public boolean isEmpty();
	public boolean isEndOfComposite();
	
	public EDINodeType getNodeType();
	public EDINode getOwnerInterchange();
	public EDINode getParent();
	public boolean isRoot();
	
	public EDINode appendChild(EDINode newChild);
	public List<? extends EDINode> getChildren();
	public EDINode getChild(int index);
	public EDINode getFirstChild();
	public EDINode getLastChild();
}
