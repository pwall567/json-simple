/*
 * @(#) ParserStringTest.java
 *
 * json-simple  Simple JSON Parser and Formatter
 * Copyright (c) 2021, 2023 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.parser.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import net.pwall.json.parser.ParseException;
import net.pwall.json.parser.Parser;

public class ParserStringTest {

    @Test
    public void shouldParseSimpleString() {
        assertEquals("simple", Parser.parse("\"simple\""));
    }

    @Test
    public void shouldParseStringWithEscapeSequences() {
        assertEquals("tab\tnewline\nquote\" ", Parser.parse("\"tab\\tnewline\\nquote\\\" \""));
    }

    @Test
    public void shouldParseStringWithUnicodeEscapeSequence() {
        assertEquals("mdash \u2014", Parser.parse("\"mdash \\u2014\""));
    }

    @Test
    public void shouldThrowExceptionOnMissingClosingQuote() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("\"abc"));
        assertEquals("Unterminated JSON string", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Unterminated JSON string", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnBadEscapeSequence() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("\"ab\\c\""));
        assertEquals("Illegal escape sequence in JSON string", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Illegal escape sequence in JSON string", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnBadUnicodeSequence() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("\"ab\\uxxxx\""));
        assertEquals("Illegal Unicode sequence in JSON string", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Illegal Unicode sequence in JSON string", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnIllegalCharacter() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("\"ab\u0001\""));
        assertEquals("Illegal character in JSON string", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Illegal character in JSON string", e.getMessage());
    }

}
