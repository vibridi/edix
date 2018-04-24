package com.vibridi.edix.parser;

import org.junit.Test;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.writer.EDIXMLWriter;

public class TestNonSpecificParsing {

	
	
	@Test
	public void parse820() throws Exception {
//		EDILoop root = 
//		TestResources.getAsX12Interchange("transactions-x12/_820/Example1_MortgageBankers.edi.edi")
//			.getFunctionalGroup(0)
//			.getTransactionSet(0)
//		 	.getMainLoop();
//		
		
	}
	
	@Test
	public void testPrint() throws Exception {
		EDIMessage m = TestResources.getAsMessage("transactions-x12/_820/Example1_MortgageBankers.edi");
		EDIXMLWriter w = (EDIXMLWriter) EDIFactory.newWriter(EDIFormat.XML, m);
		String xml = w.writeToString("UTF-8");
		System.out.println(xml);
	}
	
}
