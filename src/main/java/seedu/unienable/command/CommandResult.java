package seedu.unienable.command;

/**
 * Carries a user-facing message and whether the console loop should exit.
 */
public class CommandResult {
    private final String message;
    private final boolean isExit;
    private final boolean didChangeActivities;

    public CommandResult(String message, boolean isExit) {
        this(message, isExit, false);
    }

    /**
     * Creates a command result with an explicit activity-mutation flag.
     *
     * @param message user-facing result message
     * @param isExit whether the console loop should exit
     * @param didChangeActivities whether activity data was changed successfully
     */
    public CommandResult(String message, boolean isExit, boolean didChangeActivities) {
        this.message = message;
        this.isExit = isExit;
        this.didChangeActivities = didChangeActivities;
    }

    public String getMessage() {
        return message;
    }

    public boolean isExit() {
        return isExit;
    }

    /**
     * Indicates whether the command changed activity data and should be persisted.
     *
     * @return true when activity data changed
     */
    public boolean didChangeActivities() {
        return didChangeActivities;
    }
}
