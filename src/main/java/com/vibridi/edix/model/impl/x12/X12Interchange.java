package com.vibridi.edix.model.impl.x12;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;

/**
 * Facade class for X12 interchanges.
 * @author gabriele.vaccari
 *
 */
public class X12Interchange {

	private EDIMessage message;
	private EDICompositeNode isa;
	private Map<String,X12FunctionalGroup> groups;
	
	public X12Interchange(EDIMessage message) throws EDISyntaxException {
		Objects.requireNonNull(message);	
		if(message.getStandard() != EDIStandard.ANSI_X12)
			throw new IllegalArgumentException("Message standard must be ANSI_X12");
		
		this.message = message;
		this.isa = message.getSegment("ISA", 0);
		this.groups = new LinkedHashMap<>();
		
		List<EDINode> segments = message.getChildren();
		
		for(int i = 0, j = 0; i < segments.size(); i++, j++) {
			if("GS".equals(segments.get(i).getName())) {
				do {
					j++;
				} while(j < segments.size() && !"GE".equals(segments.get(j).getName()));
				
				if(j == segments.size())
					throw new EDISyntaxException("GS segment not closed");
				
				X12FunctionalGroup g = new X12FunctionalGroup(segments.subList(i, j+1));
				
				if(groups.containsKey(g.getGroupControlNumber()))
					throw new EDISyntaxException("Interchange contains duplicated functional groups");
				
				groups.put(g.getGroupControlNumber(), g);
			}
		}		
	}
	
	public Map<String,X12FunctionalGroup> getFunctionalGroups() {
		return groups;
	}
	
	public X12FunctionalGroup getFunctionalGroup(String gs06) {
		return groups.get(gs06);
	}
	
	public long getDocuments(String gs06) {
		return message.getChildren().stream().filter(seg -> "GS".equals(seg.getName())).count();
	}
	
}
