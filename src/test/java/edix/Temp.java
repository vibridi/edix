package edix;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

import com.berryworks.edireader.demo.EDItoXML;

public class Temp {

	
	
	
	public void tryout() {
		
		
		EDIParser r = EDIFactory.newParser(EDISourceFormat.PLAIN_EDI | XML)
				.setThis()
				.setThat();
		
		// EDIPlainReader -> EDIReader 
		// EDIXMLReader -> EDIReader
		
		EDIMessage msg = r.read(/* xml source */);
		
		EDIWriter w = EDIFactory.newWriter(EDITargetFormat.X12 | EDIFACT | XML)
				.setThis()
				.setThat();
		
		// AnsiX12Writer -> EDIWriter 
		// EDIFACTWriter  
		// EDIXMLWriter
			
		
		w.write(/* EDIMessage */, target);
		w.write(edistring, target);
		
		
	}
	
	
	
	
}
