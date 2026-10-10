package seedu.unienable.command.activity;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Deletes one activity chosen by its 1-based displayed index in the canonical list.
 */
public class DeleteCommand extends Command {
    // Owner: Asyraf
    private final int displayedIndex;

    /**
     * Creates a command that will delete one activity when executed.
     *
     * @param displayedIndex 1-based index shown by {@code list}, resolved by the model
     */
    public DeleteCommand(int displayedIndex) {
        this.displayedIndex = displayedIndex;
    }

    /**
     * Deletes the activity at the displayed index and returns a confirmation message
     * for Ui.showMessage(). An out-of-range index surfaces as an InvalidIndexException,
     * whose [WARNING] message is displayed by ConsoleHelper.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        Activity activity = activities.deleteByDisplayedIndex(displayedIndex);
        String message = "Deleted activity: " + activity.getName() + "\n"
                + "Date: " + activity.getDate() + "\n"
                + "Time: " + activity.getStartTime() + " - " + activity.getEndTime() + "\n"
                + "Demand: " + activity.getDemand() + "\n"
                + "Activities in this session: " + activities.getActivities().size();
        return new CommandResult(message, false);
    }
}
