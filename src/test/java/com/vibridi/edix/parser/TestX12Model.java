package com.vibridi.edix.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vibridi.edix.EDIRegistry;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.model.impl.x12.X12FunctionalGroup;
import com.vibridi.edix.model.impl.x12.X12Interchange;
import com.vibridi.edix.model.impl.x12.X12TransactionSet;

public class TestX12Model {

	@Test
	public void createX12Interchange() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/full-envelope.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		assertNotNull(m);
		
		// ISA*00*          *01*PASSWORD00*ZZ*X03400000000108*ZZ*X00450000001001*060424*1244*^*00501*000000017*1*T*>$
		X12Interchange x12 = new X12Interchange(m);
		assertEquals(x12.size(), 2);
		assertEquals(x12.getAuthInfoQualifier(), "00");
		assertEquals(x12.getAuthInfoQualifier(), x12.getISAField(1));
		assertEquals(x12.getAuthInfo(), "          ");
		assertEquals(x12.getAuthInfo(), x12.getISAField(2));
		assertEquals(x12.getSecurityInfoQualifier(), "01");
		assertEquals(x12.getSecurityInfoQualifier(), x12.getISAField(3));
		assertEquals(x12.getSecurityInfo(), "PASSWORD00");
		assertEquals(x12.getSecurityInfo(), x12.getISAField(4));
		assertEquals(x12.getInterchangeIdQualifier(), "ZZ");
		assertEquals(x12.getInterchangeIdQualifier(), x12.getISAField(5));
		assertEquals(x12.getSenderId(), "X03400000000108");
		assertEquals(x12.getSenderId(), x12.getISAField(6));
		assertEquals(x12.getReceiverId(), "X00450000001001");
		assertEquals(x12.getReceiverId(), x12.getISAField(8));
		assertEquals(x12.getDate(), "060424");
		assertEquals(x12.getDate(), x12.getISAField(9));
		assertEquals(x12.getTime(), "1244");
		assertEquals(x12.getTime(), x12.getISAField(10));
		assertEquals(x12.getRepetitionSeparator(), "^");
		assertEquals(x12.getRepetitionSeparator(), x12.getISAField(11));
		assertEquals(x12.getVersionNumber(), "00501");
		assertEquals(x12.getVersionNumber(), x12.getISAField(12));
		assertEquals(x12.getControlNumber(), "000000017");
		assertEquals(x12.getControlNumber(), x12.getISAField(13));
		assertTrue(x12.isAckRequested());
		assertEquals(x12.isAckRequested(), "1".equals(x12.getISAField(14)));
		assertEquals(x12.getUsage(), "T");
		assertEquals(x12.getUsage(), x12.getISAField(15));
		assertEquals(x12.getSubDelimiter(), ">");
		assertEquals(x12.getSubDelimiter(), x12.getISAField(16));
		
		X12FunctionalGroup g = x12.getFunctionalGroup("17");
		assertNotNull(g);
		assertEquals(g.size(), 1);
		X12TransactionSet s = g.getTransactionSet("1234");
		assertNotNull(s);
		assertEquals(s.getIdCode(), "000");
		assertEquals(s.getControlNumber(), "1234");
		
		g = x12.getFunctionalGroup("18");
		assertNotNull(g);
		assertEquals(g.size(), 1);
		s = g.getTransactionSet("1235");
		assertNotNull(s);
		assertEquals(s.getIdCode(), "000");
		assertEquals(s.getControlNumber(), "1235");
	}
	
	@Test
	public void validateX12() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/full-pass.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		assertNotNull(x12);
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12NoISA() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/no-isa.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12NoIEA() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/no-iea.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12WrongIEA() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/wrong-iea.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12NoGS() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/no-gs.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12NoGE() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/no-ge.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12NoST() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/no-st.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12NoSE() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/no-se.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12WrongGS() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/wrong-ge.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	@Test(expected = EDISyntaxException.class)
	public void validateX12WrongSE() throws Exception {
		EDILexer lx = TestResources.getAsLexer("validation-x12/wrong-se.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		EDIMessage m = parser.parse(lx);
		X12Interchange x12 = new X12Interchange(m);
		eat(x12);
		fail();
	}
	
	private static boolean eat(Object o) {
		return o == null;
	}
}
