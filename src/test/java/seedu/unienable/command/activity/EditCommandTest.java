package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;

/**
 * Checks partial edits, completion preservation, rescheduling and atomic validation.
 */
class EditCommandTest {
    private final Parser parser = new Parser();
    private final ActivityList activities = new ActivityList();
    private final Storage noWrites = new Storage() {
        @Override
        public void save(ActivityList ignored) {
            throw new AssertionError("EditCommand must not persist directly");
        }
    };

    @Test
    void execute_changesOnlySuppliedFieldsAndPreservesCompletion() throws Exception {
        add("Old name", "2099-01-02", "09:00", "10:00", "LOW");
        parser.parse("mark 1").execute(activities, noWrites);

        var result = parser.parse("edit 1 n/New name dem/HIGH").execute(activities, noWrites);

        assertTrue(result.didChangeActivities());
        String message = parser.parse("list").execute(activities, noWrites).getMessage();
        assertTrue(message.contains("1. New name (2099-01-02, 09:00-10:00) [HIGH] [X]"));
    }

    @Test
    void execute_reschedulesFutureActivity() throws Exception {
        add("Meeting", "2099-01-02", "09:00", "10:00", "MEDIUM");

        parser.parse("edit 1 d/2099-01-03 s/14:00 e/15:00").execute(activities, noWrites);

        assertTrue(parser.parse("list").execute(activities, noWrites).getMessage()
                .contains("1. Meeting (2099-01-03, 14:00-15:00) [MEDIUM]"));
    }

    @Test
    void execute_rejectsInvalidResultWithoutChangingActivity() throws Exception {
        add("Meeting", "2099-01-02", "09:00", "10:00", "MEDIUM");

        assertThrows(UniEnableException.class,
                () -> parser.parse("edit 1 s/14:00 e/13:00").execute(activities, noWrites));

        String message = parser.parse("list").execute(activities, noWrites).getMessage();
        assertTrue(message.contains("1. Meeting (2099-01-02, 09:00-10:00) [MEDIUM] [ ]"));
        assertFalse(message.contains("14:00-13:00"));
    }

    private void add(String name, String date, String start, String end, String demand)
            throws Exception {
        parser.parse("add n/" + name + " d/" + date + " s/" + start + " e/" + end
                + " dem/" + demand).execute(activities, noWrites);
    }
}
