/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 *
 * This file is part of EDIReader. You may obtain a license for its use directly from
 * BerryWorks Software, and you may also choose to use this software under the terms of the
 * GPL version 3. Other products in the EDIReader software suite are available only by licensing
 * with BerryWorks. Only those files bearing the GPL statement below are available under the GPL.
 *
 * EDIReader is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * EDIReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.vibridi.edix.read;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Data structure that associates leading character sequences with specific parser implementations.
 * When an EDI or EDI-like stream of data is to be parsed without pre-knowledge of which particular
 * EDI standard is to be used, this parser registry is used to select a parser based on the initial
 * characters of data.
 * <p>
 * The parsers for ANSI X.12 and UN/EDIFACT are included in the registry be default. The classes
 * that implement these parsers are provided by the core EDIReader framework.
 * <p>
 * Parsers for other formats, including HL7, ACH, and NSF, are also listed in the registry.
 * The classes that implement these formats are optional modules not included in the core EDIReader framework.
 * If an optional parser module is present in the classpath, the registry is therefore able to
 * select and load the appropriate parser in response to the leading character sequences in the data.
 * <p>
 * It is also possible for a developer to implement a parser for an EDI-like data format and register that
 * parser along with the leading data characters which signal the instance of an interchange of that format.
 * In this way, the EDIReader framework can be extended to parse previously unsupported data formats in the same
 * way that it supports X12 and EDIFACT.
 */
public class ParserRegistry { // TODO name?

	private static final Map<String,EDILexer> registered;

    static {
    	registered = new HashMap<>();
//        registered.put("ISA", AnsiReader.class); // TODO
//        registered.put("UNA", EdifactReaderWithCONTRL.class);
//        registered.put("UNB", EdifactReaderWithCONTRL.class);
//        registered.put("UNH", UNHReader.class);
    }

    /**
     * Returns an instance of some EDIReader subclass based on the first
     * several chars of data to be parsed.
     * <p>
     * Parsers for ANSI X12 and UN/EDIFACT are built-in. Other parsers can be registered, including
     * custom parsers developed by users. Parsers registered via register() are considered first for
     * a match with the incoming data before the built-in parsers are considered, allowing users to
     * provide custom implementations of X12 and EDIFACT parsers if needed.
     *
     * @param prefix of data to be parsed
     * @return Optional with subclass of EDILexer that knows how to tokenize the data
     */

    public static Optional<EDILexer> get(String prefix) {
        EDILexer result = null;

        // See if a suitable registered class name
        for(String k : registered.keySet()) {
        	if(k.regionMatches(true, 0, prefix, 0, Math.min(k.length(), prefix.length()))) {
        		result = registered.get(k);
        		break;
        	}
        }

        return Optional.ofNullable(result);
    }

    /**
     * Registers a parser and associates it with the leading data characters that signal an instance of an interchange
     * supported by the parser.
     *
     * @param firstChars of data to be parsed
     * @param className  fully qualified classname of an EDIReader subclass
     */
    public static void register(String firstChars, EDILexer lexer) {
        registered.put(firstChars, lexer);
    }

}
