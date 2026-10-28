package seedu.unienable.exception;

import java.io.Serial;

/**
 * Base checked exception for errors that can be explained to the user.
 */
public class UniEnableException extends Exception {
    /** Identifies the serialized form of this recoverable error. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an error with a message that the console can display to the user.
     *
     * @param message description of the recoverable failure.
     */
    public UniEnableException(String message) {
        super(message);
    }
}
