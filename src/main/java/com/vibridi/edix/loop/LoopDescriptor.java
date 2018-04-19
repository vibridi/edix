package com.vibridi.edix.loop;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

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
	
	public static final LoopDescriptor EMPTY_DESCRIPTOR = new LoopDescriptor("", "", "", new HashSet<>());
	
	public static final String CURRENT_LOOP = ".";
	public static final String ANY_CONTEXT = "*";
	
	public final String name;
	public final String description;
	public final Optional<String> startingSegment;
	public final Set<String> segments;
	
	public LoopDescriptor(String name, String description, String startingSegment, Set<String> segments) {
		this.name = name;
		this.description = description;
		this.startingSegment = Optional.ofNullable(startingSegment).filter(s -> !s.isEmpty());
		this.segments = segments;
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(", ", "[", "]");
		sj.add(name);
		sj.add(startingSegment.orElse(""));
		return sj.toString();
	}
}