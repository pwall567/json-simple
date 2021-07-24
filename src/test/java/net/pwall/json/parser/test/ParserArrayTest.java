/*
 * @(#) ParserArrayTest.java
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.pwall.json.parser.ParseException;
import net.pwall.json.parser.Parser;

public class ParserArrayTest {

    @Test
    public void shouldParseEmptyArray() {
        Object result = Parser.parse("[]");
        assertTrue(result instanceof List);
        List<?> list = (List<?>)result;
        assertEquals(0, list.size());
    }

    @Test
    public void shouldParseArrayOfString() {
        Object result = Parser.parse("[\"simple\"]");
        assertTrue(result instanceof List);
        List<?> list = (List<?>)result;
        assertEquals(1, list.size());
        assertEquals("simple", list.get(0));
    }

    @Test
    public void shouldParseArrayOfTwoStrings() {
        Object result = Parser.parse("[\"Hello\",\"world\"]");
        assertTrue(result instanceof List);
        List<?> list = (List<?>)result;
        assertEquals(2, list.size());
        assertEquals("Hello", list.get(0));
        assertEquals("world", list.get(1));
    }

    @Test
    public void shouldParseArrayOfArray() {
        Object result = Parser.parse("[[\"Hello\",[\"world\",\"universe\"]]]");
        assertTrue(result instanceof List);
        List<?> list1 = (List<?>)result;
        assertEquals(1, list1.size());
        assertTrue(list1.get(0) instanceof List);
        List<?> list2 = (List<?>)list1.get(0);
        assertEquals(2, list2.size());
        assertEquals("Hello", list2.get(0));
        assertTrue(list2.get(1) instanceof List);
        List<?> list3 = (List<?>)list2.get(1);
        assertEquals(2, list3.size());
        assertEquals("world", list3.get(0));
        assertEquals("universe", list3.get(1));
    }

    @Test
    public void shouldThrowExceptionOnMissingClosingBracket() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("[\"simple\""));
        assertEquals("Missing closing bracket in JSON array", e.getText());
        assertEquals("", e.getPointer());
        assertEquals("Missing closing bracket in JSON array", e.getMessage());
    }

}
