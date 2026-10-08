package seedu.unienable.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;

class ConnectionTest {
    @Test
    public void constructor_suppliedValues_preservesAllFields() {
        Connection connection = new Connection(7, "F02", "F01", 125, AccessibilityStatus.NO,
                TraversalType.PATH, ShelterStatus.YES, "Stairs", "Between blocks");
        assertEquals(7, connection.getId());
        assertEquals("F02", connection.getFrom());
        assertEquals("F01", connection.getTo());
        assertEquals(125, connection.getDistanceInMetres());
        assertEquals(AccessibilityStatus.NO, connection.getAccessibility());
        assertEquals(TraversalType.PATH, connection.getType());
        assertEquals(ShelterStatus.YES, connection.getShelter());
        assertEquals("Stairs", connection.getKnownBarrier());
        assertEquals("Between blocks", connection.getNotes());
    }

    @Test
    public void constructor_eachStatusCombination_preservesDistinctStatuses() {
        for (AccessibilityStatus accessibility : AccessibilityStatus.values()) {
            for (ShelterStatus shelter : ShelterStatus.values()) {
                Connection connection = new Connection(1, "F01", "F02", 10, accessibility,
                        TraversalType.RAMP, shelter, null, null);
                assertEquals(accessibility, connection.getAccessibility());
                assertEquals(shelter, connection.getShelter());
            }
        }
    }

    @Test
    public void constructor_eachTraversalType_preservesType() {
        for (TraversalType type : TraversalType.values()) {
            Connection connection = new Connection(1, "F01", "F02", 10, AccessibilityStatus.UNKNOWN,
                    type, ShelterStatus.UNKNOWN, null, null);
            assertEquals(type, connection.getType());
        }
    }

    @Test
    public void constructor_nullOptionalFields_preservesNulls() {
        Connection connection = new Connection(1, "F01", "F02", 0, AccessibilityStatus.UNKNOWN,
                TraversalType.OTHER, ShelterStatus.UNKNOWN, null, null);
        assertNull(connection.getKnownBarrier());
        assertNull(connection.getNotes());
        assertEquals(0, connection.getDistanceInMetres());
    }
}
