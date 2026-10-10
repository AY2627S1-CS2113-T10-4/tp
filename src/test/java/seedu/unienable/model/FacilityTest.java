package seedu.unienable.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Checks that a facility owns an immutable snapshot of its features.
 */
class FacilityTest {
    @Test
    void snapshots_areIndependentAndUnmodifiable() {
        var features = new ArrayList<>(List.of(
                new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, "Note")));
        var facility = new Facility("F01", "AS1", null, features);
        features.clear();
        assertEquals(1, facility.getFeatures().size());
        assertThrows(UnsupportedOperationException.class, () -> facility.getFeatures().clear());
    }
}
