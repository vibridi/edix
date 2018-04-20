package com.vibridi.edix.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.loop.LoopDescriptorManager;
import com.vibridi.edix.loop.impl.EDILoopNode;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.impl.EDICompositeNodeImpl;
import com.vibridi.edix.writer.EDIXMLWriter;

public class TestLoop {

	@Test
	public void testLoopDescriptorManager() throws IOException {
		EDILoop ld = LoopDescriptorManager.instance.forTransaction(EDIStandard.ANSI_X12, "110");
		assertNotNull(ld);
		assertTrue(ld.isRoot());
		assertEquals(ld.getName(), "110");
		assertEquals(ld.getDescription().get(), "Air Freight Details and Invoice");
	}
	
	@Test
	public void testLoopDescriptorVersion() throws IOException {
		EDILoop ld = LoopDescriptorManager.instance.forTransaction(EDIStandard.ANSI_X12, "278");
		assertNotNull(ld);
		assertTrue(ld.isRoot());
		assertEquals(ld.getName(), "278");
		assertEquals(ld.getDescription().get(), "Health Care Services Review Information");
	}
	
	@Test
	public void testLoopDescriptor() throws IOException {
		EDILoop ld = LoopDescriptorManager.instance.forTransaction(EDIStandard.ANSI_X12, "110");
		assertEquals(ld.getChildren().size(), 0);
		assertTrue(ld.allowsSegment("B3"));
		assertTrue(ld.allowsSegment("B3A"));
		assertTrue(ld.allowsSegment("C2"));
		assertTrue(ld.allowsSegment("C3"));
		assertTrue(ld.allowsSegment("ITD"));
		assertTrue(ld.allowsSegment("L3"));
		assertTrue(ld.allowsSegment("ACS"));
		assertTrue(ld.allowsSegment("NTE"));
		assertTrue(ld.allowsLoop("N1"));
		assertTrue(ld.allowsLoop("LX"));
	}
	
	@Test
	public void testLoopTree() throws Exception {
		EDILoop root = 
		TestResources.getAsX12Interchange("transactions-x12/110.edi")
			.getFunctionalGroupt(0)
			.getTransactionSet(0)
		 	.getMainLoop();
		
		assertTrue(root instanceof EDILoopNode);
		
		// Test ST leaves
		assertEquals(root.getChildren().get(0).getName(), "B3");
		assertTrue(root.getChildren().get(0).isTerminal());
		assertEquals(root.getChildren().get(1).getName(), "B3A");
		assertTrue(root.getChildren().get(1).isTerminal());
		
		// Test ST level 1 loops
		EDILoop n1 = root.getChildren().get(2);
		assertTrue(n1 instanceof EDILoopNode);
		assertEquals(n1.getName(), "N1");
		assertEquals(n1.getChildren().size(), 5);
		assertTrue(n1.getChildren().get(0).isTerminal());
		
		n1 = root.getChildren().get(3);
		assertTrue(n1 instanceof EDILoopNode);
		assertEquals(n1.getName(), "N1");
		
		n1 = root.getChildren().get(4);
		assertTrue(n1 instanceof EDILoopNode);
		assertEquals(n1.getName(), "N1");
		
		EDILoop lx = root.getChildren().get(5);
		assertTrue(lx instanceof EDILoopNode);
		assertEquals(lx.getName(), "LX");
		
		// Test loop starting segment is loop node's first child
		assertEquals(lx.getChildren().get(0).getName(), "LX");
		
		// Test ST level 2 loops
		EDILoop l5 = lx.getChildren().get(4);
		assertTrue(l5 instanceof EDILoopNode);
		assertEquals(l5.getName(), "L5");
		assertTrue(l5.getChildren().get(4).isTerminal());
		assertEquals(l5.getChildren().get(6).getName(), "SL1");
		
		EDILoop l1 = lx.getChildren().get(5);
		assertTrue(l1 instanceof EDILoopNode);
		assertEquals(l1.getName(), "L1");
		
		// Test loop with only one segment
		assertTrue(l1.getChildren().size() == 1);
		
		l1 = lx.getChildren().get(6);
		assertTrue(l1 instanceof EDILoopNode);
		assertEquals(l1.getName(), "L1");
		
		// Test ST leaves after deeply nested loops
		assertEquals(root.getChildren().get(6).getName(), "L3");
		assertTrue(root.getChildren().get(6).isTerminal());
		assertEquals(root.getChildren().get(7).getName(), "NTE");
		assertTrue(root.getChildren().get(7).isTerminal());
	}
	
	@Test
	public void testPrint() throws Exception {
		EDIMessage m = TestResources.getAsMessage("transactions-x12/110.edi");
		EDIXMLWriter w = (EDIXMLWriter) EDIFactory.newWriter(EDIFormat.XML, m);
		String xml = w.writeToString("UTF-8");
		System.out.println(xml);
	}
	
}
