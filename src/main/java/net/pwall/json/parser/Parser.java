/*
 * @(#) Parser.java
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

package net.pwall.json.parser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.pwall.text.TextMatcher;
import net.pwall.util.ImmutableList;
import net.pwall.util.ImmutableMap;

/**
 * A simple JSON parser.  This class consists of static functions that parse JSON into standard Java classes, avoiding
 * the need for additional class definitions.
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
    public static final String UNTERMINATED_STRING = "Unterminated JSON string";
    public static final String ILLEGAL_CHAR = "Illegal character in JSON string";
    public static final String ILLEGAL_UNICODE_SEQUENCE = "Illegal Unicode sequence in JSON string";
    public static final String ILLEGAL_ESCAPE_SEQUENCE = "Illegal escape sequence in JSON string";

    /**
     * Parse a string of JSON and return a value of type <b><i>J</i></b> (not an actual parameterised type), where
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
     * @param   json            the JSON string
     * @return                  the object, as described above
     * @throws  ParseException  if there are any errors in the JSON
     */
    public static Object parse(String json) {
        TextMatcher tm = new TextMatcher(json);
        Object result = parse(tm, ROOT_POINTER);
        tm.skip(Parser::isSpaceCharacter);
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
     * @return                  the JSON element
     * @throws  ParseException  if there are any errors in the JSON
     */
    private static Object parse(TextMatcher tm, String pointer) {
        tm.skip(Parser::isSpaceCharacter);

        if (tm.match('{')) {
            ImmutableMap.MapEntry<String, Object>[] array = ImmutableMap.createArray(8);
            int index = 0;
            tm.skip(Parser::isSpaceCharacter);
            if (!tm.match('}')) {
                while (true) {
                    if (!tm.match('"'))
                        throw new ParseException(ILLEGAL_KEY, pointer);
                    String key = parseString(tm, pointer);
                    if (ImmutableMap.containsKey(array, index, key))
                        throw new ParseException(DUPLICATE_KEY, pointer);
                    tm.skip(Parser::isSpaceCharacter);
                    if (!tm.match(':'))
                        throw new ParseException(MISSING_COLON, pointer);
                    if (index == array.length) {
                        ImmutableMap.MapEntry<String, Object>[] newArray =
                                ImmutableMap.createArray(array.length + Math.min(array.length, 4096));
                        System.arraycopy(array, 0, newArray, 0, array.length);
                        array = newArray;
                    }
                    array[index++] = ImmutableMap.entry(key, parse(tm, pointer + '/' + key));
                    tm.skip(Parser::isSpaceCharacter);
                    if (!tm.match(','))
                        break;
                    tm.skip(Parser::isSpaceCharacter);
                }
                if (!tm.match('}'))
                    throw new ParseException(MISSING_CLOSING_BRACE, pointer);
            }
            return new ImmutableMap<>(array, index);
        }

        if (tm.match('[')) {
            Object[] array = new Object[16];
            int index = 0;
            tm.skip(Parser::isSpaceCharacter);
            if (!tm.match(']')) {
                do {
                    if (index == array.length) {
                        Object[] newArray = new Object[array.length + Math.min(array.length, 4096)];
                        System.arraycopy(array, 0, newArray, 0, array.length);
                        array = newArray;
                    }
                    array[index] = parse(tm, pointer + '/' + index);
                    index++;
                    tm.skip(Parser::isSpaceCharacter);
                } while (tm.match(','));
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
        if (tm.matchDec(0, 1)) {
            int integerLength = tm.getResultLength();
            if (integerLength > 1 && tm.getResultChar() == '0')
                throw new ParseException(ILLEGAL_NUMBER, pointer);
            boolean floating = false;
            if (tm.match('.')) {
                floating = true;
                if (!tm.matchDec(0, 1))
                    throw new ParseException(ILLEGAL_NUMBER, pointer);
            }
            if (tm.match('e') || tm.match('E')) {
                floating = true;
                tm.matchAny("-+"); // ignore the result, just step the index
                if (!tm.matchDec(0, 1))
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
        int start = tm.getIndex();
        while (true) {
            if (tm.isAtEnd())
                throw new ParseException(UNTERMINATED_STRING, pointer);
            char ch = tm.nextChar();
            if (ch == '"')
                return tm.getString(start, tm.getStart());
            if (ch == '\\')
                break;
            if (ch < 0x20)
                throw new ParseException(ILLEGAL_CHAR, pointer);
        }
        StringBuilder sb = new StringBuilder(tm.getCharSeq(start, tm.getStart()));
        while (true) {
            if (tm.isAtEnd())
                throw new ParseException(UNTERMINATED_STRING, pointer);
            char ch = tm.nextChar();
            if (ch == '"')
                sb.append('"');
            else if (ch == '\\')
                sb.append('\\');
            else if (ch == '/')
                sb.append('/');
            else if (ch == 'b')
                sb.append('\b');
            else if (ch == 'f')
                sb.append('\f');
            else if (ch == 'n')
                sb.append('\n');
            else if (ch == 'r')
                sb.append('\r');
            else if (ch == 't')
                sb.append('\t');
            else if (ch == 'u') {
                if (!tm.matchHex(4, 4))
                    throw new ParseException(ILLEGAL_UNICODE_SEQUENCE, pointer);
                sb.append((char)tm.getResultHexInt());
            }
            else
                throw new ParseException(ILLEGAL_ESCAPE_SEQUENCE, pointer);
            while (true) {
                if (tm.isAtEnd())
                    throw new ParseException(UNTERMINATED_STRING, pointer);
                ch = tm.nextChar();
                if (ch == '"')
                    return sb.toString();
                if (ch == '\\')
                    break;
                if (ch < 0x20)
                    throw new ParseException(ILLEGAL_CHAR, pointer);
                sb.append(ch);
            }
        }
    }

    /**
     * Test whether a given character is a space, according to the JSON specification.
     *
     * @param   ch          the character
     * @return              {@code true} if the character is a space
     */
    private static boolean isSpaceCharacter(int ch) {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
    }

}
