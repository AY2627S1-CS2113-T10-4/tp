package seedu.unienable.exception;

/**
 * Shared error type for future commands receiving an invalid user-visible index.
 */
public class InvalidIndexException extends UniEnableException {
    public InvalidIndexException(String message) {
        super(message);
    }
}
