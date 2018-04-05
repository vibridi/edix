package com.vibridi.edix.model.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;

public class EDIMessageImpl extends EDICompositeNodeImpl implements EDIMessage {
	
	private TokenType[] controlCharacters;
	private Map<String,EDICompositeNode> segments;
	private String event;

	protected EDIMessageImpl() {
		super(null);
		segments = new LinkedHashMap<>();
	}
	
	@Override
	public EDINodeType getNodeType() {
		return EDINodeType.INTERCHANGE;
	}
	
	@Override
	public void addSegment(String name, EDICompositeNode node) {
		checkOwnership(node, this);
		getChildren().add(node);
		segments.put(name, node);
	}
	
	@Override
	public EDICompositeNode getSegment(String name) {
		return segments.get(name);
	}
	
	// TODO remove?
	public String getEvent() {
		return event;
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
	 * 
	 */
	@Override
	public String getTextAt(String path) {
		//SEG.f1.f2.f3
		String[] cmp = path.split("\\.");
		if(cmp.length < 1)
			return this.getTextContent();
		
		EDINode n = getSegment(cmp[0]);
		
		for(int i = 1; i < cmp.length; i++) {
			n = n.getChild(Integer.parseInt(cmp[i]) - 1);
		}
		
		return n.getTextContent();
		// TODO polish
	}
	
	@Override
	public boolean isRoot() {
		return true;
	}
	
}
