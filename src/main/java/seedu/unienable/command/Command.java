package seedu.unienable.command;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Common execution contract for commands introduced by feature owners.
 */
public abstract class Command {
    /**
     * Executes against the shared activity collection and persistence service.
     */
    public abstract CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException;
}
