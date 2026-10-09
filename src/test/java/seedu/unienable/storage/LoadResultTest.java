package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Checks defensive copies and immutable lists of records and dataset warnings.
 */
class LoadResultTest {
    @Test
    void snapshots_areIndependentAndUnmodifiable() {
        var facility = new Facility("F01", "AS1", null, List.of(
                new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, "Note")));
        var records = new ArrayList<>(List.of(facility));
        var warnings = new ArrayList<>(List.of("Line 2: invalid"));
        var loaded = new LoadResult<>(records, warnings);
        records.clear();
        warnings.clear();
        assertEquals(List.of(facility), loaded.getRecords());
        assertEquals(List.of("Line 2: invalid"), loaded.getWarnings());
        assertThrows(UnsupportedOperationException.class, () -> loaded.getRecords().clear());
        assertThrows(UnsupportedOperationException.class, () -> loaded.getWarnings().clear());
    }
}
