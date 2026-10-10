package seedu.unienable.command.activity;

import java.util.List;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.activity.ActivityListFormatter;

/**
 * Lists all activities grouped by demand (HIGH to LOW), ties in canonical order.
 */
public class ListDemandCommand extends Command {
    // Owner: Asyraf

    /**
     * Returns the demand-ordered activity list for Ui.showMessage() without
     * changing any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Activity> view = activities.getDemandView();
        String message = ActivityListFormatter.formatDemandList(view);
        return new CommandResult(message, false);
    }
}
