package com.vibridi.edix.loop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Describes an EDI loop. 
 * <ul>
 * <li>name: loop name. Normally it matches the segment name. Admits the special character '.' to signal 
 * the return to the parent loop. Practically, this means the loop descriptor is used to break an ongoing loop 
 * instead of starting a new one.</li>
 * <li>level: depth of nesting. 1 is the most shallow level.</li>
 * <li>context: path of nested loops where we can expect to find this loop. Admits the wildcard '*' to signal the 
 * start of a new loop at any path.</li>
 * <li>except: list of exceptions to the wildcard context
 * </ul>
 * TODO rewrite this
 * @author gabriele.vaccari
 *
 */
public class LoopDescriptor {
	
	private final String name;
	private String description;
	private String startingSegment;
	private Set<String> codes;
	private Set<String> allowedSegments;
	private Map<String, LoopDescriptor> allowedLoops;			// < name, descriptor >
	private boolean hasHLDescriptors;
	
	public LoopDescriptor(String name) {
		this.name = name;
		this.allowedSegments = new HashSet<>();
		this.allowedLoops = new HashMap<>();
		this.hasHLDescriptors = false;
		this.codes = new HashSet<>();
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Set<String> getCodes() {
		return codes;
	}

	public String getStartingSegment() {
		return startingSegment;
	}

	public Set<String> getAllowedSegments() {
		return allowedSegments;
	}

	public Map<String, LoopDescriptor> getAllowedLoops() {
		return allowedLoops;
	}
	
	/**
	 * Retrieves a loop descriptor previously stored with an {@code addAllowedLoop} call. 
	 * @param descriptorKey The key is the name of the loop's starting segment, example <code>N1, LX</code>. <br>
	 * In case of ambiguity, the key takes the form form <code>segmentName_code</code>, example <code>HL_20</code>.
	 * @return
	 */
	public LoopDescriptor getDescriptor(String descriptorKey) {
		return allowedLoops.get(descriptorKey);
	}
	
	public boolean hasHLDescriptors() {
		return hasHLDescriptors;
	}
	
	/**
	 * The segment tag is ambiguous if: <br>
	 * 1. there is one or more keys that start with it <br>
	 * 2. there is one or more keys that specify a level code / entity identifier, which empirically evaluates to true
	 * if the tag is followed by the code, e.g. NM1_QC <br>
	 * <br>
	 * Example A: <br>
	 * NM1 for NM1_PR, NM1_QC returns true because both start with NM1 and both specify a code <br>
	 * <br>
	 * Example B: <br>
	 * NM1 for NM1_QC returns true because the key starts with NM1 and specifies a code <br>
	 * <br>
	 * Example C: <br>
	 * NM1 for NM1 returns false because the key starts with NM1 but doesn't specify a code <br>
	 * 
	 * @param segmentTag The segment tag
	 * @return true if the tag is ambiguous, false otherwise
	 */
	public boolean isAmbiguous(String segmentTag) {
		return allowedLoops.entrySet()
				.stream()
				.filter(e -> e.getKey().startsWith(segmentTag))
				.filter(e -> e.getValue().codes.size() > 0)
				.findAny()
				.isPresent();
	}
	
	protected void setDescription(String description) {
		Objects.requireNonNull(description);
		this.description = description;
	}
	
	protected void addCode(String code) {
		if(code != null && !code.isEmpty())
			codes.add(code);
	}

	protected void setStartingSegment(String startingSegment) {
		Objects.requireNonNull(startingSegment);
		this.startingSegment = startingSegment;
	}
	
	protected void addAllowedSegment(String allowedSegment) {
		this.allowedSegments.add(allowedSegment);
	}
	
	/**
	 * Adds this loop descriptor to the list of allowed descriptors. 
	 * @param descriptor
	 */
	protected void addAllowedLoop(LoopDescriptor descriptor) {
		String key = descriptor.getStartingSegment();
		if(key.isEmpty())
			throw new IllegalArgumentException("Adding a loop descriptor that doesn't specify a starting segment");
		
		if(key.equals("HL"))
			this.hasHLDescriptors = true;
		
		if(!descriptor.getCodes().isEmpty()) {
			for(String c : descriptor.getCodes())
				this.allowedLoops.put(key.concat("_").concat(c), descriptor);
			
		} else {
			this.allowedLoops.put(key, descriptor);
		}
			
		
	}
}