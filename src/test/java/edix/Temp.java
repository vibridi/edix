package edix;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.model.impl.EDIMessage;
import com.vibridi.edix.EDIReader;
import com.vibridi.edix.parser.EDIParser;

public class Temp {

	
	
	
	public void tryout() {
		
		
		EDIReader r = EDIFactory.newReader(EDIFormat.PLAIN_TEXT, null /* xml source */);
//				.setThis()
//				.setThat();
		
		// EDIPlainReader -> EDIReader 
		// EDIXMLReader -> EDIReader
		
		EDIMessage msg = r.read();
		
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
