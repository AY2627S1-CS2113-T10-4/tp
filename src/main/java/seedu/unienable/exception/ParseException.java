package seedu.unienable.exception;

/**
 * Reports input that cannot be parsed as a supported command.
 */
public class ParseException extends UniEnableException {
    public ParseException(String message) {
        super(message);
    }
}
