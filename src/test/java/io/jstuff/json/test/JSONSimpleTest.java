/*
 * @(#) JSONSimpleTest.java
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

package io.jstuff.json.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.jstuff.json.JSONSimple;

public class JSONSimpleTest {

    @Test
    public void shouldAccessParserThroughJSONSimpleClass() {
        Object result = JSONSimple.parse("{\"alpha\":123,\"beta\":\"excellent\"}");
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>)result;
        assertEquals(2, map.size());
        assertTrue(map.get("alpha") instanceof Integer);
        assertEquals(123, map.get("alpha"));
        assertTrue(map.get("beta") instanceof String);
        assertEquals("excellent", map.get("beta"));
    }

    @Test
    public void shouldAccessFormatterThroughJSONSimpleClass() throws IOException {
        Map<String, Integer> map = new HashMap<>();
        map.put("alpha", 123);
        assertEquals("{\"alpha\":123}", JSONSimple.output(map));
        StringBuilder sb = new StringBuilder();
        JSONSimple.outputTo(sb, map);
        assertEquals("{\"alpha\":123}", sb.toString());
        assertEquals("{\n  \"alpha\": 123\n}", JSONSimple.format(map));
        sb.setLength(0);
        JSONSimple.formatTo(sb, map);
        assertEquals("{\n  \"alpha\": 123\n}", sb.toString());
    }

    @Test
    public void shouldReformatJSON() throws IOException {
        String json = "{\"alpha\":123}";
        assertEquals("{\n  \"alpha\": 123\n}", JSONSimple.reformat(json));
        StringBuilder sb = new StringBuilder();
        JSONSimple.reformatTo(sb, json);
        assertEquals("{\n  \"alpha\": 123\n}", sb.toString());
    }

}
