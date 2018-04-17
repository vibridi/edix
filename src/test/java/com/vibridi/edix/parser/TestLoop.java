package com.vibridi.edix.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptorManager;
import com.vibridi.edix.loop.LoopMatcher;
import com.vibridi.edix.loop.impl.EDILoopNode;
import com.vibridi.edix.model.impl.x12.X12Interchange;
import com.vibridi.edix.model.impl.x12.X12TransactionSet;

public class TestLoop {

	@Test
	public void testLoopDescriptorManager() throws IOException {
		LoopMatcher ld = LoopDescriptorManager.instance.forTransaction(EDIStandard.ANSI_X12, "110");
		assertNotNull(ld);
		assertEquals(ld.getTransaction(), "110");
		assertEquals(ld.getDescription(), "Air Freight Details and Invoice");
	}
	
	@Test
	public void testLoopMatcher() throws IOException {
		LoopMatcher ld = LoopDescriptorManager.instance.forTransaction(EDIStandard.ANSI_X12, "110");
		assertEquals(ld.getNames().size(), 5);
		assertEquals(ld.sizeOf("N1"), 2);
		assertEquals(ld.sizeOf("LX"), 1);
		assertEquals(ld.sizeOf("L1"), 1);
		assertEquals(ld.sizeOf("L3"), 1);
		assertEquals(ld.sizeOf("L5"), 1);
		assertEquals(ld.sizeOf("P1"), 0);
		assertEquals(ld.get("N1", 0).name, "N1");
		assertEquals(ld.get("N1", 0).level, 1);
		assertEquals(ld.get("N1", 0).context, "*");
		assertEquals(ld.get("L1", 0).name, "L1");
		assertEquals(ld.get("L1", 0).level, 3);
		assertEquals(ld.get("L1", 0).context, "*");
	}
	
	@Test
	public void testLoopTree() throws Exception {
		EDILoopNode root = 
		TestResources.getAsX12Interchange("transactions-x12/110.edi")
			.getFunctionalGroupt(0)
			.getTransactionSet(0)
		 	.getMainLoop();
		
		assertTrue(root instanceof EDILoopNode);
		
		// Test ST leaves
		assertEquals(root.getChildren().get(0).getSegment().getName(), "B3");
		assertTrue(root.getChildren().get(0).isLeaf());
		assertEquals(root.getChildren().get(1).getSegment().getName(), "B3A");
		assertTrue(root.getChildren().get(1).isLeaf());
		
		// Test ST level 1 loops
		EDILoop n1 = root.getChildren().get(2);
		assertTrue(n1 instanceof EDILoopNode);
		assertEquals(n1.getName(), "N1");
		assertEquals(n1.nestingLevel(), 1);
		assertEquals(n1.getChildren().size(), 4);
		assertTrue(n1.getChildren().get(0).isLeaf());
		
		n1 = root.getChildren().get(3);
		assertTrue(n1 instanceof EDILoopNode);
		assertEquals(n1.getName(), "N1");
		
		n1 = root.getChildren().get(4);
		assertTrue(n1 instanceof EDILoopNode);
		assertEquals(n1.getName(), "N1");
		
		EDILoop lx = root.getChildren().get(5);
		assertTrue(lx instanceof EDILoopNode);
		assertEquals(lx.getName(), "LX");
		assertEquals(lx.nestingLevel(), 1);
		
		// Test loop starting segment is stored in the loop node itself
		assertEquals(lx.getSegment().getName(), "LX");
		
		// Test ST level 2 loops
		EDILoop l5 = lx.getChildren().get(3);
		assertTrue(l5 instanceof EDILoopNode);
		assertEquals(l5.getName(), "L5");
		assertEquals(l5.nestingLevel(), 2);
		assertTrue(l5.getChildren().get(5).isLeaf());
		assertEquals(l5.getChildren().get(5).getName(), "SL1");
		
		// Test ST level 3 loops
		EDILoop l1 = l5.getChildren().get(6);
		assertTrue(l1 instanceof EDILoopNode);
		assertEquals(l1.getName(), "L1");
		assertEquals(l1.nestingLevel(), 3);
		
		// Test loop with only one segment
		assertTrue(l1.getChildren().isEmpty());
		
		l1 = l5.getChildren().get(7);
		assertTrue(l1 instanceof EDILoopNode);
		assertEquals(l1.getName(), "L1");
		assertEquals(l1.nestingLevel(), 3);
		
		// Test ST leaves after deeply nested loops
		assertEquals(root.getChildren().get(6).getSegment().getName(), "L3");
		assertTrue(root.getChildren().get(6).isLeaf());
		assertEquals(root.getChildren().get(7).getSegment().getName(), "NTE");
		assertTrue(root.getChildren().get(7).isLeaf());
	}
	
}
