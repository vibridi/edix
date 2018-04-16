package com.vibridi.edix.parser;

import static org.junit.Assert.assertEquals;

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
	public void testManager() throws IOException {
		LoopMatcher ld = LoopDescriptorManager.instance.forTransaction(EDIStandard.ANSI_X12, "110");
		assertEquals(ld.getTransaction(), "110");
		assertEquals(ld.getDescription(), "Air Freight Details and Invoice");
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
	
	// TODO test loops
	@Test
	public void testX12_110() throws Exception {
		X12Interchange x12 = TestResources.getAsX12Interchange("x12-110.edi");
		
		X12TransactionSet ts = x12.getFunctionalGroupt(0).getTransactionSet(0);
				
		EDILoopNode root = ts.getMainLoop();
		//List<EDILoop> list = root.getChildrenImmutable();
		print(root);

	}
	
	public void print(EDILoop loop) {
		if(loop.isLeaf()) {
			System.out.println(loop.getSegment().getName());
			return;
		}
		
		System.out.println("--- new loop ---\t" + loop.getName() + " at lev: "+ (loop.nestingLevel() + 1));		
		if(loop.isRoot()) {
			// 
		} else {
			System.out.println(loop.getSegment().getName());
		}
		
		for(int i = 0; i < loop.getChildren().size(); i++) {
			print(loop.getChildren().get(i));
		}
		System.out.println("--- end loop ---");
	}
	
}
