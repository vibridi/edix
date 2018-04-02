package com.vibridi.edix.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.vibridi.edix.TestResources;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.lexer.EDILexerFactory;
import com.vibridi.edix.lexer.Token;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.util.StringUtils;

public class TestUtils {

	
	@Test
	public void nthIndexOf() {
		String s = "the quick brown fox moved over the grass";
		// 						^    ^   ^    ^
		
		char[] a = s.toCharArray(); 
		
		assertEquals(StringUtils.nthIndexOf(a, 'o', 1), 12);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 2), 17);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 3), 21);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 4), 26);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 5), -1);	// no 5-th occurrence
	}
	
	@Test
	public void nthIndexOfEdgeCases() {
		char[] a = "".toCharArray(); 
		
		assertEquals(StringUtils.nthIndexOf(a, 'o', 3), -1);
		assertEquals(StringUtils.nthIndexOf(new char[0], 'o', 3), -1);
		try {
			StringUtils.nthIndexOf(null, 'o', 3);
		} catch(NullPointerException e) {
			assertTrue(true);
		}
	}
	
	
	@Test
	public void test() throws EDISyntaxException, IOException { // TODO move to own class
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		
		//AnsiLexer lx = new AnsiLexer();
		
		EDILexer lx = EDILexerFactory.newFor(new InputStreamReader(in));
		
		TokenType[] cc = lx.getControlCharacters();
		for(int i = 0; i < cc.length; i++) {
			if(cc[i] != TokenType.E) 
				System.out.println(cc[i] + ": " + (char)i);
			
			
		}
	}
	
	@Test
	public void test2() throws EDISyntaxException, IOException { // TODO move to own class
		InputStream in = TestResources.getAsStream("minimal-interchange.edi");
		
		//AnsiLexer lx = new AnsiLexer();
		
		EDILexer lx = EDILexerFactory.newFor(new InputStreamReader(in));
		
		for(Token t : lx.tokenize()) {
			System.out.println(t);
		}
	}
	
}
