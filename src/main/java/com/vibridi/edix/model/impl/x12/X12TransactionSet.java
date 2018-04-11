package com.vibridi.edix.model.impl.x12;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDICompositeNode;

public class X12TransactionSet {
	
	private EDICompositeNode st, se;
	private String idCode;
	private String controlNumber;
	
	private List<EDICompositeNode> segments;
	private Map<String,EDICompositeNode> content;

	public X12TransactionSet(List<EDICompositeNode> segments) throws EDISyntaxException {		
		this.content = new LinkedHashMap<>();
		this.st = (EDICompositeNode) segments.get(0); // TODO check class cast?
		if(!"ST".equals(st.getName()))
			throw new EDISyntaxException("Transaction set first segment isn't ST");
		
		this.idCode = this.st.getChild(0).getTextContent();
		this.controlNumber = this.st.getChild(1).getTextContent();
		
		this.se = (EDICompositeNode) segments.get(segments.size() - 1);
		if(!"SE".equals(se.getName()))
			throw new EDISyntaxException("Transaction set last segment isn't SE");
		
		if(!st.getChild(1).getTextContent().equals(se.getChild(1).getTextContent()))
			throw new EDISyntaxException("ST02 and SE02 (transaction control code) don't match");
		
		this.segments = segments;
		// parse loops?
//		for(EDINode n : segments)
//			content.put(n.getName(), (EDICompositeNode) n); // check cast? but must be composite
		
		if(Integer.parseInt(se.getChild(0).getTextContent()) != segments.size()) // TODO check
			throw new EDISyntaxException("SE01 doesn't match number of segments.");
	}
	
	public String getIdCode() {
		return idCode;
	}

	public String getControlNumber() {
		return controlNumber;
	}

}
