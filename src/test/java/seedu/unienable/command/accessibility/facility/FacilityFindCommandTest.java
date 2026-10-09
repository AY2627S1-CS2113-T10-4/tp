package seedu.unienable.command.accessibility.facility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Checks read-only execution and empty metadata for facility find commands.
 */
class FacilityFindCommandTest {
    @Test
    void execute_isReadOnlyAndHandlesEmptyMetadata() throws Exception {
        Facility empty = new Facility("F01", "AS1", null, List.of());
        var manager = new FacilityManager(List.of(empty));
        ActivityList activities = new ActivityList();
        Storage noWrites = new Storage() {
            @Override
            public void save(ActivityList ignored) {
                throw new AssertionError("Facility commands must not persist activities");
            }
        };
        var command = new Parser(manager).parse("facility find type/LIFT");
        var result = command.execute(activities, noWrites);
        assertFalse(result.isExit());
        assertTrue(result.getMessage().endsWith(AccessibilityDisclaimer.TEXT));
        assertTrue(activities.getActivities().isEmpty());
    }
}
