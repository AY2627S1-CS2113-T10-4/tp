package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;
import seedu.unienable.storage.ConnectionStorage;
import seedu.unienable.storage.FacilityStorage;
import seedu.unienable.storage.LoadResult;

/**
 * Verifies that bundled facility and connection data work together through both lookup managers.
 */
class FacilityConnectionIntegrationTest {
    /**
     * Checks that both bundled datasets load their expected record counts without warnings.
     */
    @Test
    public void bundledDatasets_loadExpectedRecordsWithoutWarnings() throws UniEnableException {
        LoadResult<Facility> facilities = new FacilityStorage().load();
        LoadResult<Connection> connections = new ConnectionStorage(facilities.getRecords()).load();

        assertEquals(9, facilities.getRecords().size());
        assertEquals(10, connections.getRecords().size());
        assertFalse(facilities.hasWarnings());
        assertFalse(connections.hasWarnings());
    }

    /**
     * Checks that every bundled endpoint is a known facility name and each connection can be found by ID.
     */
    @Test
    public void bundledConnections_allEndpointsResolveToFacilityNames() throws UniEnableException {
        List<Facility> facilities = new FacilityStorage().load().getRecords();
        List<Connection> connections = new ConnectionStorage(facilities).load().getRecords();
        Set<String> names = new HashSet<>();
        for (Facility facility : facilities) {
            names.add(facility.getName());
        }
        ConnectionManager manager = new ConnectionManager(connections);

        for (Connection connection : connections) {
            assertTrue(names.contains(connection.getFrom()));
            assertTrue(names.contains(connection.getTo()));
            assertEquals(connection, manager.findConnection(connection.getId()).orElseThrow());
        }
    }

    /**
     * Checks that lookup and filtering retain bundled accessibility details, notes, and barriers.
     */
    @Test
    public void bundledDataset_featureAndConnectionQueries_preserveAccessibilityDetails()
            throws UniEnableException {
        List<Facility> loadedFacilities = new FacilityStorage().load().getRecords();
        FacilityManager facilities = new FacilityManager(loadedFacilities);
        List<Connection> loadedConnections = new ConnectionStorage(facilities.getFacilities()).load().getRecords();
        ConnectionManager connections = new ConnectionManager(loadedConnections);

        Facility as1 = facilities.findFacility("f01").orElseThrow();
        Facility clb = facilities.findFacility("F09").orElseThrow();
        List<Facility> withoutLift = facilities.findByFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.NO);
        List<Facility> withRestPoint = facilities.findByFeature(FacilityFeature.Type.REST_POINT);
        List<Connection> reverseMatches = connections.findConnections("AS6", "CLB", null, null, null);
        List<Connection> unshelteredRamps = connections.findConnections("AS2", "AS1", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO);
        Connection ramp = connections.findConnection(7).orElseThrow();
        Connection narrowPassage = connections.findConnection(4).orElseThrow();

        assertEquals("AS1", as1.getName());
        assertEquals("CLB", clb.getName());
        assertEquals(List.of("AS1", "AS2"), facilityNames(withoutLift));
        assertEquals(List.of("AS8"), facilityNames(withRestPoint));
        assertEquals(List.of(1), connectionIds(reverseMatches));
        assertEquals(List.of(7), connectionIds(unshelteredRamps));
        assertTrue(ramp.getNotes().contains("unsheltered"));
        assertTrue(narrowPassage.getKnownBarrier().contains("Narrow"));
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

    /**
     * Returns connection IDs in result order so assertions also check ordering.
     *
     * @param connections connections returned by a loader or filter
     * @return their IDs in the same order
     */
    private static List<Integer> connectionIds(List<Connection> connections) {
        List<Integer> ids = new ArrayList<>();
        for (Connection connection : connections) {
            ids.add(connection.getId());
        }
        return ids;
    }
}
