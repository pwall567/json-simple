/*
 * @(#) Formatter.java
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

package net.pwall.json.format;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * JSON Formatter - formats a data structure as JSON using the specified indentation and line termination settings.
 * Objects are converted as follows:
 * <dl>
 *   <dt>{@code null}</dt>
 *   <dd>The JSON null value "{@code null}"</dd>
 *   <dt>A {@link Map}</dt>
 *   <dd>A JSON object, with the keys as property names and the values converted by this class recursively
 *   (keys that are not strings are converted by {@link Object#toString() toString()})</dd>
 *   <dt>A {@link Collection} (<i>e.g.</i> {@link java.util.List List}, {@link java.util.Set Set})</dt>
 *   <dd>A JSON array, where each value is converted by this class recursively</dd>
 *   <dt>An array (of any object type)</dt>
 *   <dd>A JSON array, where each value is converted by this class recursively</dd>
 *   <dt>A {@link String}</dt>
 *   <dd>A JSON string, with full Unicode character escaping</dd>
 *   <dt>A {@link Number} (<i>e.g.</i> {@link Integer}, {@link java.math.BigDecimal BigDecimal})</dt>
 *   <dd>A JSON number, using the {@link Object#toString() toString()} of the object</dd>
 *   <dt>Anything else</dt>
 *   <dd>If the {@link Object#toString() toString()} of the object is "{@code true}" or "{@code false}", that
 *   string is used as a JSON boolean value; otherwise the string is output as a JSON string (this will catch the
 *   {@link Boolean} class, as well as other classes representing boolean values).</dd>
 * </dl>
 * This is a superset of the output of the {@link net.pwall.json.parser.Parser Parser} class, so this class may be
 * used as a complement to that one - to output structures created by that class or to create JSON data to be read
 * by it.  It also works well with the {@code JSONValue} structures produced by the {@code JSON.parse()} function of the
 * <a href="https://github.com/pwall567/jsonutil">jsonutil</a> library.  It may also work for other similar data
 * structures, but the results in such cases are not guaranteed.
 *
 * @author  Peter Wall
 */
public class Formatter {

    public static final int defaultIndent = 2;
    public static final String systemLineSeparator = System.getProperty("line.separator");
    public static final String unixLineSeparator = "\n";

