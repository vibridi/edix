package com.vibridi.edix.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.vibridi.util.StringUtils;

public class TestUtils {

	
	@Test
	public void nthIndexOf() {
		String s = "the quick brown fox moved over the grass";
		// 						^    ^   ^    ^
		
		char[] a = s.toCharArray(); 
		
		assertEquals(StringUtils.nthIndexOf(a, 'o', 1), 12);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 2), 17);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 3), 21);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 4), 26);
		assertEquals(StringUtils.nthIndexOf(a, 'o', 5), -1);	// no 5-th occurrence
	}
	
	@Test
	public void nthIndexOfEdgeCases() {
		char[] a = "".toCharArray(); 
		
		assertEquals(StringUtils.nthIndexOf(a, 'o', 3), -1);
		assertEquals(StringUtils.nthIndexOf(new char[0], 'o', 3), -1);
		try {
			StringUtils.nthIndexOf(null, 'o', 3);
		} catch(NullPointerException e) {
			assertTrue(true);
		}
	}
	
	
	@Test
	public void test() {
//	//	ByteBuffer bb = ByteBuffer.
//		
//		byte b = 0x24;
		char[] g = Character.toChars(0x01D50A);
		//char a = '\uD835';\uDD0A';
//					 
//		System.out.println((char)b);
		System.out.println(String.valueOf(g));
	}
	
}
