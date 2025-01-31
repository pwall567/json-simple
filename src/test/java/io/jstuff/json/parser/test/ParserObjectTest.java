/*
 * @(#) ParserObjectTest.java
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

package io.jstuff.json.parser.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import io.jstuff.json.parser.ParseException;
import io.jstuff.json.parser.ParseOptions;
import io.jstuff.json.parser.ParseOptions.DuplicateKeyOption;
import io.jstuff.json.parser.Parser;
import static io.jstuff.json.parser.Parser.MAX_DEPTH_EXCEEDED;

public class ParserObjectTest {

    @Test
    public void shouldParseEmptyObject() {
        Object result = Parser.parse("{}");
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(0, map.size());
    }

    @Test
    public void shouldParseSimpleObject() {
        Object result = Parser.parse("{\"first\":123,\"second\":\"Hi there!\"}");
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(2, map.size());
        assertTrue(map.get("first") instanceof Integer);
        assertEquals(123, map.get("first"));
        assertTrue(map.get("second") instanceof String);
        assertEquals("Hi there!", map.get("second"));
    }

    @Test
    public void shouldParseNestedObject() {
        Object result = Parser.parse("{\"first\":123,\"second\":{\"a\":[{\"aa\":0}]}}");
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(2, map.size());
        assertTrue(map.get("first") instanceof Integer);
        assertEquals(123, map.get("first"));
        assertTrue(map.get("second") instanceof Map);
        Map<?, ?> map2 = (Map<?, ?>)map.get("second");
        assertEquals(1, map2.size());
        assertTrue(map2.get("a") instanceof List);
        List<?> list = (List<?>)map2.get("a");
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof Map);
        Map<?, ?> map3 = (Map<?, ?>)list.get(0);
        assertEquals(1, map3.size());
        assertEquals(0, map3.get("aa"));
    }

    @Test
    public void shouldThrowExceptionOnMissingClosingBrace() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("{\"first\":123"));
        assertEquals("Missing closing brace in JSON object", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Missing closing brace in JSON object", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnMissingColon() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("{\"first\"123}"));
        assertEquals("Missing colon in JSON object", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Missing colon in JSON object", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnTrailingComma() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("{\"first\":123,}"));
        assertEquals("Illegal key in JSON object", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Illegal key in JSON object", e.getMessage());
    }

    @Test
    public void shouldAllowTrailingCommaWithOption_objectTrailingComma() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.ERROR, false, true, false, 1000);
        Object result = Parser.parse("{\"first\":123,}", options);
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(123, map.get("first"));
    }

    @Test
    public void shouldThrowExceptionOnMissingQuotes() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("{first:123}"));
        assertEquals("Illegal key in JSON object", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Illegal key in JSON object", e.getMessage());
    }

    @Test
    public void shouldAllowMissingQuotesWithOption_objectKeyUnquoted() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.ERROR, true, false, false, 1000);
        Object result = Parser.parse("{first:123}", options);
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(1, map.size());
        assertTrue(map.get("first") instanceof Integer);
        assertEquals(123, map.get("first"));
    }

    @Test
    public void shouldThrowExceptionOnDuplicateKeys() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("{\"first\":123,\"first\":456}"));
        assertEquals("Duplicate key in JSON object \"first\"", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Duplicate key in JSON object \"first\"", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnDuplicateKeysWithOptionERROR() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.ERROR, false, false, false, 1000);
        ParseException e = assertThrows(ParseException.class,
                () -> Parser.parse("{\"first\":123,\"first\":456}", options));
        assertEquals("Duplicate key in JSON object \"first\"", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Duplicate key in JSON object \"first\"", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnDuplicateKeysWithOptionCHECK_IDENTICALAndDifferentValues() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.CHECK_IDENTICAL, false, false, false, 1000);
        ParseException e = assertThrows(ParseException.class,
                () -> Parser.parse("{\"first\":123,\"first\":456}", options));
        assertEquals("Duplicate key in JSON object \"first\"", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Duplicate key in JSON object \"first\"", e.getMessage());
    }

    @Test
    public void shouldTakeFirstOnDuplicateKeysWithOptionCHECK_IDENTICALAndSameValues() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.TAKE_FIRST, false, false, false, 1000);
        Object result = Parser.parse("{\"first\":123,\"first\":123}", options);
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(1, map.size());
        Object value = map.get("first");
        assertTrue(value instanceof Integer);
        assertEquals(123, value);
    }

    @Test
    public void shouldTakeFirstOnDuplicateKeysWithOptionTAKE_FIRST() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.TAKE_FIRST, false, false, false, 1000);
        Object result = Parser.parse("{\"first\":123,\"first\":456}", options);
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(1, map.size());
        Object value = map.get("first");
        assertTrue(value instanceof Integer);
        assertEquals(123, value);
    }

    @Test
    public void shouldTakeLastOnDuplicateKeysWithOptionTAKE_LAST() {
        ParseOptions options = new ParseOptions(DuplicateKeyOption.TAKE_LAST, false, false, false, 1000);
        Object result = Parser.parse("{\"first\":123,\"first\":456}", options);
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(1, map.size());
        Object value = map.get("first");
        assertTrue(value instanceof Integer);
        assertEquals(456, value);
    }

    @Test
    public void shouldAllowNestingUpToMaximumDepth() {
        ParseOptions options = new ParseOptions(ParseOptions.DuplicateKeyOption.ERROR, false, false, false, 50);
        StringBuilder allowed = new StringBuilder(301);
        for (int i = 50; i > 0; i--)
            allowed.append("{\"a\":");
        allowed.append('1');
        for (int i = 50; i > 0; i--)
            allowed.append('}');
        Object result = Parser.parse(allowed.toString(), options);
        for (int i = 50; i > 0; i--) {
            assertTrue(result instanceof Map<?, ?>);
            result = ((Map<?, ?>)result).get("a");
        }
        assertEquals(1, result);
    }

    @Test
    public void shouldThrowExceptionOnNestingDepthExceeded() {
        ParseOptions options = new ParseOptions(ParseOptions.DuplicateKeyOption.ERROR, false, false, false, 50);
        StringBuilder excessive = new StringBuilder(51);
        for (int i = 51; i > 0; i--)
            excessive.append("{\"a\":");
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse(excessive.toString(), options));
        assertEquals(MAX_DEPTH_EXCEEDED, e.getText());
        assertEquals(MAX_DEPTH_EXCEEDED, e.getMessage());
        assertEquals("", e.getPointer());
    }

}
