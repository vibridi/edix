package com.vibridi.edix.util;

import java.util.function.Predicate;

import com.vibridi.edix.model.EDICompositeNode;

public class MiscUtils {

	@SafeVarargs
	public static <T> T coalesceNotNull(T... args) {
		return coalesce(a -> a != null, args);
	}
	
	@SafeVarargs
	public static <T> T coalesce(Predicate<T> predicate, T... args) {
		for(T arg : args) {
			if(predicate.test(arg))
				return arg;
		}
		return args[args.length - 1];
	}
	
	
	public static String getDescriptorKey(EDICompositeNode segment) {
		String name = segment.getName();
		String code = null;
		switch(name) {
		case "HL":
			code = segment.getChild(2).getTextContent();
			break;
			
		case "N1":
			code = segment.getChild(0).getTextContent();
			break;
		
		case "NM1":
			code = segment.getChild(0).getTextContent();
			break;
		
		default:
			// do nothing
			break;
		}
		
		return code != null
				? String.format("%s_%s", name, code)
				: name;
	}
	
}
