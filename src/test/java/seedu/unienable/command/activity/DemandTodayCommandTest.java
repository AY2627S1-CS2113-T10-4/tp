package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;

//@@author lukelouyu
/**
 * Verifies the full-day HIGH-demand count, read-only behaviour, command grammar,
 * and date changes in the clock's local zone.
 */
class DemandTodayCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-23T04:00:00Z"), SINGAPORE);

    private final Parser parser = new Parser(FIXED_CLOCK);
    private final ActivityList activities = new ActivityList();
    private final Storage noWrites = new Storage() {
        @Override
        public void save(ActivityList ignored) {
            throw new AssertionError("DemandTodayCommand must not persist activities");
        }
    };

    @Test
    void execute_countsOnlyTodayHighDemandIncludingCompletedAndAllTimes() throws Exception {
        add("Yesterday", "2026-10-22", "23:00", DemandLevel.HIGH, false);
        add("Tomorrow", "2026-10-24", "00:00", DemandLevel.HIGH, false);
        add("Done earlier", "2026-10-23", "00:00", DemandLevel.HIGH, true);
        add("Starting now", "2026-10-23", "12:00", DemandLevel.HIGH, false);
        add("Later today", "2026-10-23", "23:00", DemandLevel.HIGH, false);
        add("Low", "2026-10-23", "09:00", DemandLevel.LOW, false);
        add("Medium", "2026-10-23", "10:00", DemandLevel.MEDIUM, true);
        List<Activity> originalOrder = activities.getActivities();
        List<Boolean> originalCompletion = originalOrder.stream().map(Activity::isDone).toList();

        CommandResult result = parser.parse("demand today").execute(activities, noWrites);

        assertEquals("High-demand activities scheduled today (2026-10-23): 3", result.getMessage());
        assertFalse(result.isExit());
        assertFalse(result.didChangeActivities());
        assertEquals(originalOrder, activities.getActivities());
        assertEquals(originalCompletion, activities.getActivities().stream().map(Activity::isDone).toList());
    }

    @Test
    void execute_emptyListAndNoMatchingActivitiesReportZero() throws Exception {
        String expected = "[WARNING] High-demand activities scheduled today (2026-10-23): 0";
        assertEquals(expected, parser.parse("demand today").execute(activities, noWrites).getMessage());

        add("Tomorrow", "2026-10-24", "09:00", DemandLevel.HIGH, false);
        add("Low", "2026-10-23", "09:00", DemandLevel.LOW, false);
        add("Medium", "2026-10-23", "10:00", DemandLevel.MEDIUM, false);

        assertEquals(expected, parser.parse("demand today").execute(activities, noWrites).getMessage());
    }

    @Test
    void parse_acceptsMixedCaseAndWhitespace() throws Exception {
        add("Lecture", "2026-10-23", "09:00", DemandLevel.HIGH, false);
        for (String input : List.of("demand today", "DEMAND TODAY", "  DeMaNd   ToDaY  ", "demand\ttoday")) {
            assertEquals("High-demand activities scheduled today (2026-10-23): 1",
                    parser.parse(input).execute(activities, noWrites).getMessage(), input);
        }
    }

    @Test
    void parse_rejectsMissingOrUnsupportedSubcommandsAndExtraArguments() {
        for (String input : List.of("demand", "demand tomorrow", "demand week", "demand today extra",
                "demand today dem/HIGH", "demand today today")) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input), input);
            assertTrue(exception.getMessage().startsWith("[WARNING]"), input);
            assertTrue(exception.getMessage().endsWith("Usage: demand today"), input);
        }
    }

    @Test
    void execute_usesLocalDateAtExecutionAndRefreshesAcrossNewYearMidnight() throws Exception {
        AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-12-31T15:59:59Z"));
        Parser midnightParser = new Parser(mutableClock(now, SINGAPORE));
        Command command = midnightParser.parse("demand today");
        add("Last year", "2026-12-31", "09:00", DemandLevel.HIGH, false);
        add("New year", "2027-01-01", "00:00", DemandLevel.HIGH, false);
        add("New year done", "2027-01-01", "12:00", DemandLevel.HIGH, true);

        // Parsing before midnight must not freeze the date used when execution happens later.
        now.set(Instant.parse("2026-12-31T16:00:00Z"));
        assertEquals("High-demand activities scheduled today (2027-01-01): 2",
                command.execute(activities, noWrites).getMessage());

        now.set(Instant.parse("2027-01-01T16:00:00Z"));
        assertEquals("[WARNING] High-demand activities scheduled today (2027-01-02): 0",
                command.execute(activities, noWrites).getMessage());
    }

    private void add(String name, String date, String start, DemandLevel demand, boolean done) {
        activities.addActivity(new Activity(name, LocalDate.parse(date), LocalTime.parse(start),
                LocalTime.parse(start).plusMinutes(30), demand, done));
    }

    /**
     * Creates a clock whose instant can advance while all zone views share the same time source.
     */
    private static Clock mutableClock(AtomicReference<Instant> now, ZoneId zone) {
        return new Clock() {
            @Override
            public ZoneId getZone() {
                return zone;
            }

            @Override
            public Clock withZone(ZoneId newZone) {
                return mutableClock(now, newZone);
            }

            @Override
            public Instant instant() {
                return now.get();
            }
        };
    }
}
//@@author
