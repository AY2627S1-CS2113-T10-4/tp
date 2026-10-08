package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.storage.FacilityStorage;

/**
 * Tests facility lookups and feature filtering without involving the CLI.
 */
class FacilityManagerTest {
    /**
     * Checks that facilities are returned in source order through an unmodifiable list.
     */
    @Test
    public void getFacilities_preservesOrderAndCannotBeModified() {
        Facility as1 = facility("F01", "AS1", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO));
        Facility as2 = facility("F02", "AS2", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES));
        FacilityManager manager = new FacilityManager(List.of(as1, as2));

        assertEquals(List.of(as1, as2), manager.getFacilities());
        assertThrows(UnsupportedOperationException.class, () -> manager.getFacilities().clear());
    }

    /**
     * Checks that clearing the caller's list does not remove facilities from the manager.
     */
    @Test
    public void constructor_mutatedSourceList_doesNotChangeManager() {
        Facility as1 = facility("F01", "AS1");
        List<Facility> source = new ArrayList<>(List.of(as1));
        FacilityManager manager = new FacilityManager(source);
        source.clear();

        assertEquals(List.of(as1), manager.getFacilities());
    }

    /**
     * Checks that ID and name searches ignore capitalization and surrounding spaces.
     */
    @Test
    public void findFacility_idOrName_ignoresCaseAndWhitespace() {
        Facility as1 = facility("F01", "AS1");
        FacilityManager manager = new FacilityManager(List.of(as1));

        assertSame(as1, manager.findFacility(" f01 ").orElseThrow());
        assertSame(as1, manager.findFacility(" as1 ").orElseThrow());
    }

    /**
     * Checks that unknown and blank identifiers return no facility.
     */
    @Test
    public void findFacility_missingOrBlank_returnsEmpty() {
        FacilityManager manager = new FacilityManager(List.of(facility("F01", "AS1")));

        assertFalse(manager.findFacility("F99").isPresent());
        assertFalse(manager.findFacility("  ").isPresent());
    }

    /**
     * Checks that an omitted status matches YES only and an explicit NO filter remains distinct.
     */
    @Test
    public void findByFeature_omittedStatus_matchesYesOnly() {
        Facility no = facility("F01", "AS1", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO));
        Facility yes = facility("F02", "AS2", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES));
        Facility unknown = facility("F03", "AS3", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN));
        FacilityManager manager = new FacilityManager(List.of(no, yes, unknown));

        assertEquals(List.of(yes), manager.findByFeature(FacilityFeature.Type.LIFT));
        assertEquals(List.of(no), manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO));
    }

    /**
     * Checks that UNKNOWN matches recorded UNKNOWN values rather than missing features.
     */
    @Test
    public void findByFeature_unknown_matchesOnlyExplicitUnknown() {
        Facility unknown = facility("F01", "AS1", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN));
        Facility absent = facility("F02", "AS2");
        FacilityManager manager = new FacilityManager(List.of(unknown, absent));

        assertEquals(List.of(unknown), manager.findByFeature(FacilityFeature.Type.LIFT,
                AccessibilityStatus.UNKNOWN));
    }

    /**
     * Checks that repeated matching feature records return a facility only once.
     */
    @Test
    public void findByFeature_duplicateMatches_returnsFacilityOnce() {
        Facility as1 = facility("F01", "AS1",
                feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES),
                feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES));
        FacilityManager manager = new FacilityManager(List.of(as1));

        assertEquals(List.of(as1), manager.findByFeature(FacilityFeature.Type.LIFT));
    }

    /**
     * Checks that callers cannot modify the feature-filter results.
     */
    @Test
    public void findByFeature_resultCannotBeModified() {
        FacilityManager manager = new FacilityManager(List.of(facility("F01", "AS1",
                feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES))));

        assertThrows(UnsupportedOperationException.class,
                () -> manager.findByFeature(FacilityFeature.Type.LIFT).clear());
    }

    /**
     * Checks the bundled lift and rest-point results, including NO and absent UNKNOWN matches.
     */
    @Test
    public void findByFeature_bundledDataset_returnsExpectedFacilities() throws UniEnableException {
        List<Facility> facilities = new FacilityStorage().load().getRecords();
        FacilityManager manager = new FacilityManager(facilities);

        List<Facility> withLift = manager.findByFeature(FacilityFeature.Type.LIFT);
        List<Facility> withoutLift = manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO);
        List<Facility> withRestPoint = manager.findByFeature(FacilityFeature.Type.REST_POINT);

        assertEquals(List.of("AS3", "AS4", "AS5", "AS6", "AS7", "AS8", "CLB"), facilityNames(withLift));
        assertEquals(List.of("AS1", "AS2"), facilityNames(withoutLift));
        assertEquals(List.of("AS8"), facilityNames(withRestPoint));
        assertTrue(manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN).isEmpty());
    }

    /**
     * Creates a facility with only the identity and features needed by the test.
     *
     * @param id the facility ID
     * @param name the facility name
     * @param features the recorded accessibility features
     * @return a facility with no description
     */
    private static Facility facility(String id, String name, FacilityFeature... features) {
        return new Facility(id, name, null, List.of(features));
    }

    /**
     * Creates a recorded feature without optional notes.
     *
     * @param type the feature type
     * @param status the recorded accessibility status
     * @return the feature used in the test
     */
    private static FacilityFeature feature(FacilityFeature.Type type, AccessibilityStatus status) {
        return new FacilityFeature(type, status, null);
    }

    /**
     * Returns facility names in result order so assertions also check ordering.
     *
     * @param facilities facilities returned by a lookup or filter
     * @return their names in the same order
     */
    private static List<String> facilityNames(List<Facility> facilities) {
        List<String> names = new ArrayList<>();
        for (Facility facility : facilities) {
            names.add(facility.getName());
        }
        return names;
    }
}
