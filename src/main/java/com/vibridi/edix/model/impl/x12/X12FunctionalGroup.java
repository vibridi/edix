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
	
	/**
	 * Transaction sets contained in this functional group. The number matches GE.1
	 * @return Value of GE.1
	 */
	public int size() {
		return sets.size();
	}

	/**
	 * 1-indexed access to GS fields
	 * @param i 1-indexed accessor
	 * @return Value of i-th GS field
	 */
	public String getGSField(int i) {
		if(i <= 0 || i > 8)
			throw new IllegalArgumentException(String.format("Invalid GS field.", i));
		return gs.getChild(i - 1).getTextContent();
	}
	
	/**
	 * GS01
	 * @return content of GS01
	 */
	public String getGroupHeaderCode() {
		return getGSField(1);
	}
	
	/**
	 * GS02
	 * @return content of GS02
	 */
	public String getApplicationSenderCode() {
		return getGSField(2);
	}
	
	/**
	 * GS03
	 * @return content of GS03
	 */
	public String getApplicationReceiverCode() {
		return getGSField(3);
	}
	
	/**
	 * GS04
	 * @return content of GS04
	 */
	public String getDate() {
		return getGSField(4);
	}
	
	/**
	 * GS05
	 * @return content of GS05
	 */
	public String getTime() {
		return getGSField(5);
	}
	
	/**
	 * GS06
	 * @return content of GS06
	 */
	public String getGroupControlNumber() {
		return getGSField(6);
	}
	
	/**
	 * GS07
	 * @return content of GS07
	 */
	public String getResponsibleAgencyCode() {
		return getGSField(7);
	}
	
	/**
	 * GS08
	 * @return content of GS08
	 */
	public String getVersionIdCode() {
		return getGSField(8);
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
