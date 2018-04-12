package com.vibridi.edix.loop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.loop.LoopDescriptor.Loop;

public enum LoopDescriptorManager {
	instance;
	
	private final Map<String,LoopDescriptor> cache;
	private final ObjectMapper om;
	
	private LoopDescriptorManager() {
		cache = new ConcurrentHashMap<>();
		om = new ObjectMapper();
	}
	
	public LoopDescriptor forName(EDIStandard std, String transaction) throws IOException {
		LoopDescriptor ld = cache.get(transaction);
		if(ld == null) {
			synchronized(cache) {
				ld = cache.get(transaction);
				if(ld == null) {
					ld = loadDescriptor(std, transaction);
					cache.put(transaction, ld);
				}
			}
		}
		return ld;
	}
	
	private LoopDescriptor loadDescriptor(EDIStandard std, String transaction) throws IOException {
		InputStream in = loadFrom(String.format("%s_%s.json", std.name(), transaction));
		JsonNode n = om.readTree(in);
		
		assert(n.get("transactionSet").asText().equals(transaction));
		
		LoopDescriptor ld = new LoopDescriptor(transaction, n.get("description").asText());
		
		JsonNode jsonLoops = n.get("loops");
		Iterator<String> fields = jsonLoops.fieldNames();
		
		while(fields.hasNext()) {
			String name = fields.next();
			JsonNode array = jsonLoops.get(name);
			
			for(int i = 0; i < array.size(); i++) {	
				ld.addLoop(name, new Loop(
						array.get(i).get("name").asText(), 
						array.get(i).get("nestingLevel").asInt(), 
						array.get(i).get("context").asText()));
				
			}			
		}
		
		return ld;
	}
	
	private InputStream loadFrom(String fileName) throws IOException {
		return LoopDescriptorManager.class.getResourceAsStream("/loop-descriptors/" + fileName);
	}
	
}
