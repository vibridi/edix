package com.vibridi.edix.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vibridi.edix.EDIRegistry;
import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.TestResources;
import com.vibridi.edix.error.EDIPathException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;
import com.vibridi.edix.parser.EDIParser;

public class TestEDIPath {

	private EDIMessage m;
	
	@Test
	public void simplePath() throws EDIPathException {
		EDIPath p = EDIPath.of("GS");
		assertEquals(p.segment(), "GS");
		assertEquals(p.ordinal(), 0);
		assertEquals(p.fields(), 0);
	}
	
	@Test
	public void longPath() throws EDIPathException {
		EDIPath p = EDIPath.of("GS.2.8.1.12");
		assertEquals(p.segment(), "GS");
		assertEquals(p.ordinal(), 0);
		assertEquals(p.fields(), 4);
		
		// Verifies that the path is parsed correctly and that indices are 1-based
		assertEquals(p.field(0), 1);
		assertEquals(p.field(1), 7);
		assertEquals(p.field(2), 0);
		assertEquals(p.field(3), 11);
	}
	
	@Test(expected = EDIPathException.class)
	public void refuses0IndexedPath() throws EDIPathException {
		EDIPath.of("GS.0.1");
		fail();
	}
	
	@Test
	public void segmentAccessor() throws EDIPathException {
		EDIPath p = EDIPath.of("GS[1]");
		assertEquals(p.segment(), "GS");
		assertEquals(p.ordinal(), 0);
		assertEquals(p.fields(), 0);
		
		p = EDIPath.of("GS[4]");
		assertEquals(p.segment(), "GS");
		assertEquals(p.ordinal(), 3);
		assertEquals(p.fields(), 0);
	}
	
	@Test(expected = EDIPathException.class)
	public void refuses0IndexedSegmentAccessor() throws EDIPathException {
		EDIPath.of("GS[0]");
		fail();
	}
	
	@Test
	public void segmentAccessorWithLongPath() throws EDIPathException {
		EDIPath p = EDIPath.of("GS[3].2.4");
		assertEquals(p.segment(), "GS");
		assertEquals(p.ordinal(), 2);
		assertEquals(p.fields(), 2);
		assertEquals(p.field(0), 1);
		assertEquals(p.field(1), 3);
	}
	
	@Test
	public void repetitionAccessor() throws EDIPathException {
		EDIPath p = EDIPath.of("GS[3].2[1].4[3]");
		assertEquals(p.segment(), "GS");
		assertEquals(p.ordinal(), 2);
		assertEquals(p.fields(), 2);
		assertEquals(p.field(0), 1);
		assertEquals(p.field(1), 3);
		assertEquals(p.repetitionAccessor(0), 0);
		assertEquals(p.repetitionAccessor(1), 2);
	}
	
	@Test(expected = EDIPathException.class)
	public void refuses0IndexedRepetitionAccessor() throws EDIPathException {
		EDIPath.of("GS[3].2[0].4[3]");
		fail();
	}
	
	@Test
	public void ediPathUsage() throws Exception {
		EDILexer lx = TestResources.getAsLexer("test-interchange-rep.edi");
		EDIParser parser = EDIRegistry.newParser(EDIStandard.ANSI_X12);
		m = parser.parse(lx);
		assertNotNull(m);
//		
//		BGN~11^12^13~ 07141005162 ~040714~1003$
//		DMG~D8~19880208~F~~<RET<R5^<RET<E1.01~~~~~$
//		MSG~test1$
//		MSG~test2<test3<test4$
		
		assertEquals(m.getTextAt(EDIPath.of("GS")), "GS");
		assertEquals(m.getTextAt(EDIPath.of("DMG.1")), "D8");
		
		assertEquals(m.getTextAt(EDIPath.of("MSG[1]")), "MSG");
		assertEquals(m.getTextAt(EDIPath.of("MSG[2]")), "MSG");
		assertEquals(m.getTextAt(EDIPath.of("MSG[1].1")), "test1");
		assertEquals(m.getTextAt(EDIPath.of("MSG[2].1.1")), "test2");
		assertEquals(m.getTextAt(EDIPath.of("MSG[2].1.2")), "test3");
		assertEquals(m.getTextAt(EDIPath.of("MSG[2].1.3")), "test4");
		assertEquals(m.getTextAt(EDIPath.of("MSG[2].2")), "test5");
		
		assertEquals(m.getTextAt(EDIPath.of("BGN.1[1]")), "11");
		assertEquals(m.getTextAt(EDIPath.of("BGN.1[2]")), "12");
		assertEquals(m.getTextAt(EDIPath.of("BGN.1[3]")), "13");
		
		assertEquals(m.getTextAt(EDIPath.of("DMG.5[1].1")), "");
		assertEquals(m.getTextAt(EDIPath.of("DMG.5[1].2")), "RET");
		assertEquals(m.getTextAt(EDIPath.of("DMG.5[1].3")), "R5");
		
		assertEquals(m.getTextAt(EDIPath.of("DMG.5[2].1")), "");
		assertEquals(m.getTextAt(EDIPath.of("DMG.5[2].2")), "RET");
		assertEquals(m.getTextAt(EDIPath.of("DMG.5[2].3")), "E1.01");
	}
	
	
	
	
}
