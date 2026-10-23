package seedu.unienable.command;

import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

//@@author Atharva005
/**
 * Displays the commands currently available in UniEnable, including optional add ratings.
 */
public class HelpCommand extends Command {
    // Owner: Atharva
    /**
     * Displays help through the ordinary CommandResult to Ui.showMessage() path.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        String message = "UniEnable commands:\n"
                + "help - Show this message\n"
                + "add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH] "
                + "[imp/IMP] [urg/URG] [com/COM]\n"
                + "    Add an activity (demand defaults to MEDIUM; end must be after now)\n"
                + "    IMP = importance; URG = urgency; COM = commitment\n"
                + "    Ratings are optional whole numbers from 1 to 5; omitted ratings display -.\n"
                + "edit INDEX [n/NAME] [d/YYYY-MM-DD] [s/HH:mm] [e/HH:mm] "
                + "[dem/LOW|MEDIUM|HIGH] [imp/IMP] [urg/URG] [com/COM]\n"
                + "    Update at least one field; omitted fields and completion are preserved\n"
                + "    If date or time is supplied, the resulting end must be after its start and now\n"
                + "find KEYWORD - Search activity names by case-insensitive substring\n"
                + "    Multiword keywords are literal phrases; results keep chronological list indexes\n"
                + "list - View activities by date and start time\n"
                + "list demand - View activities by demand, HIGH to LOW\n"
                + "demand today - Count today's HIGH-demand activities, including completed ones\n"
                + "list priority - View all activities by combined priority score\n"
                + "    Score = URG x URG x (IMP + COM); missing ratings display NA\n"
                + "overdue - View incomplete activities that started before now\n"
                + "next - View the earliest incomplete activity that starts after now\n"
                + "mark INDEX - Mark an activity as done\n"
                + "unmark INDEX - Mark an activity as incomplete\n"
                + "delete INDEX - Delete an activity\n"
                + "    Use indexes from list for mark, unmark, edit, and delete\n"
                + "facility list - List facility reference information\n"
                + "facility LOCATION - View a facility (e.g. facility AS4)\n"
                + "facility find type/FEATURE [status/YES|NO|UNKNOWN] - Search facilities\n"
                + "route from/START to/END - Find a shortest recorded accessible route\n"
                + "bye - Exit the application\n"
                + "Activity changes are saved automatically to data/activities.txt.";
        return new CommandResult(message, false);
    }
}
//@@author
