package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;

/**
 * Checks demand-order display and read-only behaviour for the list demand command.
 */
class ListDemandCommandTest {
    private final Parser parser = new Parser();
    private final ActivityList activities = new ActivityList();
    private final Storage noWrites = new Storage() {
        @Override
        public void save(ActivityList ignored) {
            throw new AssertionError("ListDemandCommand must not persist activities");
        }
    };

    @Test
    void execute_onEmptyList_showsDemandHeadingAndPlaceholder() throws Exception {
        var result = parser.parse("list demand").execute(activities, noWrites);
        assertFalse(result.isExit());
        assertTrue(result.getMessage().startsWith("Activities by demand (HIGH to LOW):"));
        assertTrue(result.getMessage().endsWith("[WARNING] No activities yet."));
    }

    @Test
    void execute_ordersByDemandHighToLow() throws Exception {
        add("Lab", "2026-10-13", "14:00", "16:00", "MEDIUM");
        add("Exam", "2026-10-15", "09:00", "11:00", "HIGH");
        add("Run", "2026-10-12", "07:00", "08:00", "LOW");
        String message = parser.parse("list demand").execute(activities, noWrites).getMessage();
        assertTrue(message.contains("1. Exam (2026-10-15, 09:00-11:00) [HIGH]"));
        assertTrue(message.contains("2. Lab (2026-10-13, 14:00-16:00) [MEDIUM]"));
        assertTrue(message.contains("3. Run (2026-10-12, 07:00-08:00) [LOW]"));
    }

    @Test
    void execute_keepsCanonicalOrderWithinSameDemand() throws Exception {
        add("Late", "2026-10-14", "09:00", "10:00", "HIGH");
        add("Run", "2026-10-12", "07:00", "08:00", "LOW");
        add("Early", "2026-10-12", "09:00", "10:00", "HIGH");
        String message = parser.parse("list demand").execute(activities, noWrites).getMessage();
        assertTrue(message.indexOf("1. Early") < message.indexOf("2. Late"),
                "within a demand group, canonical order (by date) applies, not add order");
        assertTrue(message.indexOf("2. Late") < message.indexOf("3. Run"));
    }

    /** Seeds existing activities without applying the add parser's current-time restriction. */
    private void add(String name, String date, String start, String end, String demand) {
        activities.addActivity(new Activity(name, LocalDate.parse(date), LocalTime.parse(start),
                LocalTime.parse(end), DemandLevel.valueOf(demand), false));
    }
}
