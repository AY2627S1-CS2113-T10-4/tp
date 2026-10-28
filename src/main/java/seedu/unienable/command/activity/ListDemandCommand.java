package seedu.unienable.command.activity;

import java.util.List;
import java.util.logging.Logger;

//@@author lukelouyu
import seedu.unienable.command.common.Command;
import seedu.unienable.command.common.CommandResult;
//@@author
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.activity.ActivityListFormatter;

//@@author officialasyrafrahim
/**
 * Lists all activities grouped by demand (HIGH to LOW), ties in canonical order.
 */
public class ListDemandCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ListDemandCommand.class.getName());

    /**
     * Returns the demand-ordered activity list for Ui.showMessage() without
     * changing any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Activity> view = activities.getDemandView();
        LOGGER.fine(() -> "Displayed " + view.size() + " activities grouped by demand.");
        String message = ActivityListFormatter.formatDemandList(view, activities.getCanonicalView());
        return new CommandResult(message, false);
    }
}
//@@author
