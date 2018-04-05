package com.vibridi.edix.model.impl;

import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.EDITextNode;

public class EDIMessageFactory {

	public static EDIMessage newMessage() {
		return new EDIMessageImpl();
	}
	
	public static EDICompositeNode newCompositeNode(EDINode parent) {
		return new EDICompositeNodeImpl(parent);
	}

	public static EDITextNode newTextNode(EDINode parent, String text) {
		return new EDITextNodeImpl(parent, text);
	}
	
}
