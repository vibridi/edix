package com.vibridi.edix.model.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.path.EDIPath;

public class EDIMessageImpl extends EDICompositeNodeImpl implements EDIMessage {
	
	private TokenType[] controlCharacters;
	private Map<String,List<EDICompositeNode>> segments;
	private EDIStandard standard;
	
	public EDIMessageImpl(EDIStandard standard) {
		super(null);
		this.segments = new LinkedHashMap<>();
		this.standard = standard;
	}

	@Override
	public EDINodeType getNodeType() {
		return EDINodeType.INTERCHANGE;
	}
	
	@Override
	public void addSegment(String name, EDICompositeNode node) {
		checkOwnership(node, this);
		getChildren().add(node);
		
		if(segments.containsKey(name)) {
			segments.get(name).add(node);
			
		} else {
			List<EDICompositeNode> list = new ArrayList<>();
			list.add(node);
			segments.put(name, list);
		}
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
	public boolean isRoot() {
		return true;
	}

	@Override
	public void setStandard(EDIStandard standard) {
		throw new UnsupportedOperationException("Message standard is final.");
	}

	@Override
	public EDIStandard getStandard() {
		return standard;
	}
	
	public Map<String,List<EDICompositeNode>> getSegments() {
		return segments;
	}
}
