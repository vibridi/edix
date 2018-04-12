package com.vibridi.edix.model.impl.x12;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopData;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.loop.LoopDescriptorManager;
import com.vibridi.edix.model.EDICompositeNode;

public class X12TransactionSet {
	
	private EDICompositeNode st, se;
	private String idCode;
	private String controlNumber;
	private List<EDICompositeNode> segments;
	private EDILoop root;

	public X12TransactionSet(List<EDICompositeNode> segments) throws EDISyntaxException {		
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
		
		LoopDescriptor ld = getLoopDescriptor();
		
		root = new EDILoop(LoopData.NO_DATA, null);
		
		EDILoop currentLoop = root;
		for(EDICompositeNode seg : this.segments) {
						
			Optional<EDILoop> opt = ld.getMatchFor(seg, currentLoop);
			if(!opt.isPresent()) {
				currentLoop.addLoop(new EDILoop(LoopData.NO_DATA, seg, currentLoop));
				continue;
			}
			
			EDILoop lp = opt.get();
			
			// Null action. This occurs when the segment has an otherwise applicable loop descriptor
			// but isn't supposed to start a new loop in this specific context.
			if(lp.getName() == null)
				continue;
			
			// Finally we check the nesting level.
			// Case 1: this loop descriptor has a greater nesting level than the current loop
			// 		Then: start a new loop
			// Case 2: this loop descriptor has a same or smaller nesting level than the current loop
			// 		Then: close the current loop and start a new one
			if(lp.getNestingLevel() > currentLoop.nestingLevel()) {
				currentLoop.addLoop(lp);
				currentLoop = lp;
				
			} else {
				currentLoop = currentLoop.getParent();
				currentLoop.addLoop(lp);
			}
		}
	}
	
	public String getIdCode() {
		return idCode;
	}

	public String getControlNumber() {
		return controlNumber;
	}
	
	public EDILoop getRootLoop() {
		return root;
	}
	
	private LoopDescriptor getLoopDescriptor() {
		try {
			return LoopDescriptorManager.instance.forName(EDIStandard.ANSI_X12, idCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
