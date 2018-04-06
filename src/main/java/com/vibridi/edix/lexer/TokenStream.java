package com.vibridi.edix.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class TokenStream implements Iterable<Token> {

	private List<Token> list;
	private int pos;
	
	public TokenStream() {
		this.list = new ArrayList<>();
		this.pos = 0;
	}
	
	public void add(Token t) {
		list.add(t);
	}
	
	public void add(int i, Token t) {
		list.add(i, t);
	}
	
	public Token get(int i) {
		return list.get(i);
	}
	
	public Token remove(int i) {
		return list.remove(i);
	}
	
	public int size() {
		return list.size();
	}
	
	public int remaing() {
		return list.size() - pos;
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
	
	public void skip(int i) {
		pos += i;
	}
	
	public Optional<Token> lookAhead(int n) {
		return Optional.ofNullable((pos + n) >= size() ? null : list.get(pos + n));
	}
	
	public Optional<Token> lookBack(int n) {
		// No need to check for pos-n >= size(). If pos == 0 then 0 - n always < 0
		return Optional.ofNullable((pos - n) < 0 ? null : list.get(pos - n));
	}
	
	public int nextIndexOf(TokenType type) {
		for(int i = pos; i < list.size(); i++) {
			if(list.get(i).type == type)
				return i;
		}
		return -1;
	}
	
	public int indexOf(TokenType type) {
		return indexOf(0, type);
	}
	
	public int indexOf(int start, TokenType type) {
		for(int i = start; i < list.size(); i++) {
			if(list.get(i).type == type)
				return i;
		}
		return -1;
	}
	
	public int nextIndexOfControlChar() {
		for(int i = pos; i < list.size(); i++) {
			if(list.get(i).type != TokenType.WORD)
				return i;
		}
		return size();
	}
	
	public TokenStream subStream(int start, int end) {
		int to = Math.min(end, size());
		TokenStream sub = new TokenStream();
		list.subList(start, to).forEach(t -> sub.add(t.clone()));
		return sub;
	}
	
	public TokenStream until(int exclusive) {
		int to = Math.min(exclusive, size());
		TokenStream sub = new TokenStream();
		list.subList(pos, to).forEach(t -> sub.add(t.clone()));
		pos = to;
		return sub;
	}
	
	public void rewind() {
		pos = 0;
	}
	
	public TokenStream unescape() {
		TokenStream ts = new TokenStream();
		Queue<Token> q = new LinkedList<>();
		
		for(int i = 0; i < size(); i++) {
			switch(list.get(i).type) {
			
			case RELEASE:
				// TODO check i+1 < size()
				list.get(i+1).type = TokenType.WORD;
				break;
			
			case WORD:
				q.add(list.get(i));
				break;
				
			default:
				if(!q.isEmpty()) {
					
					String v = q.stream()
						.map(t -> t.value)
						.collect(Collectors.joining());
					
					ts.add(new Token(TokenType.WORD, v));
					q.clear();
				}
				ts.add(list.get(i));
				break;
			}
			
			
		}
		
		return ts;
	}
	
	@Override
	public String toString() {
		return list.stream().map(t -> t.value).collect(Collectors.joining(", ", "[", "]"));
	}
	
}
