package com.vibridi.edix.loop;

import java.util.StringJoiner;

public class LoopData {
	
	public static final LoopData NO_DATA = new LoopData("", 0, "");
	
	public final String name;
	public final int level;
	public final String context;
	
	public LoopData(String name, int level, String context) {
		this.name = name;
		this.level = level;
		this.context = context;
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(", ", "[", "]");
		sj.add(name);
		sj.add(Integer.toString(level));
		sj.add(context);
		return sj.toString();
	}
}