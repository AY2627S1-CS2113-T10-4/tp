package seedu.unienable.command.activity;

import java.time.LocalDate;
import java.time.LocalTime;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/**
 * Adds a validated activity to the current session's activity list.
 * File persistence is intentionally left to Branch 3's storage integration.
 */
public class AddCommand extends Command {
    // Owner: Atharva
    private final Activity activity;

    /**
     * Creates a command that will insert one activity when executed.
     *
     * @param name activity name
     * @param date activity date
     * @param startTime start time
     * @param endTime end time
     * @param demand estimated demand level
     */
    public AddCommand(String name, LocalDate date, LocalTime startTime, LocalTime endTime, DemandLevel demand) {
        this.activity = new Activity(name, date, startTime, endTime, demand, false);
    }

    /**
     * Inserts the activity before returning a message for Ui.showMessage().
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        activities.addActivity(activity);
        String message = "Added activity: " + activity.getName() + "\n"
                + "Date: " + activity.getDate() + "\n"
                + "Time: " + activity.getStartTime() + " - " + activity.getEndTime() + "\n"
                + "Demand: " + activity.getDemand() + "\n"
                + "Activities in this session: " + activities.getActivities().size();
        return new CommandResult(message, false, true);
    }
}
