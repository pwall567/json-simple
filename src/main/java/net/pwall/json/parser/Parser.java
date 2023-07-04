/*
 * @(#) Parser.java
 *
 * json-simple  Simple JSON Parser and Formatter
 * Copyright (c) 2021, 2022, 2023 Peter Wall
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

package net.pwall.json.parser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.pwall.json.JSONFunctions;
import net.pwall.text.TextMatcher;
import net.pwall.util.ImmutableList;
import net.pwall.util.ImmutableMap;
import net.pwall.util.ImmutableMapEntry;

/**
 * A simple JSON parser.  This class consists of static functions that parse JSON into standard Java classes, avoiding
 * the need for additional class definitions.
 *
 * The {@code parse} functions return a value of type <b><i>J</i></b> (not an actual parameterised type), where
 * <b><i>J</i></b> is one of the following:
 *
 * <dl>
 *   <dt>{@link String}</dt>
 *   <dd>for a JSON string</dd>
 *   <dt>{@link Integer}</dt>
 *   <dd>for a JSON integer, up to 32 bits</dd>
 *   <dt>{@link Long}</dt>
 *   <dd>for a JSON integer, 33 to 64 bits</dd>
 *   <dt>{@link BigDecimal}</dt>
 *   <dd>for all other JSON numbers, including floating point</dd>
 *   <dt>{@link Boolean}</dt>
 *   <dd>for the JSON keywords {@code true} and {@code false}</dd>
 *   <dt>{@link List}&lt;<b><i>J</i></b>&gt;</dt>
 *   <dd>for a JSON array</dd>
 *   <dt>{@link Map}&lt;{@link String}, <b><i>J</i></b>&gt;</dt>
 *   <dd>for a JSON object</dd>
 *   <dt>{@code null}</dt>
 *   <dd>for the JSON keyword {@code null}</dd>
 * </dl>
 *
 * @author  Peter Wall
 */
public class Parser {

    public static final String ROOT_POINTER = "";
    public static final int MAX_INTEGER_DIGITS_LENGTH = 10;

    public static final String EXCESS_CHARS = "Excess characters following JSON";
    public static final String ILLEGAL_NUMBER = "Illegal JSON number";
    public static final String ILLEGAL_SYNTAX = "Illegal JSON syntax";
    public static final String ILLEGAL_KEY = "Illegal key in JSON object";
    public static final String DUPLICATE_KEY = "Duplicate key in JSON object";
    public static final String MISSING_COLON = "Missing colon in JSON object";
    public static final String MISSING_CLOSING_BRACE = "Missing closing brace in JSON object";
    public static final String MISSING_CLOSING_BRACKET = "Missing closing bracket in JSON array";
    public static final String MAX_DEPTH_EXCEEDED = "Maximum nesting depth exceeded";
    public static final String MAX_DEPTH_ERROR = "Maximum nesting depth must be 1..1200";

    private static final ParseOptions defaultOptions =
            new ParseOptions(ParseOptions.DuplicateKeyOption.ERROR, false, false, false, 1000);

    /**
     * Parse a string of JSON and return a value as described in the class documentation.
     *
     * @param   json            the JSON string
     * @return                  the parsed object
     * @throws  ParseException  if there are any errors in the JSON
     */
    public static Object parse(String json) {
        return parse(json, defaultOptions);
    }

    /**
     * Parse a string of JSON and return a value as described in the class documentation, using a {@link ParseOptions}
     * object to specify non-standard parsing options.
     *
     * @param   json            the JSON string
     * @param   options         a {@link ParseOptions} object
     * @return                  the parsed object
     * @throws  ParseException  if there are any errors in the JSON
     */
    public static Object parse(String json, ParseOptions options) {
        TextMatcher tm = new TextMatcher(json);
        Object result = parse(tm, options, ROOT_POINTER, 0);
        tm.skip(JSONFunctions::isSpaceCharacter);
        if (!tm.isAtEnd())
            throw new ParseException(EXCESS_CHARS);
        return result;
    }

