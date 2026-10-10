package seedu.unienable.exception;

/**
 * Base checked exception for errors that can be explained to the user.
 */
public class UniEnableException extends Exception {
    public UniEnableException(String message) {
        super(message);
    }
}
