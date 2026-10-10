package seedu.unienable.command;

/**
 * Carries a user-facing message and whether the console loop should exit.
 */
public class CommandResult {
    private final String message;
    private final boolean isExit;

    public CommandResult(String message, boolean isExit) {
        this.message = message;
        this.isExit = isExit;
    }

    public String getMessage() {
        return message;
    }

    public boolean isExit() {
        return isExit;
    }
}
