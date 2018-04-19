package com.vibridi.edix.loop;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIStandard;

public enum LoopDescriptorManager {
	instance;
	
	private static final String BASE_PATH = "/loop-descriptors/";
	
	private final Map<String,Map<String,EDILoop>> cache; // < transactionSet < version, rootLoop > >  
	private final ObjectMapper om;
	
	private LoopDescriptorManager() {
		cache = new ConcurrentHashMap<>();
		om = new ObjectMapper();
	}
	
	/**
	 * Equivalent to calling forTransaction(standard, transaction, "all");
	 * @param std Reference standard
	 * @param transaction Transaction Set number, e.g. 110
	 * @return The appropriate loop matcher instance
	 * @throws IOException If it fails to load the resource
	 */
	public EDILoop forTransaction(EDIStandard std, String transaction) throws IOException {
		return forTransaction(std, transaction, "all");
	}
	/**
	 * Returns a constructed instance of the loop descriptor relating to this transaction set.
	 * @param std Reference standard
	 * @param transaction Transaction Set number, e.g. 110
	 * @return The appropriate loop matcher instance
	 * @throws IOException If it fails to load the resource
	 */
	public EDILoop forTransaction(EDIStandard std, String transaction, String version) throws IOException {
		Map<String,EDILoop> versions = cache.get(transaction);
		if(versions == null) {
			synchronized(cache) {
				versions = cache.get(transaction);
				if(versions == null) {
					versions = loadDescriptors(std, transaction);
					cache.put(transaction, versions);
				}
			}
		}
		
		String key = versions.keySet()
				.stream()
				.filter(k -> k.contains(version))
				.findFirst()
				.orElse("all");
		
		return versions.get(key);
	}
	
	private Map<String,EDILoop> loadDescriptors(EDIStandard std, String transaction) throws IOException {
		InputStream in = loadFrom(String.format("%s_%s.json", std.name(), transaction));
		JsonNode n = om.readTree(in);
		
		assert(transaction.equals(n.get("transactionSet").asText()));
		
		Map<String,EDILoop> map = new HashMap<>();
				
		JsonNode versions = n.get("versions");
		Iterator<String> versionNames = versions.fieldNames();
		while(versionNames.hasNext()) {
			String versionName = versionNames.next();
			JsonNode version = versions.get(versionName);			
			EDILoop mainLoop = parseDescriptor(null, version);
			map.put(versionName, mainLoop);
		}
	
		return map;
	}
	
	private InputStream loadFrom(String fileName) throws IOException {
		return LoopDescriptorManager.class.getResourceAsStream(BASE_PATH + fileName);
	}
	
	private EDILoop parseDescriptor(EDILoop parent, JsonNode node) {
		
		String name = textOrDefault(node.get("name"), "");
		String desc = textOrDefault(node.get("description"), "");
		String startingSegment = textOrDefault(node.get("startingSegment"), "");
		
		Set<String> segments = new HashSet<>();
		if(node.has("segments"))
			node.get("segments").forEach(jn -> segments.add(jn.textValue()));
		
		EDILoop loop = EDIFactory.newLoop(new LoopDescriptor(name, desc, startingSegment, segments), parent);
		
		if(node.has("loops")) {
			JsonNode jsonLoops = node.get("loops");
			for(int i = 0; i < jsonLoops.size(); i++)
				loop.addChild(parseDescriptor(loop, jsonLoops.get(i)));
		}
		return loop;
	}
	
//	private LoopDescriptor parseLoopData(JsonNode node) {
//		String name = node.get("name").asText();
//		String description = textOrDefault(node.get("description"), "");
//		int nesting = node.get("nestingLevel").asInt();
//		String ctx = node.get("context").asText();
//		JsonNode array = node.get("except");
//		
//		if(!ctx.equals("*") && array != null)
//			throw new IllegalStateException("May specify exceptions only to * contexts");
//		
//		Set<String> exceptions = new HashSet<>();
//		if(array != null)
//			array.forEach(s -> exceptions.add(s.asText()));
//		
//		return new LoopDescriptor(name, description, nesting, ctx, exceptions); 
//	}
	
	private String textOrDefault(JsonNode node, String dft) {
		if(node == null)
			return dft;
		return node.asText();
	}
	
}
