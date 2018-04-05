package com.vibridi.edix;

import java.io.IOException;
import java.io.PushbackReader;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;
import com.vibridi.edix.model.EDIMessage;

public abstract class EDIReader {

	private static final int MAX_LOOK_AHEAD = 3; 
	
	protected PushbackReader source;
	protected EDIStandard standard;
	
	public EDIReader(PushbackReader source) throws IOException {
		this.source = source;
		this.standard = detectStandard();
	}
	
	public abstract EDIMessage read() throws EDISyntaxException, IOException;
	
	protected EDIStandard detectStandard() throws IOException {
		char[] buf = new char[MAX_LOOK_AHEAD];
		if(source.read(buf) < buf.length)
			throw new RuntimeException(ErrorMessages.LOOK_AHEAD_FAILED); // TODO change error msg
		source.unread(buf);
		return EDIStandard.of(new String(buf));
	}
	
	public void setStandard(EDIStandard standard) {
		this.standard = standard;
	}
	
	public EDIStandard getStandard() {
		return standard;
	}
}
