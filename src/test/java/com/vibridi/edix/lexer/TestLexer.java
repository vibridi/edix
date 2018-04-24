package com.vibridi.edix.lexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vibridi.edix.TestResources;

public class TestLexer {
	
	@Test
	public void createAndUseLexer() throws Exception {
		EDILexer lexer = TestResources.getAsLexer("minimal-interchange.edi");		
		assertNotNull(lexer);
	
		TokenType[] cc = lexer.getControlCharacters();
		
		assertEquals(cc[(int)'$'], TokenType.TERMINATOR);
		assertEquals(cc[(int)'~'], TokenType.DELIMITER);
		assertEquals(cc[(int)'<'], TokenType.SUB_DELIMITER);
	}
	
	@Test
	public void noRepetitionSeparatorBelow4020() throws Exception {
		EDILexer lexer = TestResources.getAsLexer("minimal-interchange.edi");		
		assertNotNull(lexer);
		TokenType[] cc = lexer.getControlCharacters();
		
		for(TokenType tt : cc) {
			if(tt == TokenType.REPETITION_SEPARATOR)
				fail();
		}
	}
	
	@Test
	public void canFindRepetitionSeparator() throws Exception {
		EDILexer lexer = TestResources.getAsLexer("minimal-interchange-4020.edi");		
		assertNotNull(lexer);
		TokenType[] cc = lexer.getControlCharacters();
		assertEquals(cc[(int)'^'], TokenType.REPETITION_SEPARATOR);
	}
	
	@Test
	public void tokenize() throws Exception {
		EDILexer lexer = TestResources.getAsLexer("minimal-interchange.edi");		
		assertNotNull(lexer);
		TokenStream ts = lexer.tokenize();
		assertNotNull(ts);
		assertTrue(ts.hasNext());
	}
	
	@Test
	public void tokenizeWithBINSegment() throws Exception {
		EDILexer lexer = TestResources.getAsLexer("test-interchange-bin.edi");		
		assertNotNull(lexer);
		TokenStream ts = lexer.tokenize();
		assertNotNull(ts);
		assertTrue(ts.hasNext());
	}
	
}
