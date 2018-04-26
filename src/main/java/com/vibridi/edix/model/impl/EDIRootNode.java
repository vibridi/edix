package com.vibridi.edix.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.EDIOrderedNode;

public class EDIRootNode extends EDINodeImpl implements EDICompositeNode {

	private List<EDICompositeNode> segments;
	private AtomicInteger aint;
	
	protected EDIRootNode(EDINode parent) {
		super(parent);
		segments = new ArrayList<>();
		aint = new AtomicInteger(0);
	}
	
	public EDIRootNode(EDIRootNode that, EDINode parent) {
		super(parent);
		segments = new ArrayList<>();
		Collections.copy(this.segments, that.segments);
	}
	
	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public String getTextContent() {
		return "";
	}

	@Override
	public void setTextContent(String textContent) {
		throw new UnsupportedOperationException("Root node can't contain any text");
	}

	@Override
	public boolean isEmpty() {
		return segments.isEmpty();
	}

	@Override
	public EDINodeType getNodeType() {
		return EDINodeType.INTERCHANGE;
	}

	@Override
	public EDINode appendChild(EDINode newChild) {
		if(!(newChild instanceof EDICompositeNode))
			throw new IllegalArgumentException("Root note can contain only composite children.");
		checkOwnership(newChild, this);
		
		if(newChild instanceof EDIOrderedNode)
			((EDIOrderedNode) newChild).setLine(aint.incrementAndGet());
		
		
		segments.add((EDICompositeNode)newChild);
		return newChild;
	}

	@Override
	public List<? extends EDINode> getChildren() {
		return segments;
	}

	@Override
	public EDINode getChild(int index) {
		return segments.get(index);
	}

	@Override
	public EDINode getFirstChild() {
		return segments.get(0);
	}

	@Override
	public EDINode getLastChild() {
		return segments.get(segments.size() - 1);
	}

	@Override
	public String getDelimiter() {
		return null;
	}

	@Override
	public void setDelimiter(String delimiter) {
	}

	@Override
	public String getRepetitionSeparator() {
		return null;
	}

	@Override
	public int fields() {
		return segments.size();
	}

	@Override
	public void setRepetitionSeparator(String repetitionSeparator) {
		
	}

	@Override
	public void setRepeated(boolean repeated) {		
	}

	@Override
	public boolean isRepeated() {
		return false;
	}

	@Override
	public void appendRepetition(EDINode repetition) {		
	}

	public List<EDICompositeNode> getSegments() {
		return segments;
	}

	@Override
	public void importChild(EDINode n) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getLine() {
		return 0;
	}

}
