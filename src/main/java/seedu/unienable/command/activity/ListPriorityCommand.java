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
 * Lists every activity by descending combined priority score, including completed
 * activities. Rows with missing ratings appear last.
 */
public class ListPriorityCommand extends Command {
    // Owner: Asyraf
    private static final Logger LOGGER = Logger.getLogger(ListPriorityCommand.class.getName());

    /**
     * Returns the priority-ordered activity list without changing any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Activity> canonicalView = activities.getCanonicalView();
        List<Activity> view = activities.getPriorityView();
        LOGGER.fine(() -> "Displayed " + view.size() + " activities in priority order.");
        String message = ActivityListFormatter.formatPriorityList(view, canonicalView);
        return new CommandResult(message, false);
    }
}
//@@author
