package seedu.unienable.command.accessibility.facility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Tests the text produced by the read-only facility view command.
 */
public class FacilityViewCommandTest {

    /**
     * Tests that viewing a facility displays its ID, description, features, and notes.
     */
    @Test
    public void execute_knownFacility_displaysAllFeaturesAndNotes() throws UniEnableException {
        FacilityFeature entrance = new FacilityFeature(
                FacilityFeature.Type.STEP_FREE_ENTRANCE, AccessibilityStatus.YES,
                "Ground floor entrance");
        FacilityFeature lift = new FacilityFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.NO,
                "Building has no lift");
        Facility facility = new Facility("F01", "AS1", "Arts Block 1", List.of(entrance, lift));
        FacilityManager manager = new FacilityManager(List.of(facility));
        FacilityViewCommand command = new FacilityViewCommand(manager, "AS1");

        CommandResult result = command.execute(new ActivityList(), new Storage());

        String expected = "[F01] AS1 - Arts Block 1\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | NO | Building has no lift\n\n"
                + AccessibilityDisclaimer.TEXT;
        assertEquals(expected, result.getMessage());
        assertFalse(result.isExit());
    }

    /**
     * Tests that facility IDs and names can be searched without matching letter case.
     */
    @Test
    public void execute_mixedCaseIdOrName_displaysSameFacility() throws UniEnableException {
        Facility facility = new Facility("F04", "AS4", "Arts Block 4", List.of());
        FacilityManager manager = new FacilityManager(List.of(facility));
        FacilityViewCommand byId = new FacilityViewCommand(manager, "f04");
        FacilityViewCommand byName = new FacilityViewCommand(manager, "as4");

        String idResult = byId.execute(new ActivityList(), new Storage()).getMessage();
        String nameResult = byName.execute(new ActivityList(), new Storage()).getMessage();

        assertEquals(idResult, nameResult);
        assertTrue(idResult.startsWith("[F04] AS4 - Arts Block 4"));
    }

    /**
     * Tests that missing descriptions and feature notes do not print null or extra separators.
     */
    @Test
    public void execute_missingOptionalText_displaysOnlyRecordedValues() throws UniEnableException {
        FacilityFeature feature = new FacilityFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN, null);
        Facility facility = new Facility("F10", "TEST", null, List.of(feature));
        FacilityViewCommand command = new FacilityViewCommand(
                new FacilityManager(List.of(facility)), "TEST");

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        assertEquals("[F10] TEST\n\nAccessibility Features:\nLIFT | UNKNOWN\n\n"
                + AccessibilityDisclaimer.TEXT, message);
    }

    /**
     * Tests that a facility without features clearly reports the missing data.
     */
    @Test
    public void execute_noFeatures_displaysNoFeaturesMessage() throws UniEnableException {
        Facility facility = new Facility("F01", "AS1", "Arts Block 1", List.of());
        FacilityViewCommand command = new FacilityViewCommand(
                new FacilityManager(List.of(facility)), "AS1");

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        assertTrue(message.contains("Accessibility Features:\nNo accessibility features recorded."));
        assertTrue(message.endsWith(AccessibilityDisclaimer.TEXT));
    }

    /**
     * Tests that unknown facilities throw a checked exception with supported names and usage guidance.
     */
    @Test
    public void execute_unknownFacility_throwsHelpfulException() {
        Facility facility = new Facility("F01", "AS1", "Arts Block 1", List.of());
        FacilityViewCommand command = new FacilityViewCommand(
                new FacilityManager(List.of(facility)), "AS10");

        UniEnableException exception = assertThrows(UniEnableException.class,
                () -> command.execute(new ActivityList(), new Storage()));

        assertEquals("[WARNING] Unknown facility 'AS10'.\n\nSupported facilities:\nAS1"
                + "\n\nUsage: facility HUB\nExample: facility AS4",
                exception.getMessage());
    }

    /**
     * Tests that lookup in an empty dataset throws an exception reporting no supported facilities.
     */
    @Test
    public void execute_emptyDataset_throwsWithNoSupportedFacilities() {
        FacilityViewCommand command = new FacilityViewCommand(
                new FacilityManager(List.of()), "AS1");

        UniEnableException exception = assertThrows(UniEnableException.class,
                () -> command.execute(new ActivityList(), new Storage()));

        assertTrue(exception.getMessage().contains("Supported facilities:\nNone"));
    }
}
