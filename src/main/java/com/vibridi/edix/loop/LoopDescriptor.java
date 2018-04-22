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
	private String code;
	private String startingSegment;
	private Set<String> allowedSegments;
	private Map<String, LoopDescriptor> allowedLoops;			// < name, descriptor >
	private boolean hasHLDescriptors;
	
	public LoopDescriptor(String name) {
		this.name = name;
		this.allowedSegments = new HashSet<>();
		this.allowedLoops = new HashMap<>();
		this.hasHLDescriptors = false;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getCode() {
		return code;
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
	
	public boolean isAmbiguous(String segmentTag) {
		return allowedLoops.keySet()
				.stream()
				.filter(k -> k.startsWith(segmentTag))
				.count() > 1;
	}
	
	protected void setDescription(String description) {
		Objects.requireNonNull(description);
		this.description = description;
	}
	
	protected void setCode(String code) {
		Objects.requireNonNull(code);
		this.code = code;
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
		
		if(!descriptor.getCode().isEmpty())
			key = key.concat("_").concat(descriptor.code);
		
		this.allowedLoops.put(key, descriptor);
	}
}