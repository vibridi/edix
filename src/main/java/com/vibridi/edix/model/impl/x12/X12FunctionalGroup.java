package com.vibridi.edix.model.impl.x12;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDICompositeNode;

public class X12FunctionalGroup {

	private X12Interchange interchange;
	private Map<String,X12TransactionSet> sets;
	private EDICompositeNode gs, ge;
	
	public X12FunctionalGroup(X12Interchange interchange, List<EDICompositeNode> segments) throws EDISyntaxException {
		this.interchange = interchange;
		this.sets = new LinkedHashMap<>();
		this.gs = segments.get(0);
		if(!"GS".equals(gs.getName()))
			throw new EDISyntaxException("Functional group first segment isn't GS");
		
		this.ge = segments.get(segments.size() - 1);
		if(!"GE".equals(ge.getName()))
			throw new EDISyntaxException("Functional group last segment isn't GE");
		
		if(!gs.getChild(5).getTextContent().equals(ge.getChild(1).getTextContent()))
			throw new EDISyntaxException("GS06 and GE02 (group control code) don't match");
		
		for(int i = 0, j = 0; i < segments.size(); i++, j++) {
			if("ST".equals(segments.get(i).getName())) {
				do {
					j++;
				} while(j < segments.size() && !"SE".equals(segments.get(j).getName()));
				
				if(j == segments.size())
					throw new EDISyntaxException("ST segment not closed");
				
				X12TransactionSet ts = new X12TransactionSet(this, segments.subList(i, j+1));
				
				if(sets.containsKey(ts.getControlNumber()))
					throw new EDISyntaxException("Functional group contains duplicated transaction sets.");
				
				sets.put(ts.getControlNumber(), ts);
				i = j;
			}
		}
		
		if(Integer.parseInt(ge.getChild(0).getTextContent()) != sets.size())
			throw new EDISyntaxException("GE01 doesn't match number of transaction sets.");
	}
	
	public int size() {
		return sets.size();
	}

	public String getGroupHeaderCode() {
		return gs.getChild(0).getTextContent();
	}
	
	public String getApplicationSendersCode() {
		return gs.getChild(1).getTextContent();
	}
	
	public String getApplicationReceiversCode() {
		return gs.getChild(2).getTextContent();
	}
	
	public String getDate() {
		return gs.getChild(3).getTextContent();
	}
	
	public String getTime() {
		return gs.getChild(4).getTextContent();
	}
	
	public String getGroupControlNumber() {
		return gs.getChild(5).getTextContent();
	}
	
	public String getResponsibleAgencyCode() {
		return gs.getChild(6).getTextContent();
	}
	
	public String getVersionIdCode() {
		return gs.getChild(7).getTextContent();
	}
	
	public X12Interchange getInterchange() {
		return interchange;
	}
	
	public X12TransactionSet getTransactionSet(String st02) {
		return sets.get(st02);
	}
	
	public X12TransactionSet getTransactionSet(int i) {
		return new ArrayList<>(sets.values()).get(i);
	}
}
