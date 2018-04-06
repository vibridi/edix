package com.vibridi.edix.parser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;
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
		EDIMessage message = EDIMessageFactory.newMessage();
		
		while(tokens.hasNext()) {
			EDICompositeNode seg = (EDICompositeNode) parse(message, nextSegment(tokens));
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
			case WORD:
				seg.appendChild(EDIMessageFactory.newTextNode(seg, t.value));
				break;
			
			case DELIMITER:
			case SUB_DELIMITER:
			case SUB_SUB_DELIMITER:
				seg.setDelimiter(t.value);
				
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
		Set<EDIValidationRule> set = new HashSet<>();
		set.add(this::numberOfISAFields);
		set.add(this::ansiX12Envelope);
		return set;
	}
	
	private void numberOfISAFields(EDIMessage message) throws EDISyntaxException {
		if(message.getSegment("ISA", 0).fields() != 16)
			throw new EDISyntaxException(ErrorMessages.ISA_FIELDS_NUMBER);
	}
	
	private void ansiX12Envelope(EDIMessage message) throws EDISyntaxException {
		Stack<EDINode> stack = new Stack<>();
		int functionalGroups = 0;
		int transactionSets = 0;
		int documentSegments = 0;
		
		for(EDINode n : message.getChildren()) {
			switch(n.getName()) {
			case "ISA":
				stack.push(n);
				break;
			case "GS":
				functionalGroups++;
				stack.push(n);
				break;
			case "ST":
				transactionSets++;
				stack.push(n);
				break;
				
			case "SE":
				EDINode st = stack.pop();
				if(!st.getName().equals("ST"))
					throw new EDISyntaxException("No ST segment matching SE.");
				
				//SE01 must match number of document segments
				int expectedDocSegs = Integer.parseInt(n.getChild(0).getTextContent());
				if(expectedDocSegs != documentSegments)
					throw new EDISyntaxException(ErrorMessages.TRANSACTION_SETS, expectedDocSegs, documentSegments);
				documentSegments = 0;
				
				//SE02 must match ST02
				if(!n.getChild(1).getTextContent().equals(st.getChild(1).getTextContent()))
					throw new EDISyntaxException(ErrorMessages.TRANSACTION_CONTROL_NUMBER);
				
				break;
				
			case "GE":
				EDINode gs = stack.pop();
				
				// GE must match GS
				if(!gs.getName().equals("GS"))
					throw new EDISyntaxException("No GS segment matching GE.");
				
				//GE01 must match number of transaction sets
				int expectedSets = Integer.parseInt(n.getChild(0).getTextContent());
				if(expectedSets != transactionSets)
					throw new EDISyntaxException(ErrorMessages.TRANSACTION_SETS, expectedSets, transactionSets);
				transactionSets = 0;
				
				//GE02 must match GS06
				if(!n.getChild(1).getTextContent().equals(gs.getChild(5).getTextContent()))
					throw new EDISyntaxException(ErrorMessages.GROUP_CONTROL_NUMBER);
				
				break;
				
			case "IEA":
				EDINode isa = stack.pop();
				
				// IEA must match ISA
				if(!isa.getName().equals("ISA"))
					throw new EDISyntaxException("No ISA segment matching IEA.");
				
				// IEA01 must match number of functional groups
				int expectedGS = Integer.parseInt(n.getChild(0).getTextContent());
				if(expectedGS != functionalGroups)
					throw new EDISyntaxException(ErrorMessages.FUNCTIONAL_GROUPS, expectedGS, functionalGroups);
				
				// IEA02 must match ISA13.
				if(!n.getChild(1).getTextContent().equals(isa.getChild(12).getTextContent()))
					throw new EDISyntaxException(ErrorMessages.INTERCHANGE_CONTROL_NUMBER);
				break;
				
			default:
				documentSegments++;	
			}
		}
		
		if(!stack.isEmpty())
			throw new EDISyntaxException("Unexpected segments in interchange");
	}
	
	
}
