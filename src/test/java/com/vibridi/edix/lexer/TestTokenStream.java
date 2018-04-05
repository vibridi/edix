package com.vibridi.edix.lexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vibridi.edix.TestResources;

public class TestTokenStream {
	
	@Test
	public void tokensBasicOperations() throws Exception {
		TokenStream ts = getTokenStream();
		assertTrue(ts.hasNext());
		assertEquals(ts.size(), 86);
		
		// Progress position
		for(int i = 0; i < ts.size(); i++)
			ts.next();
		assertFalse(ts.hasNext());
		ts.rewind();
		
		// Get
		Token t = ts.next();
		assertEquals(t.type, TokenType.WORD);
		assertEquals(t.value, "ISA");
		
		Token u = ts.get(0);
		assertEquals(t.type, u.type);
		assertEquals(t.value, u.value);
	}

	@Test
	public void tokensIndexOf() throws Exception {
		TokenStream ts = getTokenStream();
		assertEquals(ts.nextIndexOf(TokenType.WORD), 0);
		ts.next();
		assertEquals(ts.nextIndexOf(TokenType.WORD), 2);
		assertEquals(ts.indexOf(0, TokenType.WORD), 0);
		ts.rewind();
		assertEquals(ts.nextIndexOf(TokenType.DELIMITER), 1);
		assertEquals(ts.nextIndexOf(TokenType.TERMINATOR), 33);
		assertEquals(ts.indexOf(34, TokenType.TERMINATOR), 51);
		
		assertEquals(ts.nextIndexOf(TokenType.SUB_SUB_DELIMITER), -1);
		assertEquals(ts.indexOf(1000, TokenType.TERMINATOR), -1);
				
		try {
			ts.indexOf(-24, TokenType.TERMINATOR);
			fail();
		} catch(ArrayIndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void tokensLookAhead() throws Exception {
		TokenStream ts = getTokenStream();
		
		// Look-ahead at zero returns current pos
		Token u = ts.lookAhead(0).get();
		Token t = ts.next();
		assertEquals(t.type, u.type);
		assertEquals(t.value, u.value);
		ts.rewind();
		
		// Can't look past size
		ts.lookAhead(ts.size() + 12).ifPresent(v -> fail());
		
		// Look-ahead doesn't change pos
		assertEquals(ts.nextIndexOf(TokenType.TERMINATOR), 33);
		
		t = ts.lookAhead(33).get();
		assertTrue(t.value.equals("$") && t.type == TokenType.TERMINATOR);
		
		u = ts.next();
		assertTrue(u.value.equals("ISA") && u.type == TokenType.WORD);
		ts.rewind();
		
		// Look-ahead based on position
		t = ts.lookAhead(33).get();
		assertTrue(t.value.equals("$") && t.type == TokenType.TERMINATOR);
		
		final int decrease = 10;
		for(int i = decrease; i > 0; i--)
			ts.next();
	
		u = ts.lookAhead(33 - decrease).get();
		assertEquals(t.type, u.type);
		assertEquals(t.value, u.value);		
	}
	
	@Test
	public void tokensLookBack() throws Exception {
		TokenStream ts = getTokenStream();
		assertEquals(ts.nextIndexOf(TokenType.TERMINATOR), 33);
		
		final int advance = 33;
		for(int i = 0; i < advance; i++)
			ts.next();
		
		// Look-back doesn't change pos
		Token t = ts.lookBack(1).get();
		assertTrue(t.value.equals("<") && t.type == TokenType.SUB_DELIMITER);
		ts.skip(1);
		Token u = ts.next();
		assertTrue(u.value.equals("GS") && u.type == TokenType.WORD);

		
		// Look-back at zero returns current pos
		t = ts.lookBack(0).get();
		assertTrue(t.value.equals("~") && t.type == TokenType.DELIMITER);
		
		// Can't look back past 0
		ts.rewind();
		ts.lookBack(2).ifPresent(v -> fail());
	}
	
	@Test
	public void tokensSubStream() throws Exception {
		TokenStream ts = getTokenStream();
		
		TokenStream tt = ts.subStream(0, 3);
		assertEquals(tt.size(), 3);
		
		for(int i = 0; i < 3; i++)
			assertEquals(ts.get(i), tt.get(i));
		
		// Changing sub-stream doesn't change the original
		tt.get(2).type = TokenType.E;
		assertNotEquals(ts.get(2).type, TokenType.E);
		
		// Getting a sub-stream doesn't change pos on the original
		Token t = ts.next();
		assertTrue(t.type == TokenType.WORD && t.value.equals("ISA"));
	}
	
	@Test
	public void tokensUntil() throws Exception {
		TokenStream ts = getTokenStream();
		TokenStream tt = ts.until(6);
		
		assertEquals(tt.size(), 6);
		Token t = tt.next();
		assertTrue(t.type == TokenType.WORD && t.value.equals("ISA"));
		
		// Until changes pos on the original
		t = ts.next();
		assertTrue(t.type == TokenType.WORD && t.value.equals("00"));
		ts.rewind();
		
		// Can combine indexOf and until
		TokenStream tv = ts.until(ts.nextIndexOf(TokenType.TERMINATOR));
		
		// Size of sub-stream equals index of 'until' limit
		assertEquals(tv.size(), ts.indexOf(TokenType.TERMINATOR));
		
		ts.skip(1);
		tv = ts.until(ts.nextIndexOf(TokenType.TERMINATOR));
		
		// Last item of sub-stream equals item before 'until' limit in the original
		// Original: 	GS~AG~04000~58401~040714~1003~38327~X~002040CHRY$
		// Sub:			GS~AG~04000~58401~040714~1003~38327~X~002040CHRY
		assertEquals(tv.get(tv.size() - 1), ts.lookBack(1).get());

		tv = ts.until(ts.size());
		assertEquals(tv.get(tv.size() - 1), ts.get(ts.size() - 1));
	}
	
	@Test
	public void tokenizedISACorrectly() throws Exception {
		TokenStream ts = getTokenStream();
		ts = ts.until(ts.nextIndexOf(TokenType.TERMINATOR) + 1);

		assertEquals(ts.next(), new Token(TokenType.WORD, "ISA"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "00"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "          "));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "00"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "          "));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "ZZ"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "04000          "));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "ZZ"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "58401          "));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "040714"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "1003"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "U"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "00204"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "000038449"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "0"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.WORD, "P"));
		assertEquals(ts.next(), new Token(TokenType.DELIMITER, "~"));
		assertEquals(ts.next(), new Token(TokenType.SUB_DELIMITER, "<"));
		assertEquals(ts.next(), new Token(TokenType.TERMINATOR, "$"));
	}
	
	@Test
	public void tokensAddAndRemove() throws Exception {
		TokenStream ts = getTokenStream();
		ts.add(new Token(TokenType.E, ""));
		assertEquals(ts.size(), 87);
		
		final int index = 12;
		Token t = ts.remove(index);
		assertEquals(ts.size(), 86);
		
		ts.add(index, t);
		Token u = ts.get(index);
		assertEquals(t.type, u.type);
		assertEquals(t.value, u.value);
	}
	
	@Test
	public void edgeCases() throws Exception {
		TokenStream empty = new TokenStream();
		assertFalse(empty.hasNext());
		// TODO complete
	}
	
	
	private TokenStream getTokenStream() throws Exception {
		return TestResources.getTokenStream();
	}
}
