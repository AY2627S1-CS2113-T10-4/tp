package seedu.unienable.model.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.FacilityFeature;

class FacilityConnectionEnumsTest {
    @Test
    public void accessibilityStatus_values_containsExactlyRequiredConstants() {
        assertArrayEquals(new AccessibilityStatus[] {AccessibilityStatus.YES, AccessibilityStatus.NO,
            AccessibilityStatus.UNKNOWN}, AccessibilityStatus.values());
    }

    @Test
    public void shelterStatus_values_containsExactlyRequiredConstants() {
        assertArrayEquals(new ShelterStatus[] {ShelterStatus.YES, ShelterStatus.NO, ShelterStatus.UNKNOWN},
                ShelterStatus.values());
    }

    @Test
    public void traversalType_values_containsExactlyRequiredConstants() {
        assertArrayEquals(new TraversalType[] {TraversalType.RAMP, TraversalType.SHELTERED_RAMP,
            TraversalType.LIFT, TraversalType.PATH, TraversalType.OTHER}, TraversalType.values());
    }

    @Test
    public void facilityFeatureType_values_containsExactlyRequiredConstants() {
        assertArrayEquals(new FacilityFeature.Type[] {FacilityFeature.Type.LIFT, FacilityFeature.Type.RAMP,
            FacilityFeature.Type.SHELTERED_RAMP, FacilityFeature.Type.ACCESSIBLE_WASHROOM,
            FacilityFeature.Type.STEP_FREE_ENTRANCE, FacilityFeature.Type.REST_POINT,
            FacilityFeature.Type.AUTOMATIC_DOOR, FacilityFeature.Type.OTHER}, FacilityFeature.Type.values());
    }
}
