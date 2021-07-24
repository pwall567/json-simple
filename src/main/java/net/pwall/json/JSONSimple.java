/*
 * @(#) JSONSimple.java
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

package net.pwall.json;

import java.io.IOException;

import net.pwall.json.format.Formatter;
import net.pwall.json.parser.Parser;

/**
 * A set of static functions to access simple JSON functionality - a parser that converts JSON into a structure
 * consisting solely of standard classes and interfaces, and formatter that takes such a structure and formats it as
 * required.
 *
 * @author  Peter Wall
 */
public class JSONSimple {

    /**
     * Parse a string of JSON and return a value consisting af simple classes and interfaces only.
     *
     * @param   json        the JSON string
     * @return              the decoded structure
     * @see                 Parser#parse(String)
     */
    public static Object parse(String json) {
        return Parser.parse(json);
    }

    /**
     * Output an arbitrary object as JSON.
     *
     * @param   obj         the object (may be {@code null})
     * @return              the object in JSON form (with no formatting)
     * @see                 Formatter#format(Object)
     */
    public static String output(Object obj) {
        return Formatter.output(obj);
    }

    /**
     * Output an arbitrary object as JSON by appending to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   obj         the object (may be {@code null})
     * @throws  IOException if thrown by the {@link Appendable}
     * @see                 Formatter#outputTo(Appendable, Object)
     */
    public static void outputTo(Appendable a, Object obj) throws IOException {
        Formatter.outputTo(a, obj);
    }

    /**
     * Format an arbitrary object as JSON, using the line separator and indentation value of the default
     * {@code Formatter}.  JSON objects are output with each property on a separate line, and JSON arrays are output
     * with each item on a line.
     *
     * @param   obj         the object (may be {@code null})
     * @return              the object in JSON form (with no formatting)
     * @see                 Formatter#format(Object)
     */
    public static String format(Object obj) {
        return Formatter.getDefaultFormatter().format(obj);
    }

    /**
     * Format an arbitrary object as JSON by appending to an {@link Appendable}, using the line separator and
     * indentation value of the default {@code Formatter}.  JSON objects are output with each property on a separate
     * line, and JSON arrays are output with each item on a line.
     *
     * @param   a           the {@link Appendable}
     * @param   obj         the object (may be {@code null})
     * @throws  IOException if thrown by the {@link Appendable}
     * @see                 Formatter#formatTo(Appendable, Object)
     */
    public static void formatTo(Appendable a, Object obj) throws IOException {
        Formatter.getDefaultFormatter().formatTo(a, obj);
    }

    /**
     * Reformat a JSON string, using the line separator and indentation value of the default {@code Formatter}.
     *
     * @param   json        the JSON string
     * @return              the reformatted JSON
     * @see                 Parser#parse(String)
     * @see                 Formatter#format(Object)
     */
    public static String reformat(String json) {
        return Formatter.getDefaultFormatter().format(Parser.parse(json));
    }

    /**
     * Reformat a JSON string by appending to an {@link Appendable}, using the line separator and indentation value of
     * the default {@code Formatter}.
     *
     * @param   a           the {@link Appendable}
     * @param   json        the JSON string
     * @throws  IOException if thrown by the {@link Appendable}
     * @see                 Parser#parse(String)
     * @see                 Formatter#format(Object)
     */
    public static void reformatTo(Appendable a, String json) throws IOException {
        Formatter.getDefaultFormatter().formatTo(a, Parser.parse(json));
    }

}
