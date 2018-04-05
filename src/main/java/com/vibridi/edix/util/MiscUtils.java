package com.vibridi.edix.util;

import java.util.function.Predicate;

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
	
	
}
