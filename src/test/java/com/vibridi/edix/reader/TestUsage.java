package com.vibridi.edix.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.EDIReader;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.writer.EDIWriter;

public class TestUsage {
	public void tryout() throws EDISyntaxException, IOException {
		
		// ALTERNATIVE
//		X12Simple x12 = (X12Simple) new X12SimpleParser().parse(new File("C:\\test\\835.txt"));
//		for (Segment s : x12)
//		{
//		        if (s.getElement(0).equals("CLP")) {
//		                System.out.println("Total Change Amount " + s.getElement(3));
//		        }
//		}
		
		EDIReader r = EDIFactory.newReader(EDIFormat.PLAIN_TEXT, null /* xml source */);
//				.setThis()
//				.setThat();
		
		// EDIPlainReader -> EDIReader 
		// EDIXMLReader -> EDIReader
		
		EDIMessage msg = r.read();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, msg);
//				.setThis()
//				.setThat();
		
		// AnsiX12Writer -> EDIWriter 
		// EDIFACTWriter  
		// EDIXMLWriter
			
		
	//	w.write(new OutputStreamWriter(out));
		//w.write(edistring, target);
		
		
	}
}
