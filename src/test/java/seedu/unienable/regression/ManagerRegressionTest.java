package seedu.unienable.regression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;

/**
 * Protects endpoint identity and filtering needed by a future accessible route graph.
 */
class ManagerRegressionTest {
    private final Connection first = new Connection(1, "AS1", "AS2", 10, AccessibilityStatus.YES,
            TraversalType.PATH, ShelterStatus.YES, null, null);
    private final Connection second = new Connection(2, "AS2", "AS3", 20, AccessibilityStatus.UNKNOWN,
            TraversalType.RAMP, ShelterStatus.NO, "Step", "Note");
    private final ConnectionManager connections = new ConnectionManager(List.of(first, second));

    @TestFactory
    Stream<DynamicTest> filters() throws Exception {
        return TestSupport.cases("filters").stream().map(row -> DynamicTest.dynamicTest(row[0], () -> {
            var matches = connections.findConnections(text(row[1]), text(row[2]),
                    row[3].isEmpty() ? null : TraversalType.valueOf(row[3]),
                    row[4].isEmpty() ? null : AccessibilityStatus.valueOf(row[4]),
                    row[5].isEmpty() ? null : ShelterStatus.valueOf(row[5]));
            assertEquals(row[6], String.join(",", matches.stream().map(c -> "" + c.getId()).toList()));
        }));
    }

    private String text(String value) {
        return value.isEmpty() ? null : value;
    }

    @Test
    void connectionBoundaries_rejectEmptyFiltersAndPreserveSnapshots() {
        assertThrows(IllegalArgumentException.class, () -> connections.findConnections(null, null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> connections.findConnections(" ", null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> connections.findConnections(null, " ", null, null, null));
        assertSame(first, connections.findConnection(1).orElseThrow());
        assertTrue(connections.findConnection(99).isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> connections.getConnections().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> connections.findConnections("AS1", null, null, null, null).clear());
        var source = new ArrayList<>(List.of(first));
        var snapshot = new ConnectionManager(source);
        source.clear();
        assertEquals(List.of(first), snapshot.getConnections());
        assertTrue(new ConnectionManager(List.of()).findConnections("AS1", null, null, null, null).isEmpty());
    }

    @Test
    void facilityIdentityAndStatuses_preserveDistinctMeaning() {
        var lift = new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, null);
        Facility firstHub = new Facility("F01", "AS1", null, List.of(lift, lift));
        Facility secondHub = new Facility("F02", "F01", null, List.of(
                new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN, null)));
        var source = new ArrayList<>(List.of(firstHub, secondHub));
        var manager = new FacilityManager(source);
        source.clear();
        for (String query : List.of("f01", " F01 ", "as1")) {
            assertSame(firstHub, manager.findFacility(query).orElseThrow());
        }
        assertSame(secondHub, manager.findFacility("f02").orElseThrow());
        assertTrue(manager.findFacility("missing").isEmpty());
        assertTrue(manager.findFacility(" ").isEmpty());
        assertEquals(List.of(firstHub), manager.findByFeature(FacilityFeature.Type.LIFT));
        assertEquals(List.of(secondHub),
                manager.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.UNKNOWN));
        assertTrue(manager.findByFeature(FacilityFeature.Type.RAMP).isEmpty());
        assertTrue(manager.findByFeature(FacilityFeature.Type.RAMP, AccessibilityStatus.UNKNOWN).isEmpty());
        assertThrows(NullPointerException.class, () -> manager.findFacility(null));
        assertThrows(NullPointerException.class, () -> manager.findByFeature(null));
        assertThrows(NullPointerException.class, () -> manager.findByFeature(FacilityFeature.Type.LIFT, null));
        assertThrows(UnsupportedOperationException.class, () -> manager.getFacilities().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> manager.findByFeature(FacilityFeature.Type.LIFT).clear());
        assertTrue(new FacilityManager(List.of()).findFacility("AS1").isEmpty());
    }
}
