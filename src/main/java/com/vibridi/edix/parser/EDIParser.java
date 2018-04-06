package com.vibridi.edix.parser;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import com.vibridi.edix.error.EDISyntaxException;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.model.EDIMessage;

public abstract class EDIParser {
	
	@FunctionalInterface
	public interface EDIValidationRule {
		public void check(EDIMessage message) throws EDISyntaxException;
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
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	public void registerValidationRule(EDIValidationRule rule) {
		validationRules.add(rule);
	}
	
	public void validate(EDIMessage message) throws EDISyntaxException {			
		for(EDIValidationRule rule : validationRules) {
			rule.check(message);
		}
	}
	
}
