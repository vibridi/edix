package com.vibridi.edix.loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.vibridi.edix.loop.impl.EDILoopNode;
import com.vibridi.edix.model.EDICompositeNode;

public class LoopMatcher {
	
	public final class LoopMatch {
		public final boolean matches;
		public final LoopDescriptor data;
		
		public LoopMatch(LoopDescriptor data) {
			this.data = data;
			this.matches = data != null;
		}
	}

	private final String transaction; 				// 110
	private final String text; 						// "Air Freight Details and Invoice"
	private final Map<String,List<LoopDescriptor>> loops;
	
	public LoopMatcher(String transaction, String text) {
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
	
	public void addLoopData(String name, LoopDescriptor loop) {
		loops.computeIfAbsent(name, k -> new ArrayList<>()).add(loop);
	}
	
	public Set<String> getNames() {
		return loops.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public int sizeOf(String name) {
		return loops.getOrDefault(name, Collections.EMPTY_LIST).size();
	}
	
	public LoopDescriptor get(String name, int i) {
		return loops.get(name).get(i);
	}

	public Optional<EDILoopNode> getMatchFor(EDICompositeNode seg, EDILoopNode currentLoop) {
		List<LoopDescriptor> candidates = loops.get(seg.getName()); 
		if(candidates == null)
			return Optional.ofNullable(null);
		
		for(LoopDescriptor data : candidates) {
			if(data.context.equals("*") || data.context.equals(currentLoop.toString()))
				return Optional.of(new EDILoopNode(data, seg, currentLoop));
		}
		
		return Optional.ofNullable(null);
	}
	
	public boolean hasMatch(EDICompositeNode seg, String currentLoopPath) {
		List<LoopDescriptor> candidates = loops.get(seg.getName()); 
		if(candidates == null)
			return false;
		
		for(LoopDescriptor data : candidates) {
			if(data.context.equals("*")) {
				return !data.exceptions.contains(currentLoopPath);
			}
			
			if(data.context.equals(currentLoopPath)) {
				return true;
			}
		}
		
		return false;
	}
	
	public LoopMatch findMatch(EDICompositeNode seg, String currentLoopPath) {
		List<LoopDescriptor> candidates = loops.get(seg.getName()); 
		if(candidates == null)
			return new LoopMatch(null);
		
		for(LoopDescriptor data : candidates) {
			if(data.context.equals(LoopDescriptor.ANY_CONTEXT)) {
				return new LoopMatch(
						data.exceptions.contains(currentLoopPath) 
							? null 
							: data);
			}
			
			if(data.context.equals(currentLoopPath)) {
				return new LoopMatch(data);
			}
		}
		
		return new LoopMatch(null);
	}
	
}
