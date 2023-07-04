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

import static net.pwall.json.parser.Parser.MAX_DEPTH_ERROR;

public class ParseOptions {

    private final DuplicateKeyOption objectKeyDuplicate;
    private final boolean objectKeyUnquoted;
    private final boolean objectTrailingComma;
    private final boolean arrayTrailingComma;
    private final int maximumNestingDepth;

    public ParseOptions(
            DuplicateKeyOption objectKeyDuplicate,
            boolean objectKeyUnquoted,
            boolean objectTrailingComma,
            boolean arrayTrailingComma,
            int maximumNestingDepth
    ) {
        if (maximumNestingDepth < 1 || maximumNestingDepth > 1200)
            throw new IllegalArgumentException(MAX_DEPTH_ERROR + ", was " + maximumNestingDepth);
        this.objectKeyDuplicate = objectKeyDuplicate;
        this.objectKeyUnquoted = objectKeyUnquoted;
        this.objectTrailingComma = objectTrailingComma;
        this.arrayTrailingComma = arrayTrailingComma;
        this.maximumNestingDepth = maximumNestingDepth;
    }

    public DuplicateKeyOption getObjectKeyDuplicate() {
        return objectKeyDuplicate;
    }

    public boolean getObjectKeyUnquoted() {
        return objectKeyUnquoted;
    }

    public boolean getObjectTrailingComma() {
        return objectTrailingComma;
    }

    public boolean getArrayTrailingComma() {
        return arrayTrailingComma;
    }

    public int getMaximumNestingDepth() {
        return maximumNestingDepth;
    }

    public enum DuplicateKeyOption { ERROR, TAKE_FIRST, TAKE_LAST, CHECK_IDENTICAL }

}
