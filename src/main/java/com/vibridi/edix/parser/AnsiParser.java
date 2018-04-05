package com.vibridi.edix.parser;

import java.util.Stack;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.Token;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.impl.EDIMessageFactory;
import com.vibridi.edix.util.MiscUtils;

public class AnsiParser extends EDIParser {
	
	private Stack<Token> stack;
	
	public AnsiParser() {
		stack = new Stack<>();
	}
	
	@Override
	public EDIMessage parse(TokenStream tokens) throws EDISyntaxException {
		EDIMessage message = EDIMessageFactory.newMessage();
		
		while(tokens.hasNext()) {
			EDICompositeNode seg = (EDICompositeNode) parse(message, nextSegment(tokens));
			message.addSegment(seg.getName(), seg);
		}
		
		validate(message);
		return message;
	}
	
	private EDINode parse(EDICompositeNode parent, TokenStream tokens) throws EDISyntaxException {
		// Example segment: BEG*00*SA*95018017***950118$
		
		if(tokens.size() == 0)
			return EDIMessageFactory.newTextNode(parent, "");
		
		if(tokens.size() == 1)
			return EDIMessageFactory.newTextNode(parent, tokens.next().value);
		// Covers one-value fields
		// Covers the case in which a control character is used as a literal value as in ISA.16
		
		EDICompositeNode seg = EDIMessageFactory.newCompositeNode(parent);
		if(parent.isRoot()) {
			Token t = tokens.next();
			seg.setName(t.value);
			seg.setTextContent(t.value);
		}
		
		// *00*SA*95018017***950118$

		// 		2. sub-field first slot is empty (eg. BEG*00*SA*<95018017<stuff*
		// 												        ^ BEG.3.1 = empty
//																^ BEG.3.2 =  95018017			
		
		while(tokens.hasNext()) {	
			Token t = tokens.next();
			
			switch(t.type) {
			case WORD:
				seg.appendChild(EDIMessageFactory.newTextNode(seg, t.value));
				break;
			
			case DELIMITER:
			case SUB_DELIMITER:
			case SUB_SUB_DELIMITER:
				// TODO set separator
				
				if(!tokens.lookBack(2).isPresent())
					seg.appendChild(EDIMessageFactory.newTextNode(seg, ""));
				
				int d = MiscUtils.coalesce(n -> n != -1, 
					tokens.nextIndexOf(t.type), 
					tokens.nextIndexOf(TokenType.TERMINATOR),
					tokens.size());
				seg.appendChild(parse(seg, tokens.until(d)));
				break;
				
			case REPETITION_SEPARATOR:
				// TODO 
				break;
				
			default:
				break;
			
			} // end switch
			
		}
		
		return seg;
	}

	public void validate(EDIMessage message) {
		if(!strict)
			return;
		
		// TODO validate X12
	}
	
	private TokenStream nextSegment(TokenStream tokens) throws EDISyntaxException {
		if(!tokens.hasNext())
			return new TokenStream();
		
		if(tokens.lookAhead(0).get().type != TokenType.WORD)
			throw new EDISyntaxException("Segment doesn't start with a segment tag.");
		
		int i = tokens.nextIndexOf(TokenType.TERMINATOR);
		if(i == -1)
			throw new EDISyntaxException("Unexpected end of segment. No terminator");
		
		return tokens.until(i+1);
	}
}
