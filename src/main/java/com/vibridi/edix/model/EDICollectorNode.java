package com.vibridi.edix.model;

public interface EDICollectorNode<T extends EDINode> {
	public T appendChild(T newChild);
}
