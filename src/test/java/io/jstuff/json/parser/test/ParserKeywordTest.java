/*
 * @(#) ParserKeywordTest.java
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

import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.jstuff.json.parser.Parser;

public class ParserKeywordTest {

    @Test
    public void shouldParseNull() {
        Object result = Parser.parse("null");
        assertNull(result);
    }

    @Test
    public void shouldParseTrue() {
        Object result = Parser.parse("true");
        assertTrue(result instanceof Boolean);
        assertEquals(true, result);
    }

    @Test
    public void shouldParseFalse() {
        Object result = Parser.parse("false");
        assertTrue(result instanceof Boolean);
        assertEquals(false, result);
    }

    @Test
    public void shouldParseKeywordsInObject() {
        Object result = Parser.parse("{\"aaa\":true,\"bbb\":false,\"ccc\":null}");
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(3, map.size());
        assertTrue(map.get("aaa") instanceof Boolean);
        assertEquals(true, map.get("aaa"));
        assertTrue(map.get("bbb") instanceof Boolean);
        assertEquals(false, map.get("bbb"));
        assertNull(map.get("ccc"));
    }

}
