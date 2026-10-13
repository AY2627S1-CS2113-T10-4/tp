package seedu.unienable.command;

import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Displays commands currently available in this Branch 1 sample.
 */
public class HelpCommand extends Command {
    // Owner: Atharva
    /**
     * Displays help through the ordinary CommandResult to Ui.showMessage() path.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        String message = "UniEnable commands (Branch 1 sample):\n"
                + "help - Show this message\n"
                + "add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]\n"
                + "    Add an activity for this session (demand defaults to MEDIUM)\n"
                + "list or list demand - View the activities\n"
                + "mark 1 or unmark 1 - Mark or unmark the event"
                + "facility list - List facility reference information\n"
                + "facility LOCATION - View a facility (e.g. facility AS4)\n"
                + "facility find type/FEATURE [status/YES|NO|UNKNOWN] - Search facilities\n"
                + "route from/as6 to/as7 - Find shortest accessible route\n"
                + "bye - Exit the application\n"
                + "Activity list/delete/mark and file saving await Branches 2 and 3.";
        return new CommandResult(message, false);
    }
}
