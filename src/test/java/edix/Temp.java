package edix;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDISourceFormat;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class Temp {

	
	
	
	public void tryout() {
		
		
		EDIParser r = EDIFactory.newParser(EDISourceFormat.PLAIN_EDI);
//				.setThis()
//				.setThat();
		
		// EDIPlainReader -> EDIReader 
		// EDIXMLReader -> EDIReader
		
		EDIMessage msg = r.parse(null/* xml source */);
		
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