    private static final char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final char[] tensDigits = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    };

    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static final Formatter defaultFormatter = new Formatter();

    private final int indent;
    private final String lineSeparator;

    /**
     * Construct a {@code Formatter} with the specified indentation size and line separator.
     *
     * @param   indent          the indentation size
     * @param   lineSeparator   the line separator
     * @throws  IllegalArgumentException    if the indentation size is less than 0, or if the line separator is
     *                                      {@code null}
     */
    public Formatter(int indent, String lineSeparator) {
        this.indent = checkIndent(indent);
        this.lineSeparator = checkLineSeparator(lineSeparator);
    }

    /**
     * Construct a {@code Formatter} with the specified indentation size and the default line separator.
     *
     * @param   indent          the indentation size
     * @throws  IllegalArgumentException    if the indentation size is less than 0
     */
    public Formatter(int indent) {
        this.indent = checkIndent(indent);
        this.lineSeparator = systemLineSeparator;
    }

    /**
     * Construct a {@code Formatter} with the default indentation size and the specified line separator.
     *
     * @param   lineSeparator   the line separator
     * @throws  IllegalArgumentException    if the line separator is {@code null}
     */
    public Formatter(String lineSeparator) {
        this.indent = defaultIndent;
        this.lineSeparator = checkLineSeparator(lineSeparator);
    }

    /**
     * Construct a {@code Formatter} with the default indentation size and line separator.
     */
    public Formatter() {
        this.indent = defaultIndent;
        this.lineSeparator = systemLineSeparator;
    }

    /**
     * Get the indentation size for this {@code Formatter}.
     *
     * @return              the indentation size
     */
    public int getIndent() {
        return indent;
    }

    /**
     * Get the line separator for this {@code Formatter}.
     *
     * @return              the line separator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Append the specified number of spaces to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @param   count       the number of spaces
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public void appendSpaces(Appendable a, int count) throws IOException {
        for (int i = count; i > 0; i--)
            a.append(' ');
    }

    /**
     * Append a line separator to an {@link Appendable}.
     *
     * @param   a           the {@link Appendable}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public void appendLineSeparator(Appendable a) throws IOException {
        a.append(lineSeparator);
    }

    /**
     * Format an arbitrary object as JSON, using the line separator and indentation value of the {@code Formatter}.
     * JSON objects are output with each property on a separate line, and JSON arrays are output with each item on a
     * line.
     *
     * @param   value       the value to be formatted
     * @return              the formatted value
     */
    public String format(Object value) {
        StringBuilder sb = new StringBuilder(64);
        try {
            formatTo(sb, value, 0);
        }
        catch (IOException ioe) {
            throw new UncheckedIOException("Should not happen - StringBuilder doesn't throw IOException", ioe);
        }
        return sb.toString();
    }

    /**
     * Format an arbitrary object as JSON by appending to an {@link Appendable}, using the line separator and
     * indentation value of the {@code Formatter}.  JSON objects are output with each property on a separate line, and
     * JSON arrays are output with each item on a line.
     *
     * @param   a           the {@link Appendable}
     * @param   value       the value to be formatted
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public void formatTo(Appendable a, Object value) throws IOException {
        formatTo(a, value, 0);
    }

    /**
     * Format an arbitrary object as JSON by appending to an {@link Appendable}, using the line separator and
     * indentation value of the {@code Formatter}, and with the supplied initial indentation value.  JSON objects are
     * output with each property on a separate line, and JSON arrays are output with each item on a line.
     *
     * @param   a               the {@link Appendable}
     * @param   value           the value to be formatted
     * @param   currentIndent   the initial indentation value
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public void formatTo(Appendable a, Object value, int currentIndent) throws IOException {
        if (value == null) {
            a.append("null");
        }
        else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>)value;
            a.append('{');
            if (map.size() > 0) {
                // potential optimisation - if size == 1 and contents size <= 1, output on one line
                appendLineSeparator(a);
                Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
                while (true) {
                    Map.Entry<?, ?> entry = iterator.next();
                    int newIndent = currentIndent + indent;
                    appendSpaces(a, newIndent);
                    appendString(a, entry.getKey().toString());
                    a.append(':');
                    a.append(' ');
                    formatTo(a, entry.getValue(), newIndent);
                    if (!iterator.hasNext())
                        break;
                    a.append(',');
                    appendLineSeparator(a);
                }
                appendLineSeparator(a);
                appendSpaces(a, currentIndent);
            }
            a.append('}');
        }
        else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>)value;
            a.append('[');
            if (collection.size() > 0) {
                // potential optimisation - if size == 1 and contents size <= 1, output on one line
                appendLineSeparator(a);
                Iterator<?> iterator = collection.iterator();
                while (true) {
                    appendSpaces(a, currentIndent + indent);
                    formatTo(a, iterator.next(), currentIndent + indent);
                    if (!iterator.hasNext())
                        break;
                    a.append(',');
                    appendLineSeparator(a);
                }
                appendLineSeparator(a);
                appendSpaces(a, currentIndent);
            }
            a.append(']');
        }
        else if (value instanceof Object[]) {
            Object[] array = (Object[])value;
            a.append('[');
            int n = array.length;
            if (n > 0) {
                // potential optimisation - if size == 1 and contents size <= 1, output on one line
                appendLineSeparator(a);
                int i = 0;
                while (true) {
                    appendSpaces(a, currentIndent + indent);
                    formatTo(a, array[i++], currentIndent + indent);
                    if (i >= n)
                        break;
                    a.append(',');
                    appendLineSeparator(a);
                }
                appendLineSeparator(a);
                appendSpaces(a, currentIndent);
            }
            a.append(']');
        }
        else {
            appendPrimitive(a, value);
        }
    }

    /**
     * Output the given {@link Object} as JSON.  JSON objects are output with each property on a separate line, and JSON
     * arrays are output with each item on a line.
     *
     * @param   value       the {@link Object} (may be {@code null})
     * @return              the JSON output
     */
    public static String output(Object value) {
        StringBuilder sb = new StringBuilder(64);
        try {
            outputTo(sb, value);
        }
        catch (IOException ioe) {
            throw new UncheckedIOException("Should not happen - StringBuilder doesn't throw IOException", ioe);
        }
        return sb.toString();
    }

    /**
     * Output the given {@link Object} as JSON by appending to an {@link Appendable}.  JSON objects are output with each
     * property on a separate line, and JSON arrays are output with each item on a line.
     *
     * @param   a           the {@link Appendable}
     * @param   value       the {@link Object} (may be {@code null})
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void outputTo(Appendable a, Object value) throws IOException {
        if (value == null) {
            a.append("null");
        }
        else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>)value;
            a.append('{');
            if (map.size() > 0) {
                Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
                while (true) {
                    Map.Entry<?, ?> entry = iterator.next();
                    appendString(a, entry.getKey().toString());
                    a.append(':');
                    outputTo(a, entry.getValue());
                    if (!iterator.hasNext())
                        break;
                    a.append(',');
                }
            }
            a.append('}');
        }
        else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>)value;
            a.append('[');
            if (collection.size() > 0) {
                Iterator<?> iterator = collection.iterator();
                while (true) {
                    outputTo(a, iterator.next());
                    if (!iterator.hasNext())
                        break;
                    a.append(',');
                }
            }
            a.append(']');
        }
        else if (value instanceof Object[]) {
            Object[] array = (Object[])value;
            a.append('[');
            int n = array.length;
            if (n > 0) {
                int i = 0;
                while (true) {
                    outputTo(a, array[i++]);
                    if (i >= n)
                        break;
                    a.append(',');
                }
            }
            a.append(']');
        }
        else {
            appendPrimitive(a, value);
        }
    }

    /**
     * Append a primitive value (string, number, boolean) to an {@link Appendable} in the form required by JSON.
     *
     * <p>This is invoked for every object that is not an array or a {@link Collection} or {@link Map}.  In the case of
     * structures created by {@link net.pwall.json.parser.Parser Parser}</p>
     *
     * @param   a           the {@link Appendable}
     * @param   value       the primitive value
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPrimitive(Appendable a, Object value) throws IOException {
        if (value instanceof Integer) {
            appendInt(a, (int)value);
        }
        else if (value instanceof Long) {
            appendLong(a, (long)value);
        }
        else if (value instanceof Number) {
            a.append(value.toString());
        }
        else if (value instanceof CharSequence) {
            appendString(a, (CharSequence)value);
        }
        else {
            String s = value.toString();
            if (s.equals("true") || s.equals("false")) // Boolean, or Boolean-like
                a.append(s);
            else
                appendString(a, s);
        }
    }

    /**
     * Append a {@link CharSequence} to an {@link Appendable} in JSON quoted string form (with non-ASCII characters
     * escaped).
     *
     * @param   a           the {@link Appendable}
     * @param   cs          the {@link CharSequence}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendString(Appendable a, CharSequence cs) throws IOException {
        a.append('"');
        for (int i = 0, n = cs.length(); i < n; i++) {
            char ch = cs.charAt(i);
            if (ch == '"' || ch == '\\') {
                a.append('\\');
                a.append(ch);
            }
            else if (ch == '\b')
                a.append("\\b");
            else if (ch == '\f')
                a.append("\\f");
            else if (ch == '\n')
                a.append("\\n");
            else if (ch == '\r')
                a.append("\\r");
            else if (ch == '\t')
                a.append("\\t");
            else if (ch < 0x20 || ch >= 0x7F) {
                a.append("\\u");
                a.append(hexDigits[(ch >> 12) & 0xF]);
                a.append(hexDigits[(ch >> 8) & 0xF]);
                a.append(hexDigits[(ch >> 4) & 0xF]);
                a.append(hexDigits[ch & 0xF]);
            }
            else
                a.append(ch);
        }
        a.append('"');
    }

    /**
     * Append an {@code int} to an {@link Appendable}.  This method outputs the digits left to right, avoiding the need
     * to allocate a separate buffer.
     *
     * @param   a           the {@link Appendable}
     * @param   i           the {@code int}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendInt(Appendable a, int i) throws IOException {
        if (i < 0) {
            if (i == Integer.MIN_VALUE)
                a.append("-2147483648");
            else {
                a.append('-');
                appendPositiveInt(a, -i);
            }
        }
        else
            appendPositiveInt(a, i);
    }

    /**
     * Append a positive {@code int} to an {@link Appendable}.  This method outputs the digits left to right, avoiding
     * the need to allocate a separate buffer.
     *
     * @param   a           the {@link Appendable}
     * @param   i           the {@code int}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPositiveInt(Appendable a, int i) throws IOException {
        if (i >= 100) {
            int n = i / 100;
            appendPositiveInt(a, n);
            i -= n * 100;
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else if (i >= 10) {
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else
            a.append(digits[i]);
    }

    /**
     * Append a {@code long} to an {@link Appendable}.  This method outputs the digits left to right, avoiding the need
     * to allocate a separate buffer.
     *
     * @param   a           the {@link Appendable}
     * @param   n           the {@code long}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendLong(Appendable a, long n) throws IOException {
        if (n < 0) {
            if (n == Long.MIN_VALUE)
                a.append("-9223372036854775808");
            else {
                a.append('-');
                appendPositiveLong(a, -n);
            }
        }
        else
            appendPositiveLong(a, n);
    }

    /**
     * Append a positive {@code long} to an {@link Appendable}.  This method outputs the digits left to right, avoiding
     * the need to allocate a separate buffer.
     *
     * @param   a           the {@link Appendable}
     * @param   n           the {@code long}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPositiveLong(Appendable a, long n) throws IOException {
        if (n >= 100) {
            long m = n / 100;
            appendPositiveLong(a, m);
            int i = (int)(n - m * 100);
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else {
            int i = (int)n;
            if (i >= 10)
                a.append(tensDigits[i]);
            a.append(digits[i]);
        }
    }

    /**
     * Get the default {@code Formatter} (a {@code Formatter} with an indentation setting of 2 and the line separator
     * determined by the system.
     *
     * @return      the default {@code Formatter}
     */
    public static Formatter getDefaultFormatter() {
        return defaultFormatter;
    }

    /**
     * Validate the indent parameter.
     *
     * @param   indent          the indentation size
     * @return                  the same value (following validation)
     * @throws  IllegalArgumentException    if the value is negative
     */
    private static int checkIndent(int indent) {
        if (indent < 0)
            throw new IllegalArgumentException("indent may not be negative");
        return indent;
    }

    /**
     * Validate the indent parameter.
     *
     * @param   lineSeparator   the line separator
     * @return                  the same value (following validation)
     * @throws  IllegalArgumentException    if the value is {@code null}
     */
    private static String checkLineSeparator(String lineSeparator) {
        if (lineSeparator == null)
            throw new IllegalArgumentException("lineSeparator may not be null");
        return lineSeparator;
    }

}
