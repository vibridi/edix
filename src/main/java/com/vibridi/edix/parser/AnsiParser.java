package com.vibridi.edix.parser;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.lexer.Token;
import com.vibridi.edix.lexer.TokenStream;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.impl.EDIMessageFactory;
import com.vibridi.edix.util.MiscUtils;

public class AnsiParser extends EDIParser {
	
	public AnsiParser() {
	}
	
	@Override
	public EDIMessage parse(EDILexer lexer) throws EDISyntaxException, IOException {
		TokenStream tokens = lexer.tokenize();
		EDIMessage message = EDIMessageFactory.newMessage(lexer.getStandard());
		
		while(tokens.hasNext()) {
			EDICompositeNode seg = (EDICompositeNode) parse(message.getRoot(), nextSegment(tokens));
			message.addSegment(seg.getName(), seg);
		}
		
		if(strict)
			validate(message);
		message.setControlCharacters(lexer.getControlCharacters());
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
			
		while(tokens.hasNext()) {	
			Token t = tokens.next();
			
			switch(t.type) {
				
			// It arrives here in case of a composite field
			case WORD:
				seg.appendChild(EDIMessageFactory.newTextNode(seg, t.value));
				break;
			
			case DELIMITER:
				seg.setDelimiter(t.value);
				int stop = MiscUtils.coalesce(n -> n != -1,
						tokens.nextIndexOf(t.type), 
						tokens.nextIndexOf(TokenType.TERMINATOR));
				seg.appendChild(parse(seg, tokens.until(stop)));
				break;
				
			case SUB_DELIMITER:
			case SUB_SUB_DELIMITER:
				seg.setDelimiter(t.value);
				
				if(!tokens.lookBack(2).isPresent())
					seg.appendChild(EDIMessageFactory.newTextNode(seg, ""));
				
				int ctrl = tokens.nextIndexOfControlChar(); // Includes repetition separator
				seg.appendChild(parse(seg, tokens.until(ctrl)));
				break;
				
			case REPETITION_SEPARATOR:
				seg.setRepeated(true);
				seg.setRepetitionSeparator(t.value);
				int rep = MiscUtils.coalesce(n -> n != -1,
						tokens.nextIndexOf(t.type),
						tokens.nextIndexOf(TokenType.DELIMITER),
						tokens.nextIndexOf(TokenType.TERMINATOR),
						tokens.size());
				seg.appendRepetition(parse(seg, tokens.until(rep)));
				
			default:
				break;
			
			} // end switch
			
		}
		
		return seg;
	}
	
	private TokenStream nextSegment(TokenStream tokens) throws EDISyntaxException {
		if(!tokens.hasNext())
			return new TokenStream(); // TODO consider TokenStream.emptyStream();
		
		if(tokens.lookAhead(0).get().type != TokenType.WORD)
			throw new EDISyntaxException("Segment doesn't start with a segment tag.");
		
		int i = tokens.nextIndexOf(TokenType.TERMINATOR);
		if(i == -1)
			throw new EDISyntaxException("Unexpected end of segment. No terminator");
		
		return tokens.until(i+1);
	}

	@Override
	public Set<EDIValidationRule> initValidationRules() {
		return Collections.emptySet();
	}
	
}
