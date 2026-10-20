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
 * Adds a validated, initially incomplete activity to the current session's activity list.
 * The console saves activities when the command result reports a change.
 */
public class AddCommand extends Command {
    // Owner: Atharva
    private final Activity activity;

    /**
     * Creates a command with all ratings unset, preserving the V1.0 constructor contract.
     *
     * @param name activity name.
     * @param date activity date.
     * @param startTime start time.
     * @param endTime end time.
     * @param demand estimated demand level.
     */
    public AddCommand(String name, LocalDate date, LocalTime startTime, LocalTime endTime, DemandLevel demand) {
        this(name, date, startTime, endTime, demand, null, null, null);
    }

    /**
     * Creates a command that will insert one incomplete activity with optional ratings.
     *
     * @param name activity name.
     * @param date activity date.
     * @param startTime start time.
     * @param endTime end time.
     * @param demand estimated demand level.
     * @param importance importance from 1 to 5, or null when unset.
     * @param urgency urgency from 1 to 5, or null when unset.
     * @param commitment commitment from 1 to 5, or null when unset.
     * @throws IllegalArgumentException if a supplied rating is outside 1 to 5.
     */
    public AddCommand(String name, LocalDate date, LocalTime startTime, LocalTime endTime, DemandLevel demand,
            Integer importance, Integer urgency, Integer commitment) {
        this.activity = new Activity(name, date, startTime, endTime, demand, false,
                importance, urgency, commitment);
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
                + "Importance: " + formatRating(activity.getImportance()) + "\n"
                + "Urgency: " + formatRating(activity.getUrgency()) + "\n"
                + "Commitment: " + formatRating(activity.getCommitment()) + "\n"
                + "Activities in this session: " + activities.getActivities().size();
        return new CommandResult(message, false, true);
    }

    /**
     * Displays an unset rating as a dash while preserving numeric values.
     */
    private static String formatRating(Integer rating) {
        return rating == null ? "-" : rating.toString();
    }
}
