//@@author Atharva005
package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;

/**
 * Verifies empty calendar warnings, date filtering, and aligned console rating columns.
 */
class ViewCommandTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 25);
    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(10, 0);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-10-25T04:00:00Z"), ZoneId.of("Asia/Singapore"));

    private final Parser parser = new Parser(FIXED_CLOCK);
    private final Storage noWrites = new Storage() {
        @Override
        public void save(ActivityList ignored) {
            throw new AssertionError("View commands must not persist activities");
        }
    };

    @Test
    void execute_emptyList_warnsWithoutShowingEmptyCharts() throws Exception {
        assertEmptyViews(new ActivityList());
    }

    @Test
    void execute_activitiesOutsideRange_warnsWithoutChangingActivities() throws Exception {
        ActivityList activities = new ActivityList();
        activities.addActivity(new Activity("Previous week", TODAY.minusDays(7), START, END,
                DemandLevel.LOW, false));
        activities.addActivity(new Activity("Next week", TODAY.plusDays(7), START, END,
                DemandLevel.HIGH, true));

        assertEmptyViews(activities);
    }

    @Test
    void execute_populatedRanges_preservesSchedulesAndLabelsMissingDays() throws Exception {
        ActivityList activities = new ActivityList();
        activities.addActivity(new Activity("Today revision", TODAY, START, END,
                DemandLevel.HIGH, true, 5, 4, 3));
        activities.addActivity(new Activity("Tomorrow meeting", TODAY.plusDays(1), START, END,
                DemandLevel.LOW, false));
        List<Activity> original = activities.getActivities();

        CommandResult today = parser.parse("view today").execute(activities, noWrites);
        assertTrue(today.getMessage().contains("Today revision"));
        assertFalse(today.getMessage().contains("Tomorrow meeting"));
        assertFalse(today.getMessage().contains("[WARNING]"));
        assertTrue(today.getMessage().contains("SUMMARY\nTotal activities: 1\nCompleted: 1\nRemaining: 0\n"
                + "High-demand activities: 1"));
        assertTrue(today.getMessage().contains("RATING TOTALS\nIMP 5\nURG 4\nCOM 3\n"));
        assertTrue(today.getMessage().contains("RATING CHART"));

        CommandResult tomorrow = parser.parse("view tomorrow").execute(activities, noWrites);
        assertTrue(tomorrow.getMessage().contains("Tomorrow meeting"));
        assertFalse(tomorrow.getMessage().contains("Today revision"));
        assertFalse(tomorrow.getMessage().contains("[WARNING]"));
        assertTrue(tomorrow.getMessage().contains("SUMMARY\nTotal activities: 1\nCompleted: 0\nRemaining: 1\n"
                + "High-demand activities: 0"));
        assertTrue(tomorrow.getMessage().contains("RATING TOTALS\nIMP 0\nURG 0\nCOM 0\n"));

        CommandResult week = parser.parse("view week").execute(activities, noWrites);
        String weeklyMessage = week.getMessage();
        assertTrue(weeklyMessage.startsWith("WEEK - Sunday, 25 October 2026 to Saturday, 31 October 2026"));
        assertTrue(weeklyMessage.contains("Tue 27 Oct - No activities scheduled for this day."));
        assertEquals(5, weeklyMessage.split("No activities scheduled for this day\\.", -1).length - 1);
        assertFalse(weeklyMessage.contains("[WARNING]"));
        assertTrue(weeklyMessage.contains("Today revision"));
        assertTrue(weeklyMessage.contains("Tomorrow meeting"));
        assertTrue(weeklyMessage.contains("SUMMARY\nTotal activities: 2\nCompleted: 1\nRemaining: 1\n"
                + "High-demand activities: 1"));
        assertTrue(weeklyMessage.contains("RATING TOTALS\nIMP 5\nURG 4\nCOM 3\n"));
        assertTrue(weeklyMessage.contains("RATING CHART"));

        for (CommandResult result : List.of(today, tomorrow, week)) {
            assertFalse(result.getMessage().contains("Ratings set:"));
            assertFalse(result.getMessage().contains("rating totals use recorded ratings only"));
            assertFalse(result.isExit());
            assertFalse(result.didChangeActivities());
        }
        assertEquals(original, activities.getActivities());
        assertTrue(original.get(0).isDone());
        assertFalse(original.get(1).isDone());
    }

    @Test
    void execute_weekRange_includesSevenDaysAcrossYearBoundary() throws Exception {
        Clock yearEndClock = Clock.fixed(Instant.parse("2026-12-31T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        Parser yearEndParser = new Parser(yearEndClock);
        LocalDate start = LocalDate.of(2026, 12, 31);
        ActivityList activities = new ActivityList();
        activities.addActivity(new Activity("Yesterday only", start.minusDays(1), START, END,
                DemandLevel.LOW, false));
        activities.addActivity(new Activity("Today only", start, START, END,
                DemandLevel.MEDIUM, false));
        activities.addActivity(new Activity("Final included day", start.plusDays(6), START, END,
                DemandLevel.HIGH, false));
        activities.addActivity(new Activity("Day eight", start.plusDays(7), START, END,
                DemandLevel.LOW, false));

        CommandResult result = yearEndParser.parse("view week").execute(activities, noWrites);
        String message = result.getMessage();

        assertTrue(message.startsWith("WEEK - Thursday, 31 December 2026 to Wednesday, 06 January 2027"));
        for (String day : List.of("Thu 31 Dec", "Fri 01 Jan", "Sat 02 Jan", "Sun 03 Jan", "Mon 04 Jan",
                "Tue 05 Jan", "Wed 06 Jan")) {
            assertTrue(message.contains("\n\n" + day), day);
        }
        assertTrue(message.contains("Today only"));
        assertTrue(message.contains("Final included day"));
        assertFalse(message.contains("Yesterday only"));
        assertFalse(message.contains("Day eight"));
        assertTrue(message.contains("Total activities: 2\nCompleted: 0\nRemaining: 2"));
        assertFalse(result.isExit());
        assertFalse(result.didChangeActivities());
    }

    @Test
    void execute_ratingChart_alignsTableColumnsAndAsciiCommitmentMarks() throws Exception {
        ActivityList activities = new ActivityList();
        activities.addActivity(new Activity("Practice quiz", TODAY, START, END,
                DemandLevel.HIGH, false, 4, 5, 3));
        activities.addActivity(new Activity("Stretch break", TODAY, START, END,
                DemandLevel.LOW, false, 1, 1, 1));
        activities.addActivity(new Activity("Plan tomorrow", TODAY, START, END,
                DemandLevel.MEDIUM, false, 3, null, 2));
        activities.addActivity(new Activity("Project notes", TODAY, START, END,
                DemandLevel.MEDIUM, false, null, 2, null));
        activities.addActivity(new Activity("An activity name longer than the old heading", TODAY, START, END,
                DemandLevel.HIGH, false, 5, 5, 5));

        String message = parser.parse("view today").execute(activities, noWrites).getMessage();
        String chart = message.substring(message.indexOf("RATING CHART\n") + "RATING CHART\n".length(),
                message.indexOf("\n\nRATING TOTALS"));
        String[] lines = chart.split("\n");
        for (String line : lines) {
            assertTrue(line.startsWith("| ") && line.endsWith(" |"), line);
            assertEquals(7L, line.chars().filter(character -> character == '|').count(), line);
            assertEquals(lines[0].length(), line.length(), line);
        }
        String[] headings = {"Activity", "IMP", "URG", "COM", "PRIORITY SCORE", "DEMAND"};
        int[] starts = new int[headings.length];
        for (int column = 0; column < headings.length; column++) {
            starts[column] = lines[0].indexOf(headings[column]);
            assertTrue(starts[column] >= 0, headings[column]);
            if (column > 0) {
                assertTrue(starts[column] - starts[column - 1] - headings[column - 1].length() >= 5);
            }
        }
        String[][] expectedRows = {
            {"Practice quiz", "****", "!!!!!", "+++", "175", "### H"},
            {"Stretch break", "*", "!", "+", "2", "# L"},
            {"Plan tomorrow", "***", "-", "++", "N", "## M"},
            {"Project notes", "-", "!!", "-", "N", "## M"},
            {"An activity name longer than the old heading", "*****", "!!!!!", "+++++", "250", "### H"}
        };
        assertEquals(expectedRows.length + 1, lines.length);
        for (int row = 0; row < expectedRows.length; row++) {
            for (int column = 0; column < headings.length; column++) {
                String expected = expectedRows[row][column];
                assertEquals(expected, lines[row + 1].substring(starts[column], starts[column] + expected.length()));
                if (column + 1 < headings.length) {
                    String gap = lines[row + 1].substring(starts[column] + expected.length(), starts[column + 1]);
                    assertTrue(gap.endsWith("     | ") && gap.replace("|", "").isBlank(), lines[row + 1]);
                }
            }
        }
        assertTrue(chart.chars().allMatch(character -> character < 128));
        assertTrue(message.contains("Legend: * Importance; ! Urgency; + Commitment; # Demand\n"
                + "Priority score = URG x URG x (IMP + COM)."));
    }

    /**
     * Checks all empty date ranges against their complete messages and preserves the input list.
     */
    private void assertEmptyViews(ActivityList activities) throws Exception {
        String[][] cases = {
            {"view today", "TODAY - Sunday, 25 October 2026\n\n"
                    + "[WARNING] No activities scheduled for this day."},
            {"view tomorrow", "TOMORROW - Monday, 26 October 2026\n\n"
                    + "[WARNING] No activities scheduled for this day."},
            {"view week", "WEEK - Sunday, 25 October 2026 to Saturday, 31 October 2026\n\n"
                    + "[WARNING] No activities scheduled for the next seven days."}
        };
        List<Activity> original = activities.getActivities();
        for (String[] scenario : cases) {
            CommandResult result = parser.parse(scenario[0]).execute(activities, noWrites);
            assertEquals(scenario[1], result.getMessage(), scenario[0]);
            assertFalse(result.isExit());
            assertFalse(result.didChangeActivities());
            assertEquals(original, activities.getActivities());
        }
    }
}
//@@author
