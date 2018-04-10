package com.vibridi.edix.path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vibridi.edix.error.EDIPathException;

public class EDIPath {

	private static Map<String,EDIPath> cache = new HashMap<>();
	
	public static EDIPath of(String path) throws EDIPathException {
		EDIPath p = cache.get(path);
		if(p == null) {
			p = new EDIPath(path).compile();
		}
		return p;
	}
	
	private String path;
	private String segment;
	private int ordinal;
	private int[] fields;
	private int[] repetitionAccessors;
	
	private EDIPath() {
		this("");
	}
	
	private EDIPath(String source) {
		this.path = source;
		this.ordinal = 0;
	}
	
	@Override
	public String toString() {
		return path;
	}
	
	protected EDIPath compile() throws EDIPathException {
		if(path == null || path.isEmpty())
			throw new EDIPathException("Invalid path. Must not be empty");

		String[] cmp = path.split("\\.");
		
		Matcher m = Pattern.compile("^(.*?)(\\[([0-9]*)\\])?$").matcher(cmp[0]);
		if(m.matches()) {
			segment = Optional.of(m.group(1)).get(); // Inline null check
			if(m.group(3) != null)
				ordinal = Integer.parseUnsignedInt(m.group(3)) - 1;
			if(ordinal < 0)
				throw new EDIPathException("Invalid path. Segment accessor must be 1-indexed");
		}
		
		fields = new int[cmp.length - 1];
		repetitionAccessors = new int[cmp.length - 1];
		Arrays.fill(repetitionAccessors, -1);
		
		for(int i = 1; i < cmp.length; i++) {
			
			m = Pattern.compile("^([0-9]*?)(\\[([0-9]*)\\])?$").matcher(cmp[i]);
			if(m.matches()) {
				String f = Optional.of(m.group(1)).get(); // Inline null check
				int a;
				try {
					a = Integer.parseUnsignedInt(f) - 1;
				} catch(NumberFormatException e) {
					throw new EDIPathException("Invalid path. Field accessor [" + f + "] must be a non-negative number");
				}
				if(a < 0)
					throw new EDIPathException("Invalid path. Field accessors must be 1-indexed");
				fields[i-1] = a;
				
				if(m.group(3) != null) {
					try {
						a = Integer.parseUnsignedInt(m.group(3)) - 1;
					} catch(NumberFormatException e) {
						throw new EDIPathException("Invalid path. Field accessor [" + f + "] must be a non-negative number");
					}
					if(a < 0)
						throw new EDIPathException("Invalid path. Field accessors must be 1-indexed");
					repetitionAccessors[i-1] = a;
				}
			}
		}
		
		return this;
	}
	
	public String segment() {
		return segment;
	}
	
	public int ordinal() {
		return ordinal;
	}
	
	public int fields() {
		return fields.length;
	}
	
	public int field(int i) {
		if(i < 0 || i >= fields.length)
			return -1;
		return fields[i];
	}
	
	public int repetitionAccessor(int i) {
		if(i < 0 || i >= repetitionAccessors.length)
			return -1;
		return repetitionAccessors[i];
	}
	
}
