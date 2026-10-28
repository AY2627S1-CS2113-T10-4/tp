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
 * Lists all activities in the canonical order (by date, then start time).
 */
public class ListCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ListCommand.class.getName());

    /**
     * Creates a read-only chronological activity listing.
     */
    public ListCommand() {
    }

    /**
     * Returns the canonical activity list for Ui.showMessage() without changing
     * any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Activity> view = activities.getCanonicalView();
        LOGGER.fine(() -> "Displayed " + view.size() + " activities in chronological order.");
        String message = ActivityListFormatter.formatList(view);
        return new CommandResult(message, false);
    }
}
//@@author
