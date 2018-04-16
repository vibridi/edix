package com.vibridi.edix.model.impl.x12;

import java.io.IOException;
import java.util.List;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.loop.LoopDescriptorManager;
import com.vibridi.edix.loop.LoopMatcher;
import com.vibridi.edix.loop.LoopMatcher.LoopMatch;
import com.vibridi.edix.loop.impl.EDILoopNode;
import com.vibridi.edix.model.EDICompositeNode;

public class X12TransactionSet {
	
	private X12FunctionalGroup group;
	private EDICompositeNode st, se;
	private String idCode;
	private String controlNumber;
	private String description;
	private List<EDICompositeNode> segments;
	private EDILoopNode root;

	public X12TransactionSet(X12FunctionalGroup group, List<EDICompositeNode> segments) throws EDISyntaxException {
		this.group = group;
		this.root = new EDILoopNode(null, null);
		
		this.st = segments.get(0);
		if(!"ST".equals(st.getName()))
			throw new EDISyntaxException("Transaction set first segment isn't ST");
		
		this.idCode = this.st.getChild(0).getTextContent();
		this.controlNumber = this.st.getChild(1).getTextContent();
		
		this.se = (EDICompositeNode) segments.get(segments.size() - 1);
		if(!"SE".equals(se.getName()))
			throw new EDISyntaxException("Transaction set last segment isn't SE");
		
		if(!st.getChild(1).getTextContent().equals(se.getChild(1).getTextContent()))
			throw new EDISyntaxException("ST02 and SE02 (transaction control code) don't match");
		
		if(Integer.parseInt(se.getChild(0).getTextContent()) != segments.size())
			throw new EDISyntaxException("SE01 doesn't match number of segments.");
		
		this.segments = segments.subList(1, segments.size() - 1);
		
		LoopMatcher ld = getLoopMatcher();
		this.description = ld.getDescription();
		
		EDILoop currentLoop = root;
		for(EDICompositeNode seg : this.segments) {
			
			LoopMatch test = ld.findMatch(seg, currentLoop.toString());
			
			if(!test.matches) {
				currentLoop.addSegment(seg);
				continue;
			}
			
			// Finally we check the nesting level.
			// Case 1: this loop descriptor has a greater nesting level than the current loop
			// 		Then: start a new loop under this one.
			// Case 2: this loop descriptor has a same or smaller nesting level than the current loop
			// 		Then: close the current loop and return to the appropriate tree node
			if(test.data.level > currentLoop.nestingLevel()) {
				currentLoop = currentLoop.newChild(test.data, seg);
				
			} else {
				// Walk the tree up until the nesting level indicated by this descriptor
				// We actually retrieve the parent of the wanted nesting level, so we can add a new segment to it.
				currentLoop = currentLoop.getAncestor(test.data.level);
				
				// If the descriptor's name is the special character '.' ... 
				if(test.data.name.equals(LoopDescriptor.CURRENT_LOOP)) {
					// ...we stay in the current loop. 
					// This means the descriptor was used to break out of the ongoing loop
					currentLoop.addSegment(seg);
					
				} else {
					// Otherwise we start a new loop.
					currentLoop = currentLoop.newChild(test.data, seg);
				}
			}
		}
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
	
	public EDILoopNode getMainLoop() {
		return root;
	}
	
	public X12FunctionalGroup getFunctionalGroup() {
		return group;
	}
	
	private LoopMatcher getLoopMatcher() {
		try {
			return LoopDescriptorManager.instance
					.forTransaction(EDIStandard.ANSI_X12, idCode, group.getInterchange().getVersionNumber());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
