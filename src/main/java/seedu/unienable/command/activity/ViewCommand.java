//@@author Atharva005
package seedu.unienable.command.activity;

import java.text.BreakIterator;
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

    /**
     * Caps displayed name width so the six-column rating table fits within 80 terminal columns.
     */
    private static final int MAX_CHART_NAME_WIDTH = 26;

    private final Range range;
    private final Clock clock;

    /**
     * The supported date ranges for a view command.
     */
    public enum Range {
        /** The current local calendar day. */
        TODAY,
        /** The next local calendar day. */
        TOMORROW,
        /** Today and the following six days. */
        WEEK
    }

    /**
     * Creates a date view using the system clock.
     *
     * @param range date range to display.
     */
    public ViewCommand(Range range) {
        this(range, Clock.systemDefaultZone());
    }

    /**
     * Creates a date view with an injectable clock for deterministic date selection.
     *
     * @param range date range to display.
     * @param clock time source used to select the current local date.
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
        int nameWidth = daily.stream().mapToInt(activity -> displayWidth(activity.getName())).max().orElse(0);
        int indexWidth = Integer.toString(index + daily.size() - 1).length();
        for (Activity activity : daily) {
            output.append("\n").append(String.format(Locale.ROOT, "%" + indexWidth + "d", index++))
                    .append(". ").append(activity.getStartTime())
                    .append("-").append(activity.getEndTime()).append(" [")
                    .append(activity.isDone() ? "X" : " ").append("] ")
                    .append(padName(activity.getName(), nameWidth))
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
        int nameWidth = Math.min(MAX_CHART_NAME_WIDTH, Math.max("Activity".length(),
                view.stream().mapToInt(activity -> displayWidth(activity.getName())).max().orElse(0)));
        String rowFormat = "| %s | %-5s | %-5s | %-5s | %-14s | %-6s |";
        String header = String.format(Locale.ENGLISH, rowFormat,
                padName("Activity", nameWidth), "IMP", "URG", "COM", "PRIORITY SCORE", "DEMAND");
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
            output.append("\n").append(String.format(Locale.ENGLISH, rowFormat,
                    padName(chartName(activity.getName()), nameWidth),
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

    /**
     * Shortens only the rating table label; the stored and scheduled name stays complete.
     */
    private static String chartName(String name) {
        if (displayWidth(name) <= MAX_CHART_NAME_WIDTH) {
            return name;
        }
        BreakIterator characters = BreakIterator.getCharacterInstance(Locale.ROOT);
        characters.setText(name);
        int width = 0;
        int start = characters.first();
        int boundary = start;
        for (int end = characters.next(); end != BreakIterator.DONE; start = end, end = characters.next()) {
            int nextWidth = clusterWidth(name.substring(start, end));
            if (width + nextWidth > MAX_CHART_NAME_WIDTH - 3) {
                break;
            }
            width += nextWidth;
            boundary = end;
        }
        return name.substring(0, boundary) + "...";
    }

    /** Pads using terminal width rather than UTF-16 length so Unicode names align with ASCII headings. */
    private static String padName(String name, int width) {
        return name + " ".repeat(width - displayWidth(name));
    }

    /** Counts rendered columns by grapheme cluster, keeping combining marks and emoji sequences together. */
    private static int displayWidth(String text) {
        BreakIterator characters = BreakIterator.getCharacterInstance(Locale.ROOT);
        characters.setText(text);
        int width = 0;
        int start = characters.first();
        for (int end = characters.next(); end != BreakIterator.DONE; start = end, end = characters.next()) {
            width += clusterWidth(text.substring(start, end));
        }
        return width;
    }

    /** Treats East Asian wide characters and emoji as two columns, and combining marks as zero. */
    private static int clusterWidth(String cluster) {
        int width = 0;
        for (int point : cluster.codePoints().toArray()) {
            int type = Character.getType(point);
            if (point == 0xfe0f || point == 0x20e3 || point >= 0x1f1e6 && point <= 0x1faff
                    || point >= 0x1100 && (point <= 0x115f || point == 0x2329 || point == 0x232a
                    || point >= 0x2e80 && point <= 0xa4cf && point != 0x303f
                    || point >= 0xac00 && point <= 0xd7a3 || point >= 0xf900 && point <= 0xfaff
                    || point >= 0xfe10 && point <= 0xfe19 || point >= 0xfe30 && point <= 0xfe6f
                    || point >= 0xff00 && point <= 0xff60 || point >= 0xffe0 && point <= 0xffe6
                    || point >= 0x20000 && point <= 0x3fffd)) {
                width = 2;
            } else if (type != Character.NON_SPACING_MARK && type != Character.COMBINING_SPACING_MARK
                    && type != Character.ENCLOSING_MARK && type != Character.FORMAT) {
                width = Math.max(width, 1);
            }
        }
        return width;
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
