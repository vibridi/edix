package com.vibridi.edix.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.loop.LoopDescriptor;
import com.vibridi.edix.loop.LoopDescriptorManager;

public class TestLoop {

	@Test
	public void testManager() throws IOException {
		LoopDescriptor ld = LoopDescriptorManager.instance.forName(EDIStandard.ANSI_X12, "110");
		assertEquals(ld.getTransaction(), "110");
		assertEquals(ld.getDescription(), "Air Freight Details and Invoice");
		assertEquals(ld.getNames().size(), 6);
		assertEquals(ld.sizeOf("N1"), 3);
		assertEquals(ld.sizeOf("LX"), 1);
		assertEquals(ld.sizeOf("L1"), 1);
		assertEquals(ld.sizeOf("L3"), 1);
		assertEquals(ld.sizeOf("L5"), 1);
		assertEquals(ld.sizeOf("P1"), 1);
		assertEquals(ld.get("N1", 0).name, "N1");
		assertEquals(ld.get("N1", 0).level, 1);
		assertEquals(ld.get("N1", 0).context, "/");
		assertEquals(ld.get("L1", 0).name, "L1");
		assertEquals(ld.get("L1", 0).level, 3);
		assertEquals(ld.get("L1", 0).context, "*");
	}
	
	
}
