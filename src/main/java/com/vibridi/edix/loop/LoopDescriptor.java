package com.vibridi.edix.loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoopDescriptor {

	public static class Loop {
		public final String name;
		public final int level;
		public final String context;
		
		public Loop(String name, int level, String context) {
			this.name = name;
			this.level = level;
			this.context = context;
		}
	}
	
	
	private final String transaction; 				// 110
	private final String text; 						// "Air Freight Details and Invoice"
	private final Map<String,List<Loop>> loops;
	
	public LoopDescriptor(String transaction, String text) {
		this.transaction = transaction;
		this.text = text;
		this.loops = new HashMap<>();
	}
	
	public String getTransaction() {
		return transaction;
	}
	
	public String getDescription() {
		return text;
	}
	
	public void addLoop(String name, Loop loop) {
		loops.computeIfAbsent(name, k -> new ArrayList<>()).add(loop);
	}
	
	public Set<String> getNames() {
		return loops.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public int sizeOf(String name) {
		return loops.getOrDefault(name, Collections.EMPTY_LIST).size();
	}
	
	public Loop get(String name, int i) {
		return loops.get(name).get(i);
	}
	
}
