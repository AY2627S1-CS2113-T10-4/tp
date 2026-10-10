package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;

/**
 * Checks displayed-index deletion behaviour: index mapping, boundary indexes,
 * renumbering, and model-reported range errors.
 */
class DeleteCommandTest {
    private final Parser parser = new Parser();
    private final ActivityList activities = new ActivityList();

    // Baseline Storage.save() always throws UniEnableException, so any accidental
    // direct persistence by DeleteCommand fails these tests loudly; persistence
    // itself is ConsoleHelper's job once Branch 3 wires it.
    private final Storage storage = new Storage();

    @Test
    void execute_deletesActivityAtDisplayedIndexNotInsertionOrder() throws Exception {
        add("Lab", "2026-10-13", "14:00", "16:00", "MEDIUM");
        add("Gym", "2026-10-12", "07:00", "09:00", "HIGH");
        var result = parser.parse("delete 1").execute(activities, storage);
        assertTrue(result.getMessage().startsWith("Deleted activity: Gym"),
                "displayed index 1 must resolve to the canonical order, not the first add");
        assertTrue(result.getMessage().contains("Activities in this session: 1"));
        String listMessage = parser.parse("list").execute(activities, storage).getMessage();
        assertFalse(listMessage.contains("Gym"), "deleted activity must be gone from the list");
        assertTrue(listMessage.contains("1. Lab (2026-10-13, 14:00-16:00) [MEDIUM]"),
                "remaining activities must renumber starting from 1");
    }

    @Test
    void execute_renumbersRemainingActivities() throws Exception {
        add("Alpha", "2026-10-12", "08:00", "09:00", "LOW");
        add("Beta", "2026-10-13", "08:00", "09:00", "LOW");
        add("Gamma", "2026-10-14", "08:00", "09:00", "LOW");
        parser.parse("delete 2").execute(activities, storage);
        String message = parser.parse("list").execute(activities, storage).getMessage();
        assertTrue(message.contains("1. Alpha"));
        assertTrue(message.contains("2. Gamma"), "activities after the deleted one shift down");
        assertFalse(message.contains("Beta"));
    }

    @Test
    void execute_deletesLastIndexThenOnlyRemainingIndex() throws Exception {
        add("First", "2026-10-12", "08:00", "09:00", "LOW");
        add("Second", "2026-10-13", "08:00", "09:00", "LOW");
        parser.parse("delete 2").execute(activities, storage);
        assertEquals(1, activities.getActivities().size(), "last displayed index deletes the last activity");
        parser.parse("delete 1").execute(activities, storage);
        assertTrue(activities.getActivities().isEmpty(), "the only remaining activity is deletable");
    }

    @Test
    void execute_rejectsOutOfRangeIndexWithValidRange() throws Exception {
        add("Solo", "2026-10-12", "08:00", "09:00", "LOW");
        var exception = assertThrows(UniEnableException.class,
                () -> parser.parse("delete 5").execute(activities, storage));
        assertTrue(exception.getMessage().startsWith("[WARNING]"));
        assertTrue(exception.getMessage().contains("No activity at index 5"));
        assertTrue(exception.getMessage().contains("Valid range: 1 to 1"));
    }

    @Test
    void execute_rejectsIndexOnEmptyList() {
        var exception = assertThrows(UniEnableException.class,
                () -> parser.parse("delete 1").execute(activities, storage));
        assertTrue(exception.getMessage().contains("There are no activities to delete."));
    }

    private void add(String name, String date, String start, String end, String demand)
            throws Exception {
        parser.parse("add n/" + name + " d/" + date + " s/" + start + " e/" + end
                + " dem/" + demand).execute(activities, storage);
    }
}
