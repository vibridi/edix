package com.vibridi.edix.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;

import org.w3c.dom.Document;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.TokenType;
import com.vibridi.edix.loop.EDILoop;
import com.vibridi.edix.model.EDICompositeNode;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.EDINode;
import com.vibridi.edix.model.EDINode.EDINodeType;
import com.vibridi.edix.model.impl.x12.X12FunctionalGroup;
import com.vibridi.edix.model.impl.x12.X12Interchange;
import com.vibridi.edix.model.impl.x12.X12TransactionSet;

public class EDIXMLWriter extends EDIWriter {
	
	public EDIXMLWriter(EDIMessage message) {
		super(message);
	}

	@Override
	public void write(OutputStream out, String characterSet) throws EDISyntaxException, IOException {		
		XMLOutputFactory outf = XMLOutputFactory.newInstance();
		XMLEventFactory evf = XMLEventFactory.newInstance();
		//fac.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
		
		try {
			XMLStreamWriter writer = outf.createXMLStreamWriter(out, characterSet);
			
			if(message.getStandard() != EDIStandard.ANSI_X12)
				throw new UnsupportedOperationException("Can write only ANSI X12 messages"); // TODO
			
			X12Interchange interchange = new X12Interchange(message);
			
			writer.writeStartDocument(characterSet, "1.0");
			writer.writeStartElement("Interchange");
			writer.writeAttribute("standard", message.getStandard().name());

			writer.writeStartElement("InterchangeControlHeader");
			writer.writeAttribute("type", message.getRoot().getChild(0).getName());
			writer.writeAttribute("terminator", getControlCharacter(message.getControlCharacters(), TokenType.TERMINATOR));
						
			appendElement(writer, "AuthorizationInformationQualifier", interchange.getAuthInfoQualifier());
			appendElement(writer, "AuthorizationInformation", interchange.getAuthInfo());
			appendElement(writer, "SecurityInformationQualifier", interchange.getSecurityInfoQualifier());
			appendElement(writer, "SecurityInformation", interchange.getSecurityInfo());
			appendElement(writer, "InterchangeIdQualifier", interchange.getInterchangeIdQualifier());
			appendElement(writer, "SenderId", interchange.getSenderId());
			appendElement(writer, "InterchangeIdQualifier", interchange.getInterchangeIdQualifier());
			appendElement(writer, "ReceiverId", interchange.getReceiverId());
			appendElement(writer, "Date", interchange.getDate(), evf.createAttribute("format", "yyMMdd"));
			appendElement(writer, "Time", interchange.getTime(), evf.createAttribute("format", "HHmm"));
			appendElement(writer, "RepetitionSeparator", interchange.getRepetitionSeparator());
			appendElement(writer, "VersionNumber", interchange.getVersionNumber());
			appendElement(writer, "ControlNumber", interchange.getControlNumber());
			appendElement(writer, "AckRequested", interchange.getISAField(14));
			appendElement(writer, "TestIndicator", interchange.getUsage());
			appendElement(writer, "SubDelimiter", interchange.getSubDelimiter());
			
			writer.writeEndElement();			// InterchangeControlHeader
			
			for(int i = 0; i < interchange.size(); i++) {
				X12FunctionalGroup group = interchange.getFunctionalGroupt(i);
				
				writer.writeStartElement("FunctionalGroup");
				writer.writeAttribute("code", group.getGroupHeaderCode());								// GS.1
				writer.writeAttribute("controlNumber", group.getGroupControlNumber());					// GS.6
				
				appendElement(writer, "ApplicationSenderCode", group.getApplicationSenderCode()); 		// GS.2
				appendElement(writer, "ApplicationReceiverCode", group.getApplicationReceiverCode());	// GS.3
				appendElement(writer, "Date", group.getDate());											// GS.4
				appendElement(writer, "Time", group.getTime());											// GS.5
				appendElement(writer, "ResponsibleAgency", group.getResponsibleAgencyCode());			// GS.7
				appendElement(writer, "VersionIdCode", group.getVersionIdCode());						// GS.8
				
				for(int j = 0; j < group.size(); j++) {
					X12TransactionSet set = group.getTransactionSet(j);
					
					writer.writeStartElement("TransactionSet");
					writer.writeAttribute("code", set.getIdCode());						// ST.1
					writer.writeAttribute("description", set.getDescription());
					writer.writeAttribute("controlNumber", set.getControlNumber());		// ST.2
					
					writeLoop(writer, set.getMainLoop());
					
					writer.writeEndElement();	// TransactionSet
				}
				
				writer.writeEndElement();		// FunctionalGroup
			}
			
			
			writer.writeEndElement(); 			// Interchange
			writer.writeEndDocument();
			writer.close();
			
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
	}
	
	public Document writeToDOM(String characterSet) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(out, characterSet);
		return DocumentBuilderFactory.newInstance()
			.newDocumentBuilder()
			.parse(new ByteArrayInputStream(out.toByteArray()));
	}
	
	public String writeToString(String characterSet) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(out, characterSet);
		return new String(out.toByteArray(), characterSet);
	}
	
	private void writeLoop(XMLStreamWriter writer, EDILoop loop) throws XMLStreamException {
		EDICompositeNode seg = loop.getSegment();
		
		if(loop.isLeaf()) {	
			writeSegment(writer, seg);
			return;
		}
		
		if(loop.isRoot()) {
			for(int i = 0; i < loop.getChildren().size(); i++) {
				writeLoop(writer, loop.getChildren().get(i));
			}
		} else {
			writer.writeStartElement("Loop");
			writer.writeAttribute("id", loop.getName());
			if(!loop.getDescription().isEmpty())
				writer.writeAttribute("description", loop.getDescription());
			
			writeSegment(writer, loop.getSegment());
			for(int i = 0; i < loop.getChildren().size(); i++) {
				writeLoop(writer, loop.getChildren().get(i));
			}
			writer.writeEndElement(); // end loop
		}
	}
	
	private void writeNode(XMLStreamWriter writer, EDINode node) throws XMLStreamException {
		if(node.getNodeType() == EDINodeType.TEXT_NODE) {
			writer.writeCharacters(node.getTextContent());
			return;
		}
		
		if(((EDICompositeNode) node).isRepeated())
			writer.writeAttribute("repeated", "true");

		for(int i = 0; i < node.getChildren().size(); i++) {
			writer.writeStartElement("Field");
			writer.writeAttribute("id", String.format("%02d", (i+1)));
			writeNode(writer, node.getChild(i));
			writer.writeEndElement(); // Field
		}		
	}
	
	private void writeSegment(XMLStreamWriter writer, EDICompositeNode segment) throws XMLStreamException {
		writer.writeStartElement("Segment");
		writer.writeAttribute("id", segment.getName());
		writeNode(writer, segment);
		writer.writeEndElement();	// Segment
	}
	
	private void appendElement(XMLStreamWriter writer, String tag, String content) throws XMLStreamException {
		writer.writeStartElement(tag);
		writer.writeCharacters(content);
		writer.writeEndElement();
	}
	
	private void appendElement(XMLStreamWriter writer, String tag, String content, Attribute... attributes) throws XMLStreamException {
		writer.writeStartElement(tag);
		for(Attribute attr : attributes)
			writer.writeAttribute(attr.getName().getLocalPart(), attr.getValue());
		writer.writeCharacters(content);
		writer.writeEndElement();
	}
	
	private String getControlCharacter(TokenType[] cchars, TokenType type) {
		for(int i = 0; i < cchars.length; i++) {
			if(cchars[i] == type)
				return "" + (char) i;
		}
		return "";
	}
}
