package seedu.unienable.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/** Verifies help documents the commands actually routed in this sample. */
class HelpCommandTest {
    @Test
    void execute_showsAvailableCommandsAndPlannedFeatures() {
        CommandResult result = new HelpCommand().execute(new ActivityList(), new Storage());
        assertTrue(result.getMessage().contains("add n/NAME"));
        assertTrue(result.getMessage().contains("facility find"));
        assertTrue(result.getMessage().contains("await Branches 2 and 3"));
        assertFalse(result.isExit());
    }
}
