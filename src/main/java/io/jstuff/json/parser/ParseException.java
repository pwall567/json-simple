/*
 * @(#) ParseException.java
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

package io.jstuff.json.parser;

import io.jstuff.json.JSONSimpleException;

/**
 * Exception class for parse exceptions.  Includes a string in
 * <a href="https://tools.ietf.org/html/rfc6901">JSON Pointer</a> form indicating the point in a nested JSON structure
 * where the error occurred.
 *
 * @author  Peter Wall
 */
public class ParseException extends JSONSimpleException {

    private final String text;
    private final String pointer;

    /**
     * Construct a {@code ParseException} with the given error message and pointer.
     *
     * @param   text        the error message
     * @param   pointer     the JSON Pointer (in string form)
     */
    public ParseException(String text, String pointer) {
        super(pointer.isEmpty() ? text : text + " at " + pointer);
        this.text = text;
        this.pointer = pointer;
    }

    /**
     * Construct a {@code ParseException} with the given error message and no pointer.
     *
     * @param   text        the error message
     */
    public ParseException(String text) {
        super(text);
        this.text = text;
        this.pointer = "";
    }

    /**
     * Get the text of the error message (excluding pointer).
     *
     * @return          the text
     */
    public String getText() {
        return text;
    }

    /**
     * Return the JSON Pointer (in string form) associated with the error message.
     *
     * @return          the JSON Pointer
     */
    public String getPointer() {
        return pointer;
    }

}
