package seedu.unienable.command;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    // add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]
    private final String description;
    private final String date;
    private final String startTime;
    private final String endTime;

    public AddCommand(String description, String date, String startTime, String endTime) {
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        return new CommandResult("Added! " + this.description, false);
    }
}
