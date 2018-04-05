package com.vibridi.edix.error;

public enum ErrorMessages {
	INCOMPLETE_X12("Incomplete ANSI X.12 interchange"),
	INVALID_SEGMENT_TERMINATOR("Invalid segment terminator [{0}]"),
	LOOK_AHEAD_FAILED("Lexer look-ahead returned too few characters."),
	X12_MISSING_ISA("ANSI X.12 interchange must begin with ISA"),
	ISA_FIELDS_NUMBER("ISA must have exactly 16 fields."),
	FUNCTIONAL_GROUPS("Incorrect number of functional groups. Expected {0} but was {1}."),
	INTERCHANGE_CONTROL_NUMBER("ISA and IEA interchange control numbers don't match"),
	TRANSACTION_SETS("Incorrect number of transaction sets. Expected {0} but was {1}."),
	GROUP_CONTROL_NUMBER("GS and GE control numbers don't match"),
	TRANSACTION_CONTROL_NUMBER("ST and SE control numbers don't match"),
	;
	
	
	private String text;
	private String[] args;
	
	private ErrorMessages(String text) {
		this.text = text;
		this.args = new String[0];
	}
	
	public ErrorMessages withArgs(Object... args) {
		if(args == null || args.length == 0)
			return this;
		
		this.args = new String[args.length];
		for(int i = 0; i < args.length; i++)
			this.args[i] = args[i].toString();
		return this;
	}
	
	public ErrorMessages withArgs(String... args) {
		this.args = args;
		return this;
	}
	
	@Override
	public String toString() {
		return resolve();
	}

	private String resolve() {
		String tmp = text;
		for(int i = 0; i < args.length; i++) {
			tmp = tmp.replace("{"+i+"}", args[i]);
		}
		return tmp;
	}
	
//    String ELEMENT_TOO_LONG = "Too many characters in an element (delimiter problem?)";
//    String EXPECTED_SIMPLE_TOKEN = "Expected a simple token";
//    String MALFORMED_EDI_SEGMENT = "Malformed EDI segment";
//    String INTERNAL_ERROR_MULTIPLE_PREVIEWS = "Internal error: interchange previewed more than once";
//    String INTERNAL_ERROR_MULTIPLE_EOFS = "End-of-file hit multiple times (internal error)";
//    String DIGITS_ONLY = "Element must contain only digits";
//    String INVALID_COMPOSITE = "Invalid composite element";
//    
//    String UNEXPECTED_EOF = "Unexpected end of data";
//    String INVALID_BEGINNING_OF_SEGMENT = "Invalid beginning of segment";
//    String UNEXPECTED_SEGMENT_IN_CONTEXT = "Unexpected segment type in this context";
//    
//    String TOO_MANY_ISA_FIELDS = "Too many fields for an ISA (Segment terminator problem?)";
//    String ISA_FIELD_WIDTH = "Incorrect length of fixed-length ISA field";
//    String ISA_SEGMENT_HAS_TOO_FEW_FIELDS = "ISA segment requires 16 instances of the field delimiter";
//    
//    String CONTROL_NUMBER_IEA = "Control number error in IEA segment";
//    String CONTROL_NUMBER_GE = "Control number error in GE segment";
//    String CONTROL_NUMBER_SE = "Control number error in SE segment";
//    String COUNT_IEA = "Functional group count error in IEA segment";
//    String COUNT_GE = "Transaction count error in GE segment";
//    String COUNT_SE = "Segment count error in SE segment";
//    String INVALID_UNA = "Improperly formed UNA segment";
//    String CONTROL_NUMBER_UNZ = "Control number error in UNZ segment";
//    String CONTROL_NUMBER_UNT = "Control number error in UNT segment";
//    String COUNT_UNZ = "Functional group count error in UNZ segment";
//    String CONTROL_NUMBER_UNE = "Control number error in UNE segment";
//    String COUNT_UNT = "Segment count error in UNT segment";
//    String COUNT_UNE = "Message count error in UNE segment";
//    String FIRST_SEGMENT_MUST_BE_UNA_OR_UNB = "First segment must be UNA or UNB";
//    String NO_HL7_PARSER = "Data begins with MSH indicating HL7 data, but no HL7 parser is available";
//    String INCOMPLETE_HL7_MESSAGE = "Incomplete HL7 message";
//    String INCOMPLETE_ACH_MESSAGE = "Incomplete ACH file";
//    String NO_STANDARD_BEGINS_WITH = "No supported EDI standard interchange begins with ";
//    String XML_INSTEAD_OF_EDI = "Encountered XML when expecting EDI";
//    String SE_MISSING = "Transaction must be terminated with an SE segment";
//    String MANDATORY_ELEMENT_MISSING = "Mandatory element missing";
//    String MISSING_UNP = "UNP segment not properly positioned after UNO segment and data object sequence";
//    String MISMATCHED_UNP_LENGTH = "UNP segment contains length field that does not match length field in UNO";
//    String MISMATCHED_PACKAGE_REF = "UNP segment contains package reference that does not match corresponding reference in UNO";
//    String MISSING_UNO_LENGTH = "UNO segment missing mandatory length field";
//    String MISSING_BIN_LENGTH = "BIN segment missing mandatory length field";
//    String TRADACOMS_MISSING_STX = "TRADACOMS interchange must begin with STX";
//    String SEQUENCE_MHD = "Message sequence error in MHD segment";
//    String COUNT_MTR = "Segment count error in MTR segment";
//    String COUNT_END = "Message count error in END segment";
//    String CONTROL_NUMBER_RSG = "Sender's transmission reference error in RSG reconciliation segment";
//    String RECIPIENT_RSG = "Recipient error in RSG reconciliation segment";
	
}
