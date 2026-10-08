package seedu.unienable.command.accessibility.facility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.CommandResult;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Tests the text returned by the read-only facility list command.
 */
public class FacilityListCommandTest {

    /**
     * Tests that all details and distinct statuses appear in their original facility and feature order.
     */
    @Test
    public void execute_multipleFacilities_listsAllDetailsInOrder() {
        FacilityFeature entrance = new FacilityFeature(
                FacilityFeature.Type.STEP_FREE_ENTRANCE, AccessibilityStatus.YES, "Main entrance");
        FacilityFeature lift = new FacilityFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.NO, "No lift recorded");
        FacilityFeature washroom = new FacilityFeature(
                FacilityFeature.Type.ACCESSIBLE_WASHROOM, AccessibilityStatus.UNKNOWN, "Not confirmed");
        Facility first = new Facility("F01", "AS1", "Arts Block 1", List.of(entrance, lift));
        Facility second = new Facility("F09", "CLB", "Central Library", List.of(washroom));
        FacilityManager manager = new FacilityManager(List.of(first, second));
        FacilityListCommand command = new FacilityListCommand(manager);

        CommandResult result = command.execute(new ActivityList(), new Storage());

        String expected = "Known facilities in the local reference:\n\n"
                + "[F01] AS1 - Arts Block 1\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Main entrance\n"
                + "LIFT | NO | No lift recorded\n\n"
                + "[F09] CLB - Central Library\n\nAccessibility Features:\n"
                + "ACCESSIBLE_WASHROOM | UNKNOWN | Not confirmed\n\n"
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

    /**
     * Tests that absent descriptions and notes do not appear as null or empty separators.
     */
    @Test
    public void execute_missingOptionalText_omitsNullAndBlankValues() {
        FacilityFeature lift = new FacilityFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN, null);
        FacilityFeature restPoint = new FacilityFeature(
                FacilityFeature.Type.REST_POINT, AccessibilityStatus.YES, " ");
        Facility first = new Facility("F01", "AS1", null, List.of(lift));
        Facility second = new Facility("F02", "AS2", " ", List.of(restPoint));
        FacilityListCommand command = new FacilityListCommand(
                new FacilityManager(List.of(first, second)));

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        String expected = "Known facilities in the local reference:\n\n"
                + "[F01] AS1\n\nAccessibility Features:\nLIFT | UNKNOWN\n\n"
                + "[F02] AS2\n\nAccessibility Features:\nREST_POINT | YES\n\n"
                + AccessibilityDisclaimer.TEXT;
        assertEquals(expected, message);
    }

    /**
     * Tests that a facility without feature records explicitly reports the missing data.
     */
    @Test
    public void execute_noRecordedFeatures_displaysMissingData() {
        Facility facility = new Facility("F01", "AS1", "Arts Block 1", List.of());
        FacilityListCommand command = new FacilityListCommand(
                new FacilityManager(List.of(facility)));

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        String expected = "Known facilities in the local reference:\n\n"
                + "[F01] AS1 - Arts Block 1\n\nAccessibility Features:\n"
                + "No accessibility features recorded.\n\n"
                + AccessibilityDisclaimer.TEXT;
        assertEquals(expected, message);
    }
}
