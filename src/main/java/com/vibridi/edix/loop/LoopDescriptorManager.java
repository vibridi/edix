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

public class LoopDescriptorManager {
	
	// TODO version value must be last 3 chars of Interchange version number.
	
	private static final String _vbasePath = "/transaction-descriptors/";
	
	private static final Map<String,Map<String,EDILoop>> _vcache; 				// < transactionSet < version, rootLoop > >  
	private static final Map<String,Map<String,LoopDescriptor>> _vhlcache; 		// < transactionSet_version < levelCode, descriptor > >  
	
	static {
		_vcache = new ConcurrentHashMap<>();
		_vhlcache = new ConcurrentHashMap<>();
	}
	
	private final ObjectMapper om;
	private final EDIStandard std;
	private final String transaction;
	private final String version;
	
	private LoopDescriptorManager(EDIStandard std, String transaction, String version) {
		this.om = new ObjectMapper();
		this.std = std;
		this.transaction = transaction;
		this.version = version;
	}
	
	/**
	 * Equivalent to calling forTransaction(standard, transaction, "all");
	 * @param std Reference standard
	 * @param transaction Transaction Set number, e.g. 110
	 * @return The appropriate loop matcher instance
	 * @throws IOException If it fails to load the resource
	 */
	public static EDILoop forTransaction(EDIStandard std, String transaction) throws IOException {
		return forTransaction(std, transaction, "all");
	}
	/**
	 * Returns a constructed instance of the loop descriptor relating to this transaction set.
	 * @param std Reference standard
	 * @param transaction Transaction Set number, e.g. 110
	 * @param version The version id code in GS.8
	 * @return The appropriate loop matcher instance
	 * @throws IOException If it fails to load the resource
	 */
	public static EDILoop forTransaction(EDIStandard std, String transaction, String version) throws IOException {
		return new LoopDescriptorManager(std, transaction, version).load();
	}
	
	public static LoopDescriptor getHLDescriptor(String transaction, String version, String levelCode) {
		String versionKey = _vcache.get(transaction)
				.keySet()
				.stream()
				.filter(version::equals)
				.findFirst()
				.orElse("all");
		
		return _vhlcache.get(String.format("%s_%s", transaction, versionKey)).get(levelCode);
	}
	
	private EDILoop load() throws IOException {
		Map<String,EDILoop> versions = _vcache.get(transaction);
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
				.filter(version::equals)
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
			LoopDescriptor ld = parseDescriptor(version);			
			map.put(versionName, new EDILoopContainer(ld, null));
		}
	
		return map;
	}
	
	private InputStream loadFrom(String fileName) throws IOException {
		return LoopDescriptorManager.class.getResourceAsStream(_vbasePath + fileName);
	}
	
	private LoopDescriptor parseDescriptor(JsonNode node) {
		String name = textOrDefault(node.get("name"), "");
		String desc = textOrDefault(node.get("description"), "");
		String startingSegment = textOrDefault(node.get("startingSegment"), "");
		String levelCode = textOrDefault(node.get("levelCode"), "");
		
		LoopDescriptor ld = new LoopDescriptor(name);
		ld.setDescription(desc);
		ld.setStartingSegment(startingSegment);
		ld.setLevelCode(levelCode);
		
		if(node.has("segments"))
			node.get("segments").forEach(jn -> ld.addAllowedSegment(jn.textValue()));
				
		if(node.has("loops")) {
			JsonNode jsonLoops = node.get("loops");
			for(int i = 0; i < jsonLoops.size(); i++) {
				ld.addAllowedLoop(parseDescriptor(jsonLoops.get(i)));				
			}
		}
		
		// TODO check synchronization
		if(!startingSegment.isEmpty() && !levelCode.isEmpty()) {
			_vhlcache.computeIfAbsent(
					String.format("%s_%s", transaction, version), 
					k -> new HashMap<>()).put(levelCode, ld);
		}
		
		return ld;
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
