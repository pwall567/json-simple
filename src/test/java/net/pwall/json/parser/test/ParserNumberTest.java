/*
 * @(#) ParserNumberTest.java
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

import java.math.BigDecimal;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import net.pwall.json.parser.ParseException;
import net.pwall.json.parser.Parser;

public class ParserNumberTest {

    @Test
    public void shouldParseZero() {
        Object result = Parser.parse("0");
        assertTrue(result instanceof Integer);
        assertEquals(0, ((Integer)result).intValue());
    }

    @Test
    public void shouldRejectLeadingZeros() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("00"));
        assertEquals("Illegal JSON number", e.getMessage());
        e = assertThrows(ParseException.class, () -> Parser.parse("0123"));
        assertEquals("Illegal JSON number", e.getMessage());
    }

    @Test
    public void shouldRejectIncorrectNumbers() {
        ParseException e = assertThrows(ParseException.class, () -> Parser.parse("123a"));
        assertEquals("Excess characters following JSON", e.getMessage());
        e = assertThrows(ParseException.class, () -> Parser.parse("12:00"));
        assertEquals("Excess characters following JSON", e.getMessage());
        e = assertThrows(ParseException.class, () -> Parser.parse("1.23/4"));
        assertEquals("Excess characters following JSON", e.getMessage());
    }

    @Test
    public void shouldParsePositiveIntegers() {
        Object result = Parser.parse("123");
        assertTrue(result instanceof Integer);
        assertEquals(123, ((Integer)result).intValue());
        result = Parser.parse("5678900");
        assertTrue(result instanceof Integer);
        assertEquals(5678900, ((Integer)result).intValue());
        result = Parser.parse("2147483647");
        assertTrue(result instanceof Integer);
        assertEquals(2147483647, ((Integer)result).intValue());
    }

    @Test
    public void shouldParseNegativeIntegers() {
        Object result = Parser.parse("-1");
        assertTrue(result instanceof Integer);
        assertEquals(-1, ((Integer)result).intValue());
        result = Parser.parse("-876543");
        assertTrue(result instanceof Integer);
        assertEquals(-876543, ((Integer)result).intValue());
        result = Parser.parse("-2147483648");
        assertTrue(result instanceof Integer);
        assertEquals(-2147483648, ((Integer)result).intValue());
    }

    @Test
    public void shouldParsePositiveLongIntegers() {
        Object result = Parser.parse("1234567890000");
        assertTrue(result instanceof Long);
        assertEquals(1234567890000L, ((Long)result).longValue());
        result = Parser.parse("567895678956789");
        assertTrue(result instanceof Long);
        assertEquals(567895678956789L, ((Long)result).longValue());
        result = Parser.parse("2147483648");
        assertTrue(result instanceof Long);
        assertEquals(2147483648L, ((Long)result).longValue());
        result = Parser.parse("9223372036854775807");
        assertTrue(result instanceof Long);
        assertEquals(9223372036854775807L, ((Long)result).longValue());
    }

    @Test
    public void shouldParseNegativeLongIntegers() {
        Object result = Parser.parse("-1234567890000");
        assertTrue(result instanceof Long);
        assertEquals(-1234567890000L, ((Long)result).longValue());
        result = Parser.parse("-567895678956789");
        assertTrue(result instanceof Long);
        assertEquals(-567895678956789L, ((Long)result).longValue());
        result = Parser.parse("-2147483649");
        assertTrue(result instanceof Long);
        assertEquals(-2147483649L, ((Long)result).longValue());
        result = Parser.parse("-9223372036854775808");
        assertTrue(result instanceof Long);
        assertEquals(-9223372036854775808L, ((Long)result).longValue());
    }

    @Test
    public void shouldParseDecimal() {
        Object result = Parser.parse("0.0");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal)result));
        result = Parser.parse("0.00");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal)result));
    }

    @Test
    public void shouldParsePositiveDecimal() {
        Object result = Parser.parse("12340.0");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("12340").compareTo((BigDecimal)result));
        result = Parser.parse("1e200");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("1e200").compareTo((BigDecimal)result));
        result = Parser.parse("27e-60");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("27e-60").compareTo((BigDecimal)result));
        result = Parser.parse("0.1e-48");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("0.1e-48").compareTo((BigDecimal)result));
        result = Parser.parse("9223372036854775808");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("9223372036854775808").compareTo((BigDecimal)result));
    }

    @Test
    public void shouldParseNegativeDecimal() {
        Object result = Parser.parse("-12340.0");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("-12340").compareTo((BigDecimal)result));
        result = Parser.parse("-1e200");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("-1e200").compareTo((BigDecimal)result));
        result = Parser.parse("-27e-60");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("-27e-60").compareTo((BigDecimal)result));
        result = Parser.parse("-0.1e-48");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("-0.1e-48").compareTo((BigDecimal)result));
        result = Parser.parse("-9223372036854775809");
        assertTrue(result instanceof BigDecimal);
        assertEquals(0, new BigDecimal("-9223372036854775809").compareTo((BigDecimal)result));
    }

}
