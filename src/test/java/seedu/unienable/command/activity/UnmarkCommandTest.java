package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/**
 * Tests unmarking activities and reporting repeated no-op operations.
 */
class UnmarkCommandTest {
    @Test
    void execute_marksTheSelectedActivityIncomplete() throws Exception {
        Activity activity = new Activity("Lecture", LocalDate.of(2026, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(10, 0), DemandLevel.MEDIUM, true);
        ActivityList activities = new ActivityList(List.of(activity));

        var result = new UnmarkCommand(1).execute(activities, new Storage());

        assertFalse(activity.isDone());
        assertTrue(result.didChangeActivities());
        assertTrue(result.getMessage().contains("Unmarked activity"));
    }

    @Test
    void execute_doesNotReportAChangeWhenAlreadyUnmarked() throws Exception {
        Activity activity = new Activity("Lecture", LocalDate.of(2026, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(10, 0), DemandLevel.MEDIUM, false);
        ActivityList activities = new ActivityList(List.of(activity));

        var result = new UnmarkCommand(1).execute(activities, new Storage());

        assertFalse(result.didChangeActivities());
        assertTrue(result.getMessage().contains("already unmarked"));
    }
}
