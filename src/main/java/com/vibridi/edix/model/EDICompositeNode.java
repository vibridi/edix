package com.vibridi.edix.model;

public interface EDICompositeNode extends EDITextNode {
	public EDINode appendChild(EDINode newChild);
	public int fields();
	public boolean isRoot();
	
	public interface NodeWalker {
		public void processNode(EDINode node);
	}
	
	default public void walk(NodeWalker delegate) {
		delegate.processNode(this);
		
		for(int i = 0; i < getChildren().size(); i++) {
			if(getChild(i).getNodeType() == EDINodeType.COMPOSITE_NODE) {
				((EDICompositeNode) getChild(i)).walk(delegate);
			} else {
				delegate.processNode(getChild(i));
			}
		}
		
	}
}
