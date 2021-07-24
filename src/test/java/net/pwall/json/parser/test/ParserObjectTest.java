/*
 * @(#) ParserObjectTest.java
 *
 * json-simple  Simple JSON Parser and Formatter
 * Copyright (c) 2021 Peter Wall
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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.pwall.json.parser.ParseException;
import net.pwall.json.parser.Parser;

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
    public void shouldThrowExceptionOnMissingQuotes() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("{first:123}"));
        assertEquals("Illegal key in JSON object", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Illegal key in JSON object", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnDuplicateKeys() {
        ParseException e = assertThrows(ParseException.class,
                () -> Parser.parse("{\"first\":123,\"first\":456}"));
        assertEquals("Duplicate key in JSON object", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Duplicate key in JSON object", e.getMessage());
    }

}
