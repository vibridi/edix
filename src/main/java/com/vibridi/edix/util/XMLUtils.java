package com.vibridi.edix.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLUtils {
	
	private static Map<String,XPathExpression> expressionsCache;

	static {
		expressionsCache = new HashMap<String, XPathExpression>();
	}
	
	/**
	 * Takes the XML string and produces a Document object
	 * @param xml XML text
	 * @return the DOM representation of the given string
	 * @throws XMLException if the transformation fails
	 */
	public static Document stringToDocument(String xml) throws IOException {
		if (xml == null || xml.trim().length() == 0)
			return null;

		try {
			Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(xml.getBytes()));
			xmlDoc.normalize();
			return xmlDoc;

		} catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Applies the given XPath on the document object.
	 * Namespace awareness should be set on the <code>Document</code> object.
	 * 
	 * @param doc Source DOM
	 * @param request XPath directive
	 * @param ctx Namespace context, nullable
	 * @param returnType An <code>XPathConstants</code> element
	 * @return Object obtained from running the given XPath on the DOM
	 * @throws XPathExpressionException 
	 * @throws XMLException if the transformation fails
	 */	
	public static Object applyXPath(Document doc, String request, NamespaceContext ctx, QName returnType) throws XPathExpressionException {
		return applyXPath(doc.getDocumentElement(), request, ctx, returnType);
	}
	
	public static Object applyXPath(Node node, String request, NamespaceContext ctx, QName returnType) throws XPathExpressionException {
		if (request == null)
			return null;
		
		XPathExpression expr = expressionsCache.get(request);
		if(expr == null) {
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			if(ctx != null)
				xpath.setNamespaceContext(ctx);
			
			expr = xpath.compile(request);
			expressionsCache.put(request, expr);
		}
		return expr.evaluate(node, returnType);
	}

}
