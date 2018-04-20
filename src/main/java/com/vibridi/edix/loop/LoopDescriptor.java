package com.vibridi.edix.loop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
 * 
 * @author gabriele.vaccari
 *
 */
public class LoopDescriptor {
	
	private final String name;
	private String description;
	private String levelCode;
	private String startingSegment;
	private Set<String> allowedSegments;
	private Map<String, LoopDescriptor> allowedLoops;			// < name, descriptor >	
	
	public LoopDescriptor(String name) {
		this.name = name;
		this.allowedSegments = new HashSet<>();
		this.allowedLoops = new HashMap<>();
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getLevelCode() {
		return levelCode;
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
	
	public LoopDescriptor getDescriptor(String name) {
		return allowedLoops.get(name);
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public void setStartingSegment(String startingSegment) {
		this.startingSegment = startingSegment;
	}
	
	public void addAllowedSegment(String allowedSegment) {
		this.allowedSegments.add(allowedSegment);
	}
	
	public void addAllowedLoop(LoopDescriptor descriptor) {
		this.allowedLoops.put(descriptor.name, descriptor);
	}
}