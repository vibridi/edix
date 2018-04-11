package com.vibridi.edix.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.EDINode.EDINodeType;

public abstract class EDIWriter {
	
	@FunctionalInterface
	public interface NodeWalker {
		public void processNode(EDINode node) throws IOException;
	}
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String TAB = "\t";
	
	protected EDIMessage message;
	protected boolean prettyPrint;
	protected String indentation;
	
	public EDIWriter(EDIMessage message) {
		Objects.requireNonNull(message);
		this.message = message;
		this.indentation = TAB;
	}
	
	public abstract void write(OutputStream out, String characterSet) throws EDISyntaxException, IOException;
	
	public void setIndentation(String indentation) {
		this.indentation = indentation;
	}
	
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	protected void walk(NodeWalker delegate) throws IOException {
		walk(delegate, message.getRoot());
	}
	
	private void walk(NodeWalker delegate, EDICompositeNode node) throws IOException {
		delegate.processNode(node);
		
		for(int i = 0; i < node.getChildren().size(); i++) {
			if(node.getChild(i).getNodeType() == EDINodeType.COMPOSITE_NODE) {
				walk(delegate, (EDICompositeNode) node.getChild(i));
			} else {
				delegate.processNode(node.getChild(i));
			}
		}
		
	}
}
