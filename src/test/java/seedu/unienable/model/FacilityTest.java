package seedu.unienable.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.enums.AccessibilityStatus;

class FacilityTest {
    @Test
    public void constructor_multipleFeatures_preservesAllFieldsAndOrder() {
        FacilityFeature lift = new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, null);
        FacilityFeature ramp = new FacilityFeature(FacilityFeature.Type.RAMP, AccessibilityStatus.UNKNOWN, null);
        Facility facility = new Facility("F01", "AS1", "Teaching block", List.of(lift, ramp));
        assertEquals("F01", facility.getId());
        assertEquals("AS1", facility.getName());
        assertEquals("Teaching block", facility.getDescription());
        assertEquals(List.of(lift, ramp), facility.getFeatures());
    }

    @Test
    public void constructor_emptyFeatures_preservesEmptyList() {
        Facility facility = new Facility("F02", "AS2", "Another block", List.of());
        assertTrue(facility.getFeatures().isEmpty());
    }

    @Test
    public void constructor_nullDescription_preservesNull() {
        Facility facility = new Facility("F01", "AS1", null, List.of());
        assertNull(facility.getDescription());
    }

    @Test
    public void constructor_inputListChanged_doesNotChangeFeatures() {
        FacilityFeature lift = new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, null);
        List<FacilityFeature> input = new ArrayList<>();
        input.add(lift);
        Facility facility = new Facility("F01", "AS1", null, input);
        input.clear();
        assertEquals(List.of(lift), facility.getFeatures());
    }

    @Test
    public void getFeatures_mutationAttempt_rejected() {
        FacilityFeature lift = new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, null);
        Facility facility = new Facility("F01", "AS1", null, List.of(lift));
        assertThrows(UnsupportedOperationException.class, () -> facility.getFeatures().clear());
        assertEquals(List.of(lift), facility.getFeatures());
    }
}
