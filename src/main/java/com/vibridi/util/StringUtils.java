package com.vibridi.util;

import java.util.Objects;

public class StringUtils {

	/**
	 * Finds the n-th occurrence of a given character in a string
	 * @param buffer char array representing a string
	 * @param c character 
	 * @param n n-th occurrence
	 * @return index of the n-th occurrence
	 */
	public static int nthIndexOf(char[] buffer, char c, int n) {
		Objects.requireNonNull(buffer, "Passing a null char array.");
		
        int result = -1;
        int count = 0;
        
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] == c && ++count == n) {
            	return i;
            }
        }
        return result;
	}
	
	
	
}
