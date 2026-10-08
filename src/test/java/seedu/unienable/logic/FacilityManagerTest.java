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
    @Test
    public void getFacilities_preservesOrderAndCannotBeModified() {
        Facility as1 = facility("F01", "AS1", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO));
        Facility as2 = facility("F02", "AS2", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES));
        FacilityManager manager = new FacilityManager(List.of(as1, as2));

        assertEquals(List.of(as1, as2), manager.getFacilities());
        assertThrows(UnsupportedOperationException.class, () -> manager.getFacilities().clear());
    }

    @Test
    public void constructor_mutatedSourceList_doesNotChangeManager() {
        Facility as1 = facility("F01", "AS1");
        List<Facility> source = new ArrayList<>(List.of(as1));
        FacilityManager manager = new FacilityManager(source);
        source.clear();

        assertEquals(List.of(as1), manager.getFacilities());
    }

    @Test
    public void findFacility_idOrName_ignoresCaseAndWhitespace() {
        Facility as1 = facility("F01", "AS1");
        FacilityManager manager = new FacilityManager(List.of(as1));

        assertSame(as1, manager.findFacility(" f01 ").orElseThrow());
        assertSame(as1, manager.findFacility(" as1 ").orElseThrow());
    }

    @Test
    public void findFacility_missingOrBlank_returnsEmpty() {
        FacilityManager manager = new FacilityManager(List.of(facility("F01", "AS1")));

        assertFalse(manager.findFacility("F99").isPresent());
        assertFalse(manager.findFacility("  ").isPresent());
    }

    @Test
    public void findByFeature_omittedStatus_matchesYesOnly() {
        Facility no = facility("F01", "AS1", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO));
        Facility yes = facility("F02", "AS2", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES));
        Facility unknown = facility("F03", "AS3", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN));
        FacilityManager manager = new FacilityManager(List.of(no, yes, unknown));

        assertEquals(List.of(yes), manager.findByFeature(FacilityFeature.Type.LIFT));
        assertEquals(List.of(no), manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO));
    }

    @Test
    public void findByFeature_unknown_matchesOnlyExplicitUnknown() {
        Facility unknown = facility("F01", "AS1", feature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN));
        Facility absent = facility("F02", "AS2");
        FacilityManager manager = new FacilityManager(List.of(unknown, absent));

        assertEquals(List.of(unknown), manager.findByFeature(FacilityFeature.Type.LIFT,
                AccessibilityStatus.UNKNOWN));
    }

    @Test
    public void findByFeature_duplicateMatches_returnsFacilityOnce() {
        Facility as1 = facility("F01", "AS1",
                feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES),
                feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES));
        FacilityManager manager = new FacilityManager(List.of(as1));

        assertEquals(List.of(as1), manager.findByFeature(FacilityFeature.Type.LIFT));
    }

    @Test
    public void findByFeature_resultCannotBeModified() {
        FacilityManager manager = new FacilityManager(List.of(facility("F01", "AS1",
                feature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES))));

        assertThrows(UnsupportedOperationException.class,
                () -> manager.findByFeature(FacilityFeature.Type.LIFT).clear());
    }

    @Test
    public void findByFeature_bundledDataset_returnsExpectedFacilities() throws UniEnableException {
        FacilityManager manager = new FacilityManager(new FacilityStorage().load().getRecords());

        assertEquals(List.of("AS3", "AS4", "AS5", "AS6", "AS7", "AS8", "CLB"),
                manager.findByFeature(FacilityFeature.Type.LIFT).stream().map(Facility::getName).toList());
        assertEquals(List.of("AS1", "AS2"),
                manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO)
                        .stream().map(Facility::getName).toList());
        assertEquals(List.of("AS8"),
                manager.findByFeature(FacilityFeature.Type.REST_POINT).stream().map(Facility::getName).toList());
        assertTrue(manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN).isEmpty());
    }

    private static Facility facility(String id, String name, FacilityFeature... features) {
        return new Facility(id, name, null, List.of(features));
    }

    private static FacilityFeature feature(FacilityFeature.Type type, AccessibilityStatus status) {
        return new FacilityFeature(type, status, null);
    }
}
