//@@author Atharva005
package seedu.unienable.command.activity;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import seedu.unienable.command.common.Command;
import seedu.unienable.command.common.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/**
 * Displays scheduled activities and rating summaries for a day or today and the following six days.
 */
public class ViewCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ViewCommand.class.getName());
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("EEE dd MMM", Locale.ENGLISH);
    private static final DateTimeFormatter WEEK_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);

    private final Range range;
    private final Clock clock;

    /**
     * The supported date ranges for a view command.
     */
    public enum Range {
        TODAY, TOMORROW, WEEK
    }

    /**
     * Creates a date view using the system clock.
     */
    public ViewCommand(Range range) {
        this(range, Clock.systemDefaultZone());
    }

    /**
     * Creates a date view with an injectable clock for deterministic date selection.
     */
    public ViewCommand(Range range, Clock clock) {
        this.range = range;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        LocalDate today = LocalDate.now(clock);
        LocalDate start = range == Range.TOMORROW ? today.plusDays(1) : today;
        LocalDate end = range == Range.WEEK ? start.plusDays(6) : start;
        List<Activity> view = activities.getViewBetween(start, end);
        LOGGER.fine(() -> "Displayed " + view.size() + " activities for " + range + ".");
        return new CommandResult(format(start, end, view), false);
    }

    /**
     * Renders a warning for an empty range, or its schedule and aggregate sections when populated.
     */
    private String format(LocalDate start, LocalDate end, List<Activity> view) {
        StringBuilder output = new StringBuilder();
        if (range == Range.WEEK) {
            output.append("WEEK - ").append(start.format(WEEK_FORMAT))
                    .append(" to ").append(end.format(WEEK_FORMAT));
        } else {
            output.append(range == Range.TODAY ? "TODAY - " : "TOMORROW - ")
                    .append(start.format(WEEK_FORMAT));
        }
        if (view.isEmpty()) {
            return output.append("\n\n[WARNING] No activities scheduled for ")
                    .append(range == Range.WEEK ? "the next seven days." : "this day.").toString();
        }
        if (range == Range.WEEK) {
            for (int day = 0; day < 7; day++) {
                appendDay(output, start.plusDays(day), view);
            }
        } else {
            appendDay(output, start, view);
        }
        appendSummary(output, view);
        appendRatingChart(output, view);
        return output.toString();
    }

    /**
     * Adds one day's activities or an explicit empty-day message.
     */
    private static void appendDay(StringBuilder output, LocalDate date, List<Activity> view) {
        List<Activity> daily = view.stream().filter(activity -> activity.getDate().equals(date)).toList();
        output.append("\n\n").append(date.format(DAY_FORMAT));
        if (daily.isEmpty()) {
            output.append(" - No activities scheduled for this day.");
            return;
        }
        int index = view.indexOf(daily.get(0)) + 1;
        for (Activity activity : daily) {
            output.append("\n").append(index++).append(". ").append(activity.getStartTime())
                    .append("-").append(activity.getEndTime()).append(" [")
                    .append(activity.isDone() ? "X" : " ").append("] ").append(activity.getName())
                    .append("    IMP ").append(rating(activity.getImportance()))
                    .append("  URG ").append(rating(activity.getUrgency()))
                    .append("  COM ").append(rating(activity.getCommitment()))
                    .append("  DEM ").append(demandCode(activity.getDemand()));
        }
    }

    /**
     * Adds completion and high-demand counts for the selected activities.
     */
    private static void appendSummary(StringBuilder output, List<Activity> view) {
        long completed = view.stream().filter(Activity::isDone).count();
        long highDemand = view.stream().filter(activity -> activity.getDemand() == DemandLevel.HIGH).count();
        output.append("\n\nSUMMARY\nTotal activities: ").append(view.size())
                .append("\nCompleted: ").append(completed)
                .append("\nRemaining: ").append(view.size() - completed)
                .append("\nHigh-demand activities: ").append(highDemand);
    }

    /**
     * Adds a padded rating table with a header divider, rating totals, and demand distribution.
     */
    private static void appendRatingChart(StringBuilder output, List<Activity> view) {
        int nameWidth = Math.max("Activity".length(),
                view.stream().mapToInt(activity -> activity.getName().length()).max().orElse(0));
        String rowFormat = "| %-" + nameWidth + "s     | %-5s     | %-5s     | %-5s     | %-14s     | %-6s |";
        String header = String.format(Locale.ENGLISH, rowFormat,
                "Activity", "IMP", "URG", "COM", "PRIORITY SCORE", "DEMAND");
        output.append("\n\nRATING CHART\n").append(header)
                .append("\n").append(header.replaceAll("[^|]", "-"));
        int importanceTotal = 0;
        int urgencyTotal = 0;
        int commitmentTotal = 0;
        int low = 0;
        int medium = 0;
        int high = 0;
        for (Activity activity : view) {
            Integer importance = activity.getImportance();
            Integer urgency = activity.getUrgency();
            Integer commitment = activity.getCommitment();
            if (importance != null) {
                importanceTotal += importance;
            }
            if (urgency != null) {
                urgencyTotal += urgency;
            }
            if (commitment != null) {
                commitmentTotal += commitment;
            }
            switch (activity.getDemand()) {
            case LOW -> low++;
            case MEDIUM -> medium++;
            case HIGH -> high++;
            default -> throw new IllegalStateException("Unexpected demand level.");
            }
            Integer priorityScore = activity.getPriorityScore();
            String priority = priorityScore == null ? "N" : priorityScore.toString();
            String demand = "#".repeat(demandCount(activity.getDemand())) + " " + demandCode(activity.getDemand());
            output.append("\n").append(String.format(Locale.ENGLISH, rowFormat, activity.getName(),
                    stars(importance), exclamations(urgency), commitmentMarks(commitment), priority, demand));
        }
        output.append("\n\nRATING TOTALS\nIMP ").append(importanceTotal)
                .append("\nURG ").append(urgencyTotal)
                .append("\nCOM ").append(commitmentTotal)
                .append("\nDEMAND DISTRIBUTION    LOW ").append(low)
                .append("    MEDIUM ").append(medium).append("    HIGH ").append(high)
                .append("\n\nLegend: * Importance; ! Urgency; + Commitment; # Demand")
                .append("\nPriority score = URG x URG x (IMP + COM).")
                .append("\nDemand: L=#, M=##, H=###")
                .append("\nN = priority unavailable; - = rating not set.");
    }

    private static int demandCount(DemandLevel demand) {
        return switch (demand) {
        case LOW -> 1;
        case MEDIUM -> 2;
        case HIGH -> 3;
        };
    }

    private static String demandCode(DemandLevel demand) {
        return switch (demand) {
        case LOW -> "L";
        case MEDIUM -> "M";
        case HIGH -> "H";
        };
    }

    private static String rating(Integer value) {
        return value == null ? "-" : value.toString();
    }

    private static String stars(Integer value) {
        return value == null ? "-" : "*".repeat(value);
    }

    private static String exclamations(Integer value) {
        return value == null ? "-" : "!".repeat(value);
    }

    private static String commitmentMarks(Integer value) {
        return value == null ? "-" : "+".repeat(value);
    }
}
//@@author
