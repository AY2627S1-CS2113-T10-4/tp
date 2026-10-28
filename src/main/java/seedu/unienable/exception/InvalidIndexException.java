package seedu.unienable.exception;

import java.io.Serial;

/**
 * Reports a user-visible index outside the current chronological activity list.
 */
public class InvalidIndexException extends UniEnableException {
    /** Identifies the serialized form of this index error. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an index error with the available range or empty-list warning.
     *
     * @param message explanation of why the index cannot be resolved.
     */
    public InvalidIndexException(String message) {
        super(message);
    }
}
