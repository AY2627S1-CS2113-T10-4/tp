package seedu.unienable.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/** Verifies help describes the available v1.0 commands and automatic saving. */
class HelpCommandTest {
    @Test
    void execute_showsAvailableCommandsAndAutomaticSaving() {
        CommandResult result = new HelpCommand().execute(new ActivityList(), new Storage());
        assertTrue(result.getMessage().contains("add n/NAME"));
        assertTrue(result.getMessage().contains("facility find"));
        assertTrue(result.getMessage().contains("delete INDEX"));
        assertTrue(result.getMessage().contains("incomplete\n" + "delete INDEX"));
        assertTrue(result.getMessage().contains("route from/START to/END"));
        assertTrue(result.getMessage().contains("Activity changes are saved automatically"));
        assertFalse(result.getMessage().contains("await Branches"));
        assertFalse(result.isExit());
        assertFalse(result.didChangeActivities());
    }
}
