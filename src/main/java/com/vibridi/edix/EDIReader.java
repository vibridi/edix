package com.vibridi.edix;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Map;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.error.ErrorMessages;
import com.vibridi.edix.lexer.AnsiLexer;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;

public abstract class EDIReader implements Closeable {

	private static final int MAX_LOOK_AHEAD = 3; 
	
	protected PushbackReader source;
	protected EDIStandard standard;
	protected EDILexer lexer;
	
	public EDIReader(PushbackReader source) throws IOException {
		this.source = source;
		this.standard = detectStandard();
		this.lexer = EDILexer.newInstance(this.standard, this.source);
	}
	
	public abstract EDIMessage read() throws EDISyntaxException, IOException;
	
	protected EDIStandard detectStandard() throws IOException {
		char[] buf = new char[MAX_LOOK_AHEAD];
		if(source.read(buf) < buf.length)
			throw new RuntimeException(ErrorMessages.LOOK_AHEAD_FAILED.toString());
		source.unread(buf);
		return EDIStandard.of(new String(buf));
	}
	
	public EDILexer getLexer() throws EDISyntaxException, IOException {
		return lexer;
	}
	
	public void setStandard(EDIStandard standard) {
		this.standard = standard;
	}
	
	public EDIStandard getStandard() {
		return standard;
	}
	
	@Override
	public void close() throws IOException {
		source.close();
	}
	
}
