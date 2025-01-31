/*
 * @(#) FormatterTest.java
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

package io.jstuff.json.format.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import io.jstuff.json.JSONSimple;
import io.jstuff.json.format.Formatter;
import static io.jstuff.json.format.Formatter.unixLineSeparator;

public class FormatterTest {

    @Test
    public void shouldOutputAllTypes() throws IOException {
        String input = "{\"aaa\":1,\"bbb\":true,\"ccc\":\"\\u2014\",\"ddd\":1.0,\"eee\":{},\"fff\":[],\"ggg\":null}";
        Object json = JSONSimple.parse(input);
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(unixLineSeparator);
        formatter.formatTo(sb, json);
        String result = "{\n" +
                "  \"aaa\": 1,\n" +
                "  \"bbb\": true,\n" +
                "  \"ccc\": \"\\u2014\",\n" +
                "  \"ddd\": 1.0,\n" +
                "  \"eee\": {},\n" +
                "  \"fff\": [],\n" +
                "  \"ggg\": null\n" +
                "}";
        assertEquals(result, sb.toString());
        assertEquals(result, formatter.format(json));
        sb.setLength(0);
        Formatter.outputTo(sb, json);
        assertEquals(input, sb.toString());
        assertEquals(input, Formatter.output(json));
    }

    @Test
    public void shouldOutputNestedObjects() throws IOException {
        String input = "{\"aaa\":{\"bbb\":{\"ccc\":{\"ddd\":{},\"eee\":[1,2,3,4,5]}}}}";
        Object json = JSONSimple.parse(input);
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(unixLineSeparator);
        formatter.formatTo(sb, json);
        String result = "{\n" +
                "  \"aaa\": {\n" +
                "    \"bbb\": {\n" +
                "      \"ccc\": {\n" +
                "        \"ddd\": {},\n" +
                "        \"eee\": [\n" +
                "          1,\n" +
                "          2,\n" +
                "          3,\n" +
                "          4,\n" +
                "          5\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        assertEquals(result, sb.toString());
        assertEquals(result, formatter.format(json));
        sb.setLength(0);
        Formatter.outputTo(sb, json);
        assertEquals(input, sb.toString());
        assertEquals(input, Formatter.output(json));
    }

    @Test
    public void shouldFormatOutputWithDifferentIndentSizesAndLineTerminators() throws IOException {
        List<Integer> list = new ArrayList<>();
        list.add(123);
        list.add(456);
        list.add(789);
        list.add(222);
        StringBuilder sb = new StringBuilder();
        Formatter formatter1 = new Formatter(3, unixLineSeparator);
        formatter1.formatTo(sb, list);
        assertEquals("[\n   123,\n   456,\n   789,\n   222\n]", sb.toString());
        sb.setLength(0);
        formatter1.formatTo(sb, list, 2);
        assertEquals("[\n     123,\n     456,\n     789,\n     222\n  ]", sb.toString());
        sb.setLength(0);
        Formatter formatter2 = new Formatter("\r\n");
        formatter2.formatTo(sb, list);
        assertEquals("[\r\n  123,\r\n  456,\r\n  789,\r\n  222\r\n]", sb.toString());
        Formatter formatter3 = new Formatter(1, "\r\n");
        sb.setLength(0);
        formatter3.formatTo(sb, list);
        assertEquals("[\r\n 123,\r\n 456,\r\n 789,\r\n 222\r\n]", sb.toString());
    }

    @Test
    public void shouldSetIndentAndTerminatorDynamically() throws IOException {
        List<Integer> list = new ArrayList<>();
        list.add(123);
        list.add(456);
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(4, "\n\n");
        assertEquals(4, formatter.getIndent());
        assertEquals("\n\n", formatter.getLineSeparator());
        formatter.formatTo(sb, list);
        assertEquals("[\n\n    123,\n\n    456\n\n]", sb.toString());
        formatter = new Formatter(2, "\n>  ");
        sb.setLength(0);
        sb.append(">  ");
        formatter.formatTo(sb, list);
        sb.append('\n');
        assertEquals(">  [\n>    123,\n>    456\n>  ]\n", sb.toString());
        assertThrows(IllegalArgumentException.class, () -> new Formatter(-1));
        assertThrows(IllegalArgumentException.class, () -> new Formatter(null));
    }

    @Test
    public void shouldOutputArrayOfStrings() {
        String[] array = new String[] { "abc", "def" };
        assertEquals("[\"abc\",\"def\"]", Formatter.output(array));
        Formatter formatter = new Formatter(unixLineSeparator);
        assertEquals("[\n  \"abc\",\n  \"def\"\n]", formatter.format(array));
    }

}
