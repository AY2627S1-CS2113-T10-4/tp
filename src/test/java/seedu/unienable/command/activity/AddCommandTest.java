package seedu.unienable.command.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.CommandResult;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/** Tests that AddCommand changes state before reporting success. */
class AddCommandTest {
    @Test
    void execute_twoAdds_updatesActivityListAndDisplaysCount() {
        ActivityList activities = new ActivityList();
        AddCommand command = new AddCommand("Team Meeting", LocalDate.of(2026, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(10, 0), DemandLevel.MEDIUM);

        CommandResult firstResult = command.execute(activities, new Storage());
        CommandResult secondResult = command.execute(activities, new Storage());

        assertEquals(2, activities.getActivities().size());
        assertEquals("Team Meeting", activities.getActivities().get(0).getName());
        assertFalse(activities.getActivities().get(0).isDone());
        assertTrue(firstResult.getMessage().contains("Activities in this session: 1"));
        assertTrue(secondResult.getMessage().contains("Activities in this session: 2"));
        assertFalse(firstResult.isExit());
    }
}
