package seedu.unienable.command.activity;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/** Displays scheduled activities and rating summaries for a day or Monday-to-Sunday week. */
public class ViewCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ViewCommand.class.getName());
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("EEE dd MMM", Locale.ENGLISH);
    private static final DateTimeFormatter WEEK_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);

    private final Range range;
    private final Clock clock;

    /** The supported date ranges for a view command. */
    public enum Range {
        TODAY, TOMORROW, WEEK
    }

    /** Creates a date view using the system clock. */
    public ViewCommand(Range range) {
        this(range, Clock.systemDefaultZone());
    }

    /** Creates a date view with an injectable clock for deterministic date selection. */
    public ViewCommand(Range range, Clock clock) {
        this.range = range;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        LocalDate today = LocalDate.now(clock);
        LocalDate start = range == Range.TOMORROW ? today.plusDays(1) : today;
        LocalDate end = range == Range.WEEK
                ? today.with(DayOfWeek.MONDAY).plusDays(6) : start;
        LocalDate weekStart = range == Range.WEEK ? today.with(DayOfWeek.MONDAY) : start;
        LocalDate weekEnd = range == Range.WEEK ? end : start;
        List<Activity> view = activities.getViewBetween(weekStart, weekEnd);
        LOGGER.fine(() -> "Displayed " + view.size() + " activities for " + range + ".");
        return new CommandResult(format(start, end, weekStart, view), false);
    }

    /** Renders the selected range followed by its schedule and aggregate sections. */
    private String format(LocalDate start, LocalDate end, LocalDate weekStart, List<Activity> view) {
        StringBuilder output = new StringBuilder();
        if (range == Range.WEEK) {
            output.append("WEEK - ").append(weekStart.format(WEEK_FORMAT))
                    .append(" to ").append(end.format(WEEK_FORMAT));
            for (int day = 0; day < 7; day++) {
                appendDay(output, weekStart.plusDays(day), view);
            }
        } else {
            output.append(range == Range.TODAY ? "TODAY - " : "TOMORROW - ")
                    .append(start.format(WEEK_FORMAT));
            appendDay(output, start, view);
        }
        appendSummary(output, view);
        appendRatingChart(output, view);
        return output.toString();
    }

    /** Adds one day's activities or an explicit empty-day message. */
    private static void appendDay(StringBuilder output, LocalDate date, List<Activity> view) {
        List<Activity> daily = view.stream().filter(activity -> activity.getDate().equals(date)).toList();
        output.append("\n\n").append(date.format(DAY_FORMAT));
        if (daily.isEmpty()) {
            output.append(" - No scheduled activities");
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

    /** Adds completion and high-demand counts for the selected activities. */
    private static void appendSummary(StringBuilder output, List<Activity> view) {
        long completed = view.stream().filter(Activity::isDone).count();
        long highDemand = view.stream().filter(activity -> activity.getDemand() == DemandLevel.HIGH).count();
        output.append("\n\nSUMMARY\nTotal activities: ").append(view.size())
                .append("    Completed: ").append(completed)
                .append("    Remaining: ").append(view.size() - completed)
                .append("\nHigh-demand activities: ").append(highDemand);
    }

    /** Adds per-activity rating marks, rating totals, and demand distribution. */
    private static void appendRatingChart(StringBuilder output, List<Activity> view) {
        output.append("\n\nRATING CHART\nActivity                       IMP / URG / COM                 PRIORITY SCORE  DEMAND");
        int importanceTotal = 0;
        int urgencyTotal = 0;
        int commitmentTotal = 0;
        int ratingsSet = 0;
        int low = 0;
        int medium = 0;
        int high = 0;
        for (Activity activity : view) {
            Integer importance = activity.getImportance();
            Integer urgency = activity.getUrgency();
            Integer commitment = activity.getCommitment();
            if (importance != null) { importanceTotal += importance; ratingsSet++; }
            if (urgency != null) { urgencyTotal += urgency; ratingsSet++; }
            if (commitment != null) { commitmentTotal += commitment; ratingsSet++; }
            switch (activity.getDemand()) {
            case LOW -> low++;
            case MEDIUM -> medium++;
            case HIGH -> high++;
            default -> throw new IllegalStateException("Unexpected demand level.");
            }
            output.append("\n").append(activity.getName()).append("  IMP ").append(stars(importance))
                    .append("  URG ").append(exclamations(urgency)).append("  COM ").append(dots(commitment))
                    .append("  ").append(activity.getPriorityScore() == null ? "N" : activity.getPriorityScore())
                    .append("  ").append("#".repeat(demandCount(activity.getDemand())))
                    .append(" ").append(demandCode(activity.getDemand()));
        }
        output.append("\n\nRATING TOTALS\nIMP ").append(importanceTotal)
                .append("    URG ").append(urgencyTotal).append("    COM ").append(commitmentTotal)
                .append("\nRatings set: ").append(ratingsSet).append("/").append(view.size() * 3)
                .append("; rating totals use recorded ratings only.")
                .append("\nDEMAND DISTRIBUTION    LOW ").append(low)
                .append("    MEDIUM ").append(medium).append("    HIGH ").append(high)
                .append("\n\nLegend: * Importance; ! Urgency; ").append("• Commitment; # Demand")
                .append("\nDemand: L=#, M=##, H=###")
                .append("\nN = priority unavailable; - = rating not set.");
    }

    private static int demandCount(DemandLevel demand) {
        return switch (demand) { case LOW -> 1; case MEDIUM -> 2; case HIGH -> 3; };
    }

    private static String demandCode(DemandLevel demand) {
        return switch (demand) { case LOW -> "L"; case MEDIUM -> "M"; case HIGH -> "H"; };
    }

    private static String rating(Integer value) { return value == null ? "-" : value.toString(); }
    private static String stars(Integer value) { return value == null ? "-" : "*".repeat(value); }
    private static String exclamations(Integer value) { return value == null ? "-" : "!".repeat(value); }
    private static String dots(Integer value) { return value == null ? "-" : "•".repeat(value); }
}
