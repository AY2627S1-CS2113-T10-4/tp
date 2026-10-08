package seedu.unienable.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Tests feature fields and the distinction between recorded accessibility statuses.
 */
class FacilityFeatureTest {
    /**
     * Checks that a feature retains its type, recorded status, and notes.
     */
    @Test
    public void constructor_suppliedValues_preservesAllFields() {
        FacilityFeature feature = new FacilityFeature(FacilityFeature.Type.LIFT,
                AccessibilityStatus.YES, "Near entrance");
        assertEquals(FacilityFeature.Type.LIFT, feature.getType());
        assertEquals(AccessibilityStatus.YES, feature.getStatus());
        assertEquals("Near entrance", feature.getNotes());
    }

    /**
     * Checks that YES, NO, and UNKNOWN remain distinct recorded statuses.
     */
    @Test
    public void constructor_eachAccessibilityStatus_preservesStatus() {
        for (AccessibilityStatus status : AccessibilityStatus.values()) {
            FacilityFeature feature = new FacilityFeature(FacilityFeature.Type.RAMP, status, "Recorded status");
            assertEquals(status, feature.getStatus());
        }
    }

    /**
     * Checks that an absent feature note remains null.
     */
    @Test
    public void constructor_nullNotes_preservesNull() {
        FacilityFeature feature = new FacilityFeature(FacilityFeature.Type.REST_POINT,
                AccessibilityStatus.UNKNOWN, null);
        assertNull(feature.getNotes());
    }
}
