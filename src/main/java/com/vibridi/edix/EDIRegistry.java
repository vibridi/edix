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

package com.vibridi.edix;

import java.util.HashMap;
import java.util.Map;

import com.vibridi.edix.lexer.AnsiLexer;
import com.vibridi.edix.lexer.EDILexer;
import com.vibridi.edix.parser.AnsiParser;
import com.vibridi.edix.parser.EDIParser;

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
public class EDIRegistry {
    


}
