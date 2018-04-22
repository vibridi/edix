package com.vibridi.edix.model.impl.x12;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.loop.LoopDescriptorManager;
import com.vibridi.edix.loop.impl.EDILoopContainer;
import com.vibridi.edix.model.EDICompositeNode;

public class X12TransactionSet {
	
	private X12FunctionalGroup group;
	private EDICompositeNode st, se;
	private String idCode;
	private String controlNumber;
	private String description;
	private List<EDICompositeNode> segments;
	private EDILoop root;
	private Map<String,EDILoop> hloops;

	public X12TransactionSet(X12FunctionalGroup group, List<EDICompositeNode> segments) throws EDISyntaxException {
		this.group = group;
		
		this.st = segments.get(0);
		if(!"ST".equals(st.getName()))
			throw new EDISyntaxException("Transaction set first segment isn't ST");
		
		this.idCode = this.st.getChild(0).getTextContent();
		this.controlNumber = this.st.getChild(1).getTextContent();
		
		this.se = segments.get(segments.size() - 1);
		if(!"SE".equals(se.getName()))
			throw new EDISyntaxException("Transaction set last segment isn't SE");
		
		if(!st.getChild(1).getTextContent().equals(se.getChild(1).getTextContent()))
			throw new EDISyntaxException("ST02 and SE02 (transaction control code) don't match");
		
		// Includes ST and SE themselves
		if(Integer.parseInt(se.getChild(0).getTextContent()) != segments.size())
			throw new EDISyntaxException("SE01 doesn't match number of segments: " + segments.size()); // TODO check size
		
		this.segments = segments.subList(1, segments.size() - 1);		
		this.hloops = new HashMap<>();
		this.root = newLoopTree();
		this.description = root.getDescription().orElse("");
		
		EDILoop currentLoop = root;
		for(EDICompositeNode seg : this.segments) {
			
			currentLoop = seg.getName().equals("HL") 
				? processHL(currentLoop, seg)
				: processSegment(currentLoop, seg);
		}
	}
	
	private EDILoop processHL(EDILoop currentLoop, EDICompositeNode seg) throws EDISyntaxException {
		String id = seg.getChild(0).getTextContent();
        String parentId = seg.getChild(1).getTextContent();
        
        if(hloops.containsKey(id))
        	throw new EDISyntaxException("Duplicate HL segment: " + seg.toString());
        
        if(!parentId.isEmpty()) {
        	if(!hloops.containsKey(parentId))
        		throw new EDISyntaxException("Can't find parent HL for segment: " + seg.toString());
        	currentLoop = hloops.get(parentId).appendHL(seg);
        	
        } else {                	
        	// A head HL can only be child of ST or a non-HL loop
        	// It's enough to set HL as one of the ST main loops, and ensure it never appears as direct child 
        	// of another HL loop
        			
        	if(!currentLoop.allowsLoop(seg.getName()))
        		throw new EDISyntaxException(
        				String.format("HL segment [%s] has no specifications at path %s", 
        						seg.toString(), 
        						currentLoop.getPath()));
        	
        	currentLoop = currentLoop.appendHL(seg);
        }
        
        hloops.put(id, currentLoop);
        return currentLoop;
	}
	
	private EDILoop processSegment(EDILoop currentLoop, EDICompositeNode seg) throws EDISyntaxException {
		if(currentLoop == null)
			throw new EDISyntaxException("No specification for current segment: " + seg.getName());
		
		if(currentLoop.allowsSegment(seg.getName())) {
			currentLoop.appendSegment(seg);
			if(seg.getName().equals("LE")) 	// Loop End
				return currentLoop.getParent();
			return currentLoop;
		}
			
		if(currentLoop.allowsLoop(seg.getName())) {
			return currentLoop.appendLoop(seg);
		}
		
		return processSegment(currentLoop.getParent(), seg);
	}
	
	public String getIdCode() {
		return idCode;
	}

	public String getControlNumber() {
		return controlNumber;
	}
	
	public String getDescription() {
		return description;
	}
	
	public EDILoop getMainLoop() {
		return root;
	}
	
	public X12FunctionalGroup getFunctionalGroup() {
		return group;
	}

	private EDILoop newLoopTree() {
		try {
			LoopDescriptor ld = LoopDescriptorManager.instance
					.forTransaction(EDIStandard.ANSI_X12, idCode, group.getInterchange().getVersionNumber());
			return new EDILoopContainer(ld, null);
		} catch (IOException e) {
			throw new RuntimeException("Can't build the loop descriptor tree.", e);
		}
	}
	
}
