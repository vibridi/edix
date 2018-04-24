package com.vibridi.edix.loop;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.loop.impl.EDILoopContainer;

public enum LoopDescriptorManager {
	instance;
	// TODO version value must be last 3 chars of Interchange version number.
	
	private static final String _vbasePath = "/transaction-descriptors/";

	private static final Map<String,Map<String,LoopDescriptor>> _vcache; 				// < transactionSet < version, rootDescriptor > >  
	
	static {
		_vcache = new ConcurrentHashMap<>();
	}
	
	private final ObjectMapper om;
	
	private LoopDescriptorManager() {
		this.om = new ObjectMapper();
	}
	
	/**
	 * Equivalent to calling forTransaction(standard, transaction, "all");
	 * @param std Reference standard
	 * @param transaction Transaction Set number, e.g. 110
	 * @return The appropriate loop matcher instance
	 * @throws IOException If it fails to load the resource
	 */
	public LoopDescriptor forTransaction(EDIStandard std, String transaction) throws IOException {
		return forTransaction(std, transaction, "all");
	}
	/**
	 * Returns a constructed instance of the loop descriptor relating to this transaction set.
	 * @param std Reference standard
	 * @param transaction Transaction Set number, e.g. 110
	 * @param version The version id code in ISA.12
	 * @return The appropriate loop matcher instance
	 * @throws IOException If it fails to load the resource
	 */
	public LoopDescriptor forTransaction(EDIStandard std, String transaction, String version) throws IOException {
		Map<String,LoopDescriptor> versions = _vcache.get(transaction);
		if(versions == null) {
			synchronized(_vcache) {
				versions = _vcache.get(transaction);
				if(versions == null) {
					versions = loadDescriptors(std, transaction);
					_vcache.put(transaction, versions);
				}
			}
		}
		
		String key = versions.keySet()
				.stream()
				.filter(version::endsWith)
				.findFirst()
				.orElse("all");
		
		return versions.get(key);
	}
	
	private Map<String,LoopDescriptor> loadDescriptors(EDIStandard std, String transaction) throws IOException {
		InputStream in = loadFrom(String.format("%s_%s.json", std.name(), transaction));
		JsonNode n = om.readTree(in);
		
		assert(transaction.equals(n.get("transactionSet").asText()));
		
		Map<String,LoopDescriptor> map = new HashMap<>();
				
		JsonNode versions = n.get("versions");
		Iterator<String> versionNames = versions.fieldNames();
		while(versionNames.hasNext()) {
			String versionName = versionNames.next();
			JsonNode version = versions.get(versionName);
			LoopDescriptor ld = parseDescriptor(version);			
			map.put(versionName, ld);
		}
	
		return map;
	}
	
	private InputStream loadFrom(String fileName) {
		return LoopDescriptorManager.class.getResourceAsStream(_vbasePath + fileName);
	}
	
	private LoopDescriptor parseDescriptor(JsonNode node) {
		String name = textOrDefault(node.get("name"), "");
		String desc = textOrDefault(node.get("description"), "");
		String startingSegment = textOrDefault(node.get("startingSegment"), "");
		
		LoopDescriptor ld = new LoopDescriptor(name);
		ld.setDescription(desc);
		ld.setStartingSegment(startingSegment);
		
		if(node.has("codes")) {
			JsonNode codes = node.get("codes");
			for(int i = 0; i < codes.size(); i++)
				ld.addCode(codes.get(i).asText());
		}
		
		if(node.has("segments"))
			node.get("segments").forEach(jn -> ld.addAllowedSegment(jn.textValue()));
				
		if(node.has("loops")) {
			JsonNode jsonLoops = node.get("loops");
			for(int i = 0; i < jsonLoops.size(); i++) {
				ld.addAllowedLoop(parseDescriptor(jsonLoops.get(i)));				
			}
		}
		
		return ld;
	}
	
	private String textOrDefault(JsonNode node, String dft) {
		if(node == null)
			return dft;
		return node.asText();
	}
	
}
