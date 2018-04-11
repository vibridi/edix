package com.vibridi.edix.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;

public class EDIPlainWriter extends EDIWriter {

	private char[] controls;
	
	public EDIPlainWriter(EDIMessage message) {
		super(message);
		controls = new char[8];
		controls[0] = '\0';
		
		TokenType[] tt = message.getControlCharacters();
		for(int i = 0; i < tt.length; i++) {
			switch(tt[i]) {
			case TERMINATOR:
				controls[7] = (char)i;
				break;
			
			case DELIMITER:
				controls[1] = (char)i;
				break;
				
			case SUB_DELIMITER:
				controls[2] = (char)i;
				break;
			
			case SUB_SUB_DELIMITER:
				controls[3] = (char)i;
				break;
				
			case REPETITION_SEPARATOR:
				controls[4] = (char)i;
				break;
				
			default:
				break;
			
			}
			
		}
		
	}

	@Override
	public void write(OutputStream out, String characterSet) throws IOException {
		OutputStreamWriter w = new OutputStreamWriter(out, characterSet);

		for(int i = 0; i < message.getRoot().getChildren().size(); i++) {
			EDICompositeNode segment = (EDICompositeNode) message.getRoot().getChild(i);
			w.write(segment.toString());
			w.write(controls[7]);
			if(prettyPrint)
				w.write(LINE_SEPARATOR);
		}
		
		w.close();
	}

}
