package com.vibridi.edix;

import java.io.IOException;
import java.io.PushbackReader;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDIMessage;

public class EDIXMLReader extends EDIReader {

	public EDIXMLReader(PushbackReader source) throws IOException {
		super(source);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EDIMessage read() throws EDISyntaxException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
