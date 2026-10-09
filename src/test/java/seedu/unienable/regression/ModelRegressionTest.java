package seedu.unienable.regression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.storage.LoadResult;
import seedu.unienable.ui.accessibility.FacilityDetailsFormatter;

/**
 * Retains immutability checks rather than repeating tests of trivial getters.
 */
class ModelRegressionTest {
    @Test
    void snapshots_areIndependentAndUnmodifiable() {
        var features = new ArrayList<>(List.of(
                new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, "Note")));
        var facility = new Facility("F01", "AS1", null, features);
        features.clear();
        assertEquals(1, facility.getFeatures().size());
        assertThrows(UnsupportedOperationException.class, () -> facility.getFeatures().clear());
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
    @Test
    void optionalText_nullEmptyAndBlank_isOmittedFromFacilityOutput() {
        for (String text : new String[]{null, "", " "}) {
            var feature = new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, text);
            var facility = new Facility("F01", "AS1", text, List.of(feature));
            assertEquals("[F01] AS1\n\nAccessibility Features:\nLIFT | YES",
                    FacilityDetailsFormatter.format(facility));
        }
    }
}
