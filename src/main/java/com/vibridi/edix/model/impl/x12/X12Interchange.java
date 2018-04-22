package com.vibridi.edix.model.impl.x12;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;

/**
 * Facade class for X12 interchanges.
 * @author gabriele.vaccari
 *
 */
public class X12Interchange {

	private EDIMessage message;
	private EDICompositeNode isa, iea;
	private Map<String,X12FunctionalGroup> groups;
	
	public X12Interchange(EDIMessage message) throws EDISyntaxException {
		Objects.requireNonNull(message);	
		if(message.getStandard() != EDIStandard.ANSI_X12)
			throw new IllegalArgumentException("Message standard must be ANSI_X12");
		
		this.message = message;
		this.groups = new LinkedHashMap<>();
		
		List<EDICompositeNode> segments = message.getSegments();
		checkEnvelope();
		
		for(int i = 0, j = 0; i < segments.size(); i++, j++) {
			if("GS".equals(segments.get(i).getName())) {
				do {
					j++;
				} while(j < segments.size() && !"GE".equals(segments.get(j).getName()));
				
				if(j == segments.size())
					throw new EDISyntaxException("GS segment not closed");
				
				X12FunctionalGroup g = new X12FunctionalGroup(this, segments.subList(i, j+1));
				
				if(groups.containsKey(g.getGroupControlNumber()))
					throw new EDISyntaxException("Interchange contains duplicated functional groups");
				
				groups.put(g.getGroupControlNumber(), g);
				i = j;
			}
		} // end for
		
		// IEA01 must match number of functional groups
		int expectedGS = Integer.parseInt(iea.getChild(0).getTextContent());
		if(expectedGS != groups.size())
			throw new EDISyntaxException(String.format(ErrorMessages.FUNCTIONAL_GROUPS, expectedGS, groups.size()));
		

	}
	
	private void checkEnvelope() throws EDISyntaxException {
		this.isa = (EDICompositeNode) message.getRoot().getFirstChild();
		if(isa == null || !"ISA".equals(isa.getName()))
			throw new EDISyntaxException("Interchange doesn't start with ISA segment");
		
		this.iea = (EDICompositeNode) message.getRoot().getLastChild();
		if(iea == null || !"IEA".equals(iea.getName()))
			throw new EDISyntaxException("Interchange doesn't end with IEA segment");
		
		// IEA02 must match ISA13.
		if(!iea.getChild(1).getTextContent().equals(this.getControlNumber()))
			throw new EDISyntaxException(ErrorMessages.INTERCHANGE_CONTROL_NUMBER);
	}

	public int size() {
		return groups.size();
	}
	
	/**
	 * 1-indexed access to ISA fields
	 * @param i 1-indexed accessor
	 * @return Value of i-th ISA field
	 */
	public String getISAField(int i) {
		if(i <= 0 || i > 16)
			throw new IllegalArgumentException(String.format("Invalid ISA field.", i));
		return isa.getChild(i - 1).getTextContent();
	}
	
	/**
	 * ISA01
	 * @return content of ISA01
	 */
	public String getAuthInfoQualifier() {
		return getISAField(1);
	}
	
	/**
	 * ISA02
	 * @return content of ISA02
	 */
	public String getAuthInfo() {
		return getISAField(2);
	}
	
	/**
	 * ISA03
	 * @return content of ISA03
	 */
	public String getSecurityInfoQualifier() {
		return getISAField(3);
	}
	
	/**
	 * ISA04
	 * @return content of ISA04
	 */
	public String getSecurityInfo() {
		return getISAField(4);
	}
	
	/**
	 * ISA05
	 * @return content of ISA05
	 */
	public String getInterchangeIdQualifier() {
		return getISAField(5);
	}
	
	/**
	 * ISA06
	 * @return content of ISA06
	 */
	public String getSenderId() {
		return getISAField(6);
	}
	
	/**
	 * ISA08
	 * @return content of ISA08
	 */
	public String getReceiverId() {
		return getISAField(8);
	}
	
	/**
	 * ISA09
	 * @return content of ISA09
	 */
	public String getDate() {
		return getISAField(9);
	}
	
	/**
	 * ISA10
	 * @return content of ISA10
	 */
	public String getTime() {
		return getISAField(10);
	}
	
	/**
	 * ISA11
	 * @return content of ISA11
	 */
	public String getRepetitionSeparator() {
		return getISAField(11);
	}
	
	/**
	 * ISA12
	 * @return content of ISA12
	 */
	public String getVersionNumber() {
		return getISAField(12);
	}
	
	/**
	 * ISA13
	 * @return content of ISA13
	 */
	public String getControlNumber() {
		return getISAField(13);
	}
	
	/**
	 * ISA14
	 * @return true if ISA14 == 1, false otherwise
	 */
	public boolean isAckRequested() {
		return "1".equals(isa.getChild(13).getTextContent());
	}
	
	/**
	 * ISA15
	 * @return content of ISA15
	 */
	public String getUsage() {
		return getISAField(15);
	}
	
	/**
	 * ISA16
	 * @return content of ISA16
	 */
	public String getSubDelimiter() {
		return getISAField(16);
	}
	
	public Map<String,X12FunctionalGroup> getFunctionalGroups() {
		return groups;
	}
	
	public X12FunctionalGroup getFunctionalGroup(String gs06) {
		return groups.get(gs06);
	}
	
	public X12FunctionalGroup getFunctionalGroup(int i) {
		return new ArrayList<>(groups.values()).get(i);
	}
	
}
