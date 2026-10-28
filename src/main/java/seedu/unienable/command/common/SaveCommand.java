//@@author Oliver-koh
package seedu.unienable.command.common;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/** Explicitly saves the current activities, allowing a failed automatic save to be retried. */
public class SaveCommand extends Command {

    /**
     * Creates a command that explicitly retries activity persistence.
     */
    public SaveCommand() {
    }
    /**
     * Saves without changing activities or asking the console to save a second time.
     *
     * @throws UniEnableException if the storage or recovery backup is still unavailable.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        storage.save(activities);
        return new CommandResult("Activities saved.", false);
    }
}
//@@author
