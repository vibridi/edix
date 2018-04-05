package com.vibridi.edix;

public enum EDIStandard {
	ANSI_X12,
	EDIFACT;
	
	
	public static EDIStandard of(String name) {
		switch(name) {
		case "ISA":
			return ANSI_X12;
		
		case "UNA":
		case "UNB":
		case "UNH":
			return EDIFACT;
			
		default:
			throw new IllegalArgumentException("EDI standard not recognized: " + name);
		}		
	}
	
}
