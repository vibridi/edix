package com.vibridi.edix.lexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import org.junit.Test;

import com.vibridi.edix.EDIRegistry;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.error.EDISyntaxException;

public class TestLexer {
	
	@Test
	public void createAndUseLexer() throws EDISyntaxException, IOException {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		lexer.setSource(new PushbackReader(new InputStreamReader(in), 256));
		TokenType[] cc = lexer.getControlCharacters();
		
		assertEquals(cc[(int)'$'], TokenType.TERMINATOR);
		assertEquals(cc[(int)'~'], TokenType.DELIMITER);
		assertEquals(cc[(int)'<'], TokenType.SUB_DELIMITER);
	}
	
	@Test
	public void noRepetitionSeparatorBelow4020() throws EDISyntaxException, IOException {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		lexer.setSource(new PushbackReader(new InputStreamReader(in), 256));
		TokenType[] cc = lexer.getControlCharacters();
		
		for(TokenType tt : cc) {
			if(tt == TokenType.REPETITION_SEPARATOR)
				fail();
		}
	}
	
	@Test
	public void canFindRepetitionSeparator() throws EDISyntaxException, IOException {
		InputStream in = TestResources.getAsStream("minimal-interchange-4020.edi");
		
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		lexer.setSource(new PushbackReader(new InputStreamReader(in), 256));
		TokenType[] cc = lexer.getControlCharacters();
		
		assertEquals(cc[(int)'^'], TokenType.REPETITION_SEPARATOR);
	}
	
	@Test
	public void tokenize() throws Exception {
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		EDILexer lexer = EDIRegistry.newLexer(EDIStandard.ANSI_X12);
		assertNotNull(lexer);
		
		lexer.setSource(new PushbackReader(new InputStreamReader(in), 256));
		TokenStream ts = lexer.tokenize();
		assertNotNull(ts);
		assertTrue(ts.hasNext());
	}
	
}
