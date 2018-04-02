package com.vibridi.edix.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TokenStream implements Iterable<Token> {

	private List<Token> list;
	private int pos;
	
	public TokenStream() {
		list = new ArrayList<>();
		pos = 0;
	}
	
	protected void add(Token t) {
		list.add(t);
	}
	
	public int size() {
		return list.size();
	}

	@Override
	public Iterator<Token> iterator() {
		return list.iterator();
	}

	public boolean hasNext() {
		return pos < size();
	}

	public Token next() {
		if(!hasNext())
			throw new NoSuchElementException("No more tokens. Call rewind() to start over.");
		return list.get(pos++);
	}
	
	public Optional<Token> lookahead(int n) {
		return Optional.ofNullable((pos + n) >= size() ? null : list.get(pos + n));
	}
	
	public void rewind() {
		pos = 0;
	}
	
}
