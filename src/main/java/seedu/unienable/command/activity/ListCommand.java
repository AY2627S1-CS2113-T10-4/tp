package seedu.unienable.command.activity;

import java.util.List;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.activity.ActivityListFormatter;

/**
 * Lists all activities in the canonical order (by date, then start time).
 */
public class ListCommand extends Command {
    // Owner: Asyraf

    /**
     * Returns the canonical activity list for Ui.showMessage() without changing
     * any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Activity> view = activities.getCanonicalView();
        String message = ActivityListFormatter.formatList(view);
        return new CommandResult(message, false);
    }
}
