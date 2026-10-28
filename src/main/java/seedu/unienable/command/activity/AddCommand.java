package seedu.unienable.command.activity;

//@@author Atharva005
import java.time.LocalDate;
import java.time.LocalTime;


import seedu.unienable.command.common.Command;
import seedu.unienable.command.common.CommandResult;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;


/**
 * Adds a validated, initially incomplete activity to the current session's activity list.
 * The console saves activities when the command result reports a change.
 */
public class AddCommand extends Command {
    private final Activity activity;
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
     * Checks the schedule before inserting the activity and reporting success.
     *
     * @throws UniEnableException if the activity duplicates or overlaps an existing activity.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        ActivityScheduleValidator.validate(activity, activities, null);
        activities.addActivity(activity);
        String message = "Added activity: " + activity.getName() + "\n"
                + "Date: " + activity.getDate() + "\n"
                + "Time: " + activity.getStartTime() + " - " + activity.getEndTime() + "\n"
                + "Demand: " + activity.getDemand() + "\n"
                //@@author lukelouyu
                + "Importance: " + formatRating(activity.getImportance()) + "\n"
                + "Urgency: " + formatRating(activity.getUrgency()) + "\n"
                + "Commitment: " + formatRating(activity.getCommitment()) + "\n"
                //@@author
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
//@@author
