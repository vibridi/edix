package com.vibridi.edix.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.vibridi.edix.EDIStandard;
import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;

public abstract class EDIParser {
	
	@FunctionalInterface
	public interface EDIValidationRule {
		public void check(EDIMessage message) throws EDISyntaxException;
	}
	
	private static final Map<EDIStandard, Class<? extends EDIParser>> parsers;

    static {
        parsers = new HashMap<>();
        parsers.put(EDIStandard.ANSI_X12, AnsiParser.class);
    }
    
    /**
     * Registers a custom parser.
     *
     * @param standard Data standard associated to this parser
     * @param clazz Fully qualified class name of an EDIParser subclass
     */
    public static void register(EDIStandard standard, Class<? extends EDIParser> clazz) {
        parsers.put(standard, clazz);
    }
    
    /**
     * Returns an instance of some EDIParser subclass based on the message standard.
     * <p>
     * Parsers for ANSI X12 and UN/EDIFACT are built-in. Other parsers can be registered with the 
     * {@code registerParser()} method.
     *
     * @param target data standard
     * @return Subclass of EDIParser that knows how to parse the data
     * @throws RuntimeException if the object can't be instantiated
     */
    public static EDIParser newInstance(EDIStandard standard) {
    	Class<? extends EDIParser> clazz = parsers.get(standard);
    	
		try {
			return (EDIParser) clazz.newInstance();
		} catch(NullPointerException e) {
			throw new RuntimeException("No parser class specified for standard: " + standard.name());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot instantiate EDI parser for class: " + clazz.getName());
		}
    }
	
	protected boolean strict;
	private Set<EDIValidationRule> validationRules;
	
	public EDIParser() {
		strict = false;
		validationRules = initValidationRules();
		Objects.requireNonNull(validationRules);
	}
	
	public abstract EDIMessage parse(EDILexer lexer) throws EDISyntaxException, IOException;
	public abstract Set<EDIValidationRule> initValidationRules();
	
	public void registerValidationRule(EDIValidationRule rule) {
		validationRules.add(rule);
	}
	
	public void validate(EDIMessage message) throws EDISyntaxException {			
		for(EDIValidationRule rule : validationRules) {
			rule.check(message);
		}
	}
	
}
