package seedu.unienable.ui.activity;

import java.util.List;

import seedu.unienable.model.Activity;

/**
 * Builds user-facing activity list messages from pre-sorted views supplied by commands.
 * Formatting is pure: it renders the given order as-is and never sorts, filters,
 * reads input, or prints to the console.
 */
public final class ActivityListFormatter {
    private static final String LIST_HEADING = "Activities (by date, start time):";

    private static final String DEMAND_LIST_HEADING = "Activities by demand (HIGH to LOW):";

    /**
     * Explains that deletion indexes refer to the chronological list, not the demand view.
     */
    private static final String DEMAND_LIST_INDEX_NOTICE =
            "Note: Do not use indexes from 'list demand' for deletion.\n"
            + "Run 'list' to find the correct index for 'delete INDEX'.";

    /**
     * Agreed v1.0 empty-list placeholder.
     */
    private static final String EMPTY_MESSAGE = "No activities yet.";

    /**
     * Prevents construction of this utility class.
     */
    private ActivityListFormatter() {
    }

    /**
     * Formats the canonical view; numbering starts at 1 to match what
     * {@code delete INDEX} resolves against.
     *
     * @param activities activities in chronological display order.
     * @return the heading and numbered activities, or the empty-list placeholder
     */
    public static String formatList(List<Activity> activities) {
        return format(LIST_HEADING, activities);
    }

    /**
     * Formats the demand view with a notice explaining which indexes deletion uses.
     * The notice appears before the activities or the empty-list placeholder.
     *
     * @param activities activities in demand display order.
     * @return the heading, deletion-index notice and formatted activities
     */
    public static String formatDemandList(List<Activity> activities) {
        return format(DEMAND_LIST_HEADING + "\n" + DEMAND_LIST_INDEX_NOTICE, activities);
    }

    private static String format(String heading, List<Activity> activities) {
        StringBuilder output = new StringBuilder(heading);
        if (activities.isEmpty()) {
            output.append("\n").append(EMPTY_MESSAGE);
            return output.toString();
        }
        for (int i = 0; i < activities.size(); i++) {
            output.append("\n").append(i + 1).append(". ")
                    .append(describe(activities.get(i)));
        }
        return output.toString();
    }

    /**
     * Matches the add confirmation's field formats (ISO date, 24-hour times, enum demand).
     */
    private static String describe(Activity activity) {
        return activity.getName()
                + " (" + activity.getDate()
                + ", " + activity.getStartTime()
                + "-" + activity.getEndTime()
                + ") [" + activity.getDemand() + "] "
                + (activity.isDone() ? "[X]" : "[ ]");
    }
}
