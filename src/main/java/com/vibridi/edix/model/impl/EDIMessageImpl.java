package com.vibridi.edix.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.path.EDIPath;

public class EDIMessageImpl implements EDIMessage {
	
	private EDIRootNode root;
	private TokenType[] controlCharacters;
	private Map<String,List<EDICompositeNode>> segments;
	private EDIStandard standard;
	
	public EDIMessageImpl(EDIStandard standard) {
		this.root = new EDIRootNode(null);
		this.segments = new HashMap<>();
		this.standard = standard;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	
	@Override
	public int size() {
		return segments.size();
	}
	
	@Override
	public void addSegment(String name, EDICompositeNode node) {
		root.appendChild(node);
		segments.computeIfAbsent(name, k -> new ArrayList<>()).add(node);
	}
	
	@Override
	public EDICompositeNode getSegment(String name, int i) {
		return segments.get(name).get(i);
	}

	@Override
	public TokenType[] getControlCharacters() {
		return controlCharacters;
	}

	@Override
	public void setControlCharacters(TokenType[] controlCharacters) {
		this.controlCharacters = controlCharacters;
	}

	/**
	 * Resolves a dot-separated path to a child node.
	 * Accepts a string in the form:<pre> {@code <segment>[.<field>]+}. </pre>
	 * Example: {@code ISA.16} <br>
	 * Example: {@code GS.2.1} <br>
	 * <br>
	 * 
	 * @param path String representing the path. Must not be null.
	 * @return text content of the node identified by this path
	 *  
	 */
	@Override
	public String getTextAt(EDIPath path) {
		return getNodeAt(path).getTextContent();
	}
	
	@Override
	public void setTextAt(EDIPath path, String text) { // TODO doesn't change name
		getNodeAt(path).setTextContent(text);
	}
	
	@Override
	public EDINode getNodeAt(EDIPath path) {
		EDINode n = getSegment(path.segment(), path.ordinal());
		Objects.requireNonNull(n);
		
		for(int i = 0; i < path.fields(); i++) {
			n = n.getChild(path.field(i));
			if(path.repetitionAccessor(i) >= 0)
				n = ((EDICompositeNode) n).getChild(path.repetitionAccessor(i));
		}
		
		return n;
	}

	@Override
	public void setStandard(EDIStandard standard) {
		throw new UnsupportedOperationException("Message standard is final.");
	}

	@Override
	public EDIStandard getStandard() {
		return standard;
	}

	@Override
	public List<EDICompositeNode> getSegments() {
		return root.getSegments();
	}

	@Override
	public EDICompositeNode getRoot() {
		return root;
	}
	
}
