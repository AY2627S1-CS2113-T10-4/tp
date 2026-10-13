package seedu.unienable.command;

import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Displays the commands available in UniEnable v1.0.
 */
public class HelpCommand extends Command {
    // Owner: Atharva
    /**
     * Displays help through the ordinary CommandResult to Ui.showMessage() path.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        String message = "UniEnable commands (v1.0):\n"
                + "help - Show this message\n"
                + "add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]\n"
                + "    Add an activity (demand defaults to MEDIUM; end must be after now)\n"
                + "list - View activities by date and start time\n"
                + "list demand - View activities by demand, HIGH to LOW\n"
                + "mark INDEX - Mark an activity as done\n"
                + "unmark INDEX - Mark an activity as incomplete\n"
                + "delete INDEX - Delete an activity\n"
                + "    Use indexes from list for mark, unmark, and delete\n"
                + "facility list - List facility reference information\n"
                + "facility LOCATION - View a facility (e.g. facility AS4)\n"
                + "facility find type/FEATURE [status/YES|NO|UNKNOWN] - Search facilities\n"
                + "route from/START to/END - Find a shortest recorded accessible route\n"
                + "bye - Exit the application\n"
                + "Activity changes are saved automatically to data/activities.txt.";
        return new CommandResult(message, false);
    }
}
