/*
 * @(#) ParseOptions.java
 *
 * json-simple  Simple JSON Parser and Formatter
 * Copyright (c) 2022 Peter Wall
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

public class ParseOptions {

    private final DuplicateKeyOption objectKeyDuplicate;
    private final Boolean objectKeyUnquoted;
    private final Boolean objectTrailingComma;
    private final Boolean arrayTrailingComma;

    public ParseOptions(
            DuplicateKeyOption objectKeyDuplicate,
            Boolean objectKeyUnquoted,
            Boolean objectTrailingComma,
            Boolean arrayTrailingComma
    ) {
        this.objectKeyDuplicate = objectKeyDuplicate;
        this.objectKeyUnquoted = objectKeyUnquoted;
        this.objectTrailingComma = objectTrailingComma;
        this.arrayTrailingComma = arrayTrailingComma;
    }

    public DuplicateKeyOption getObjectKeyDuplicate() {
        return objectKeyDuplicate;
    }

    public Boolean getObjectKeyUnquoted() {
        return objectKeyUnquoted;
    }

    public Boolean getObjectTrailingComma() {
        return objectTrailingComma;
    }

    public Boolean getArrayTrailingComma() {
        return arrayTrailingComma;
    }

    public enum DuplicateKeyOption { ERROR, TAKE_FIRST, TAKE_LAST, CHECK_IDENTICAL }

}
