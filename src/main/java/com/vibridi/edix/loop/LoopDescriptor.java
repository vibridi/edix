package com.vibridi.edix.loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.vibridi.edix.model.EDICompositeNode;

public class LoopDescriptor {

	private final String transaction; 				// 110
	private final String text; 						// "Air Freight Details and Invoice"
	private final Map<String,List<LoopData>> loops;
	
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
	
	public void addLoop(String name, LoopData loop) {
		loops.computeIfAbsent(name, k -> new ArrayList<>()).add(loop);
	}
	
	public Set<String> getNames() {
		return loops.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public int sizeOf(String name) {
		return loops.getOrDefault(name, Collections.EMPTY_LIST).size();
	}
	
	public LoopData get(String name, int i) {
		return loops.get(name).get(i);
	}

	public Optional<EDILoop> getMatchFor(EDICompositeNode seg, EDILoop currentLoop) {
		List<LoopData> candidates = loops.get(seg.getName()); 
		if(candidates == null)
			return Optional.ofNullable(null);
		
		for(LoopData data : candidates) {
			if(data.context.equals("*") || data.context.equals(currentLoop.toString()))
				return Optional.of(new EDILoop(data, seg, currentLoop));
		}
		
		return Optional.ofNullable(null);
	}
	
}
