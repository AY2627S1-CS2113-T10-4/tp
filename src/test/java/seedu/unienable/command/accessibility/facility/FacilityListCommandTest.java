package seedu.unienable.command.accessibility.facility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.CommandResult;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Tests the text returned by the read-only facility list command.
 */
public class FacilityListCommandTest {

    /**
     * Tests that facilities appear in their original order with an ID, name, and disclaimer.
     */
    @Test
    public void execute_multipleFacilities_listsIdsAndNamesInOrder() {
        Facility first = new Facility("F01", "AS1", "Arts Block 1", List.of());
        Facility second = new Facility("F09", "CLB", "Central Library", List.of());
        FacilityManager manager = new FacilityManager(List.of(first, second));
        FacilityListCommand command = new FacilityListCommand(manager);

        CommandResult result = command.execute(new ActivityList(), new Storage());

        String expected = "Known facilities in the local reference:\n"
                + "[F01] AS1\n"
                + "[F09] CLB\n\n"
                + AccessibilityDisclaimer.TEXT;
        assertEquals(expected, result.getMessage());
    }

    /**
     * Tests that an empty dataset still produces a heading and the required disclaimer.
     */
    @Test
    public void execute_noFacilities_returnsHeadingAndDisclaimer() {
        FacilityManager manager = new FacilityManager(List.of());
        FacilityListCommand command = new FacilityListCommand(manager);

        CommandResult result = command.execute(new ActivityList(), new Storage());

        String expected = "Known facilities in the local reference:\n\n"
                + AccessibilityDisclaimer.TEXT;
        assertEquals(expected, result.getMessage());
    }

    /**
     * Tests that listing facilities does not request termination of the application.
     */
    @Test
    public void execute_validFacilities_doesNotExit() {
        FacilityManager manager = new FacilityManager(List.of());
        FacilityListCommand command = new FacilityListCommand(manager);

        CommandResult result = command.execute(new ActivityList(), new Storage());

        assertFalse(result.isExit());
    }
}
