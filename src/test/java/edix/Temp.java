package edix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.vibridi.edix.EDIFactory;
import com.vibridi.edix.EDIFactory.EDIFormat;
import com.vibridi.edix.EDIReader;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.writer.EDIWriter;

public class Temp {

	
	
	
	public void tryout() throws EDISyntaxException, IOException {
		
		
		EDIReader r = EDIFactory.newReader(EDIFormat.PLAIN_TEXT, null /* xml source */);
//				.setThis()
//				.setThat();
		
		// EDIPlainReader -> EDIReader 
		// EDIXMLReader -> EDIReader
		
		EDIMessage msg = r.read();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EDIWriter w = EDIFactory.newWriter(EDIFormat.PLAIN_TEXT, message);
//				.setThis()
//				.setThat();
		
		// AnsiX12Writer -> EDIWriter 
		// EDIFACTWriter  
		// EDIXMLWriter
			
		
		w.write(new OutputStreamWriter(out));
		//w.write(edistring, target);
		
		
	}
	
	
	
	
}
