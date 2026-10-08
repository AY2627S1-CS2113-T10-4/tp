package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Test
    public void bundledDatasets_loadExpectedRecordsWithoutWarnings() throws UniEnableException {
        LoadResult<Facility> facilities = new FacilityStorage().load();
        LoadResult<Connection> connections = new ConnectionStorage(facilities.getRecords()).load();

        assertEquals(9, facilities.getRecords().size());
        assertEquals(10, connections.getRecords().size());
        assertFalse(facilities.hasWarnings());
        assertFalse(connections.hasWarnings());
    }

    @Test
    public void bundledConnections_allEndpointsResolveToFacilityNames() throws UniEnableException {
        List<Facility> facilities = new FacilityStorage().load().getRecords();
        List<Connection> connections = new ConnectionStorage(facilities).load().getRecords();
        Set<String> names = facilities.stream().map(Facility::getName).collect(Collectors.toSet());
        ConnectionManager manager = new ConnectionManager(connections);

        for (Connection connection : connections) {
            assertTrue(names.contains(connection.getFrom()));
            assertTrue(names.contains(connection.getTo()));
            assertEquals(connection, manager.findConnection(connection.getId()).orElseThrow());
        }
    }

    @Test
    public void bundledDataset_featureAndConnectionQueries_preserveAccessibilityDetails()
            throws UniEnableException {
        FacilityManager facilities = new FacilityManager(new FacilityStorage().load().getRecords());
        ConnectionManager connections = new ConnectionManager(
                new ConnectionStorage(facilities.getFacilities()).load().getRecords());

        assertEquals("AS1", facilities.findFacility("f01").orElseThrow().getName());
        assertEquals("CLB", facilities.findFacility("F09").orElseThrow().getName());
        assertEquals(List.of("AS1", "AS2"), facilities.findByFeature(
                FacilityFeature.Type.LIFT, AccessibilityStatus.NO)
                .stream().map(Facility::getName).toList());
        assertEquals(List.of("AS8"), facilities.findByFeature(FacilityFeature.Type.REST_POINT)
                .stream().map(Facility::getName).toList());

        assertEquals(List.of(1), connections.findConnections("AS6", "CLB", null, null, null)
                .stream().map(Connection::getId).toList());
        assertEquals(List.of(7), connections.findConnections("AS2", "AS1", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO).stream().map(Connection::getId).toList());
        assertTrue(connections.findConnection(7).orElseThrow().getNotes().contains("unsheltered"));
        assertTrue(connections.findConnection(4).orElseThrow().getKnownBarrier().contains("Narrow"));
    }
}
