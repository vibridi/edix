package com.vibridi.edix.model.impl;

import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;

public class EDIMessageFactory {

	public static EDIMessage newMessage() {
		return new EDIMessageImpl();
	}
	
	public static EDICompositeNode newCompositeNode(EDINode parent) {
		return new EDICompositeNodeImpl(parent);
	}

	public static EDINode newTextNode(EDINode parent, String text) {
		return new EDISimpleTextNode(parent, text);
	}
	
}
