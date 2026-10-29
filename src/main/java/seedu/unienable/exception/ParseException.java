package seedu.unienable.exception;

import java.io.Serial;

/**
 * Reports input that cannot be parsed as a supported command.
 */
public class ParseException extends UniEnableException {
    /** Identifies the serialized form of this input error. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with the warning or usage text to display.
     *
     * @param message explanation of the unsupported input.
     */
    public ParseException(String message) {
        super(message);
    }
}
