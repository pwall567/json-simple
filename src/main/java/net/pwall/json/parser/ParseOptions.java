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