    /**
     * Parse a JSON element from the current position of a {@link TextMatcher}.  The index is left positioned after the
     * end of the element.
     *
     * @param   tm              a {@link TextMatcher}
     * @param   pointer         a string in <a href="https://tools.ietf.org/html/rfc6901">JSON Pointer</a> syntax
     *                          describing the position in the result JSON (for use in error reporting)
     * @param   depth           the current nesting depth
     * @return                  the JSON element
     * @throws  ParseException  if there are any errors in the JSON
     */
    private static Object parse(TextMatcher tm, ParseOptions options, String pointer, int depth) {
        if (depth > options.getMaximumNestingDepth())
            throw new ParseException(MAX_DEPTH_EXCEEDED);

        tm.skip(JSONFunctions::isSpaceCharacter);

        if (tm.match('{')) {
            ImmutableMapEntry<String, Object>[] array = ImmutableMap.createArray(8);
            int index = 0;
            tm.skip(JSONFunctions::isSpaceCharacter);
            if (!tm.match('}')) {
                while (true) {
                    String key;
                    if (tm.match('"'))
                        key = parseString(tm, pointer);
                    else if (options.getObjectKeyUnquoted() && matchIdentifier(tm))
                        key = tm.getResult();
                    else
                        throw new ParseException(ILLEGAL_KEY, pointer);
                    tm.skip(JSONFunctions::isSpaceCharacter);
                    if (!tm.match(':'))
                        throw new ParseException(MISSING_COLON, pointer);
                    if (index == array.length) {
                        ImmutableMapEntry<String, Object>[] newArray =
                                ImmutableMap.createArray(array.length + Math.min(array.length, 4096));
                        System.arraycopy(array, 0, newArray, 0, array.length);
                        array = newArray;
                    }
                    Object value = parse(tm, options, pointer + '/' + key, depth + 1);
                    int i = ImmutableMap.findKey(array, index, key);
                    if (i >= 0) {
                        switch (options.getObjectKeyDuplicate()) {
                            case ERROR:
                                duplicateKey(key, pointer);
                            case CHECK_IDENTICAL:
                                if (!array[i].getValue().equals(value))
                                    duplicateKey(key, pointer);
                                break;
                            case TAKE_LAST:
                                System.arraycopy(array, i + 1, array, i, index - 1);
                                array[index - 1] = ImmutableMap.entry(key, value);
                            // case TAKE_FIRST does nothing
                        }
                    }
                    else
                        array[index++] = ImmutableMap.entry(key, value);
                    tm.skip(JSONFunctions::isSpaceCharacter);
                    if (!tm.match(','))
                        break;
                    tm.skip(JSONFunctions::isSpaceCharacter);
                    if (options.getObjectTrailingComma() && tm.match('}')) {
                        tm.revert();
                        break;
                    }
                }
                if (!tm.match('}'))
                    throw new ParseException(MISSING_CLOSING_BRACE, pointer);
            }
            return ImmutableMap.mapOf(array, index);
        }

        if (tm.match('[')) {
            Object[] array = new Object[16];
            int index = 0;
            tm.skip(JSONFunctions::isSpaceCharacter);
            if (!tm.match(']')) {
                while (true) {
                    if (index == array.length) {
                        Object[] newArray = new Object[array.length + Math.min(array.length, 4096)];
                        System.arraycopy(array, 0, newArray, 0, array.length);
                        array = newArray;
                    }
                    array[index] = parse(tm, options, pointer + '/' + index, depth + 1);
                    index++;
                    tm.skip(JSONFunctions::isSpaceCharacter);
                    if (!tm.match(','))
                        break;
                    tm.skip(JSONFunctions::isSpaceCharacter);
                    if (options.getArrayTrailingComma() && tm.match(']')) {
                        tm.revert();
                        break;
                    }
                }
                if (!tm.match(']'))
                    throw new ParseException(MISSING_CLOSING_BRACKET, pointer);
            }
            return ImmutableList.listOf(array, index);
        }

        if (tm.match('"'))
            return parseString(tm, pointer);

        if (tm.match("true"))
            return Boolean.TRUE;

        if (tm.match("false"))
            return Boolean.FALSE;

        if (tm.match("null"))
            return null;

        int numberStart = tm.getIndex();
        boolean negative = tm.match('-');
        if (tm.matchDec()) {
            int integerLength = tm.getResultLength();
            if (integerLength > 1 && tm.getResultChar() == '0')
                throw new ParseException(ILLEGAL_NUMBER, pointer);
            boolean floating = false;
            if (tm.match('.')) {
                floating = true;
                if (!tm.matchDec())
                    throw new ParseException(ILLEGAL_NUMBER, pointer);
            }
            if (tm.match(ch -> ch == 'e' || ch == 'E')) {
                floating = true;
                tm.match(ch -> ch == '-' || ch == '+'); // ignore the result, just step the index
                if (!tm.matchDec())
                    throw new ParseException(ILLEGAL_NUMBER, pointer);
            }
            if (!floating) {
                if (integerLength < MAX_INTEGER_DIGITS_LENGTH)
                    return tm.getResultInt(negative);
                try {
                    long result = tm.getResultLong(negative);
                    if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE)
                        return (int)result;
                    return result;
                } catch (NumberFormatException ignore) {
                    // too big for Long - drop through to BigDecimal
                }
            }
            return new BigDecimal(tm.getString(numberStart, tm.getIndex()));
        }

        throw new ParseException(ILLEGAL_SYNTAX, pointer);
    }

    /**
     * Parse a JSON string from the current position of a {@link TextMatcher} (which must be positioned after the
     * opening double quote).  The index is left positioned after the closing double quote.
     *
     * @param   tm              a {@link TextMatcher}
     * @param   pointer         a string in <a href="https://tools.ietf.org/html/rfc6901">JSON Pointer</a> syntax
     *                          describing the position in the result JSON (for use in error reporting)
     * @return                  the JSON string
     * @throws  ParseException  if there are any errors in the JSON
     */
    private static String parseString(TextMatcher tm, String pointer) {
        try {
            return JSONFunctions.parseString(tm);
        }
        catch (IllegalArgumentException iae) {
            throw new ParseException(iae.getMessage(), pointer);
        }
    }

    private static void duplicateKey(String key, String pointer) {
        throw new ParseException(DUPLICATE_KEY + " \"" + key + '"', pointer);
    }

    private static boolean matchIdentifier(TextMatcher tm) {
        return tm.match(ch -> ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '_') &&
                tm.matchContinue(ch -> ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' ||
                        ch == '_');
    }

    private static String findStartOfRecursion(String pointer) {
        String result = pointer;
        int i = result.lastIndexOf('/');
        if (i > 0) {
            String previous = result.substring(i);
            result = result.substring(0, i);
            while (result.length() > 0) {
                i = result.lastIndexOf('/');
                if (i < 0)
                    break;
                String node = result.substring(i);
                if (!result.substring(i).equals(previous))
                    break;
            }
        }
        return result;
    }

}
