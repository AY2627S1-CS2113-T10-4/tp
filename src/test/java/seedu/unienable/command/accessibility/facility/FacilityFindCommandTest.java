package seedu.unienable.command.accessibility.facility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Tests the text produced by the read-only facility feature search command.
 */
public class FacilityFindCommandTest {

    /**
     * Tests that the YES search lists matching facilities in their original order.
     */
    @Test
    public void execute_yesStatus_listsMatchingFacilitiesInOrder() {
        Facility first = createFacility("F01", "AS1", AccessibilityStatus.YES);
        Facility second = createFacility("F02", "AS2", AccessibilityStatus.NO);
        Facility third = createFacility("F03", "AS3", AccessibilityStatus.YES);
        FacilityManager manager = new FacilityManager(List.of(first, second, third));
        FacilityFindCommand command = new FacilityFindCommand(
                manager, FacilityFeature.Type.LIFT, AccessibilityStatus.YES);

        CommandResult result = command.execute(new ActivityList(), new Storage());

        String expected = "Facilities with LIFT (status: YES):\n[F01] AS1\n[F03] AS3\n\n"
                + AccessibilityDisclaimer.TEXT;
        assertEquals(expected, result.getMessage());
        assertFalse(result.isExit());
    }

    /**
     * Tests that a NO search excludes facilities with a YES status.
     */
    @Test
    public void execute_noStatus_listsOnlyNoFacilities() {
        Facility first = createFacility("F01", "AS1", AccessibilityStatus.NO);
        Facility second = createFacility("F02", "AS2", AccessibilityStatus.YES);
        FacilityFindCommand command = new FacilityFindCommand(
                new FacilityManager(List.of(first, second)),
                FacilityFeature.Type.LIFT, AccessibilityStatus.NO);

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        assertTrue(message.contains("[F01] AS1"));
        assertFalse(message.contains("[F02] AS2"));
    }

    /**
     * Tests that UNKNOWN matches only explicit UNKNOWN records and explains the status.
     */
    @Test
    public void execute_unknownStatus_doesNotMatchMissingFeature() {
        Facility unknown = createFacility("F01", "AS1", AccessibilityStatus.UNKNOWN);
        Facility noFeature = new Facility("F02", "AS2", null, List.of());
        FacilityFindCommand command = new FacilityFindCommand(
                new FacilityManager(List.of(unknown, noFeature)),
                FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN);

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        assertTrue(message.contains("[F01] AS1"));
        assertFalse(message.contains("[F02] AS2"));
        assertTrue(message.contains("UNKNOWN means the local dataset does not confirm the feature."));
    }

    /**
     * Tests that an empty search result gives a clear message and the disclaimer.
     */
    @Test
    public void execute_noMatches_displaysNoMatchesAndDisclaimer() {
        Facility facility = createFacility("F01", "AS1", AccessibilityStatus.YES);
        FacilityFindCommand command = new FacilityFindCommand(
                new FacilityManager(List.of(facility)),
                FacilityFeature.Type.LIFT, AccessibilityStatus.NO);

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        assertEquals("Facilities with LIFT (status: NO):\nNo matching facilities found.\n\n"
                + AccessibilityDisclaimer.TEXT, message);
    }

    /**
     * Tests that the same facility is not printed twice for repeated matching features.
     */
    @Test
    public void execute_duplicateFeatures_listsFacilityOnlyOnce() {
        FacilityFeature firstLift = new FacilityFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.YES, "First lift");
        FacilityFeature secondLift = new FacilityFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.YES, "Second lift");
        Facility facility = new Facility("F01", "AS1", null, List.of(firstLift, secondLift));
        FacilityFindCommand command = new FacilityFindCommand(
                new FacilityManager(List.of(facility)),
                FacilityFeature.Type.LIFT, AccessibilityStatus.YES);

        String message = command.execute(new ActivityList(), new Storage()).getMessage();

        assertEquals("Facilities with LIFT (status: YES):\n[F01] AS1\n\n"
                + AccessibilityDisclaimer.TEXT, message);
    }

    /**
     * Tests that an empty dataset does not cause an error during a feature search.
     */
    @Test
    public void execute_emptyDataset_displaysNoMatchingFacilities() {
        FacilityFindCommand command = new FacilityFindCommand(
                new FacilityManager(List.of()),
                FacilityFeature.Type.REST_POINT, AccessibilityStatus.YES);

        CommandResult result = command.execute(new ActivityList(), new Storage());

        assertTrue(result.getMessage().contains("No matching facilities found."));
        assertFalse(result.isExit());
    }

    /**
     * Creates a facility containing one lift feature with the requested status.
     *
     * @param id stable facility ID
     * @param name short facility name
     * @param status recorded lift accessibility status
     * @return facility with one lift feature
     */
    private Facility createFacility(String id, String name, AccessibilityStatus status) {
        FacilityFeature lift = new FacilityFeature(FacilityFeature.Type.LIFT, status, null);
        return new Facility(id, name, null, List.of(lift));
    }
}
