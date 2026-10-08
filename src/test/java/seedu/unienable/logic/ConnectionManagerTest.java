package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;
import seedu.unienable.storage.ConnectionStorage;
import seedu.unienable.storage.FacilityStorage;

/**
 * Tests connection lookups and filters without involving CLI command parsing or formatting.
 */
class ConnectionManagerTest {
    @Test
    public void getConnections_preservesOrderAndCannotBeModified() {
        Connection first = connection(2, "AS2", "AS3", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection second = connection(1, "AS1", "AS2", TraversalType.RAMP,
                AccessibilityStatus.NO, ShelterStatus.NO);
        ConnectionManager manager = new ConnectionManager(List.of(first, second));

        assertEquals(List.of(first, second), manager.getConnections());
        assertThrows(UnsupportedOperationException.class, () -> manager.getConnections().clear());
    }

    @Test
    public void constructor_mutatedSourceList_doesNotChangeManager() {
        Connection one = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        List<Connection> source = new ArrayList<>(List.of(one));
        ConnectionManager manager = new ConnectionManager(source);
        source.clear();

        assertEquals(List.of(one), manager.getConnections());
    }

    @Test
    public void findConnection_existingAndMissing_returnsAppropriateResult() {
        Connection one = connection(12, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one));

        assertSame(one, manager.findConnection(12).orElseThrow());
        assertFalse(manager.findConnection(13).isPresent());
        assertFalse(manager.findConnection(-1).isPresent());
    }

    @Test
    public void findConnections_fromAlone_matchesEitherStoredEndpoint() {
        Connection first = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection second = connection(2, "AS3", "AS1", TraversalType.RAMP,
                AccessibilityStatus.NO, ShelterStatus.NO);
        Connection other = connection(3, "AS2", "AS3", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(first, second, other));

        assertEquals(List.of(first, second), manager.findConnections("as1", null, null, null, null));
    }

    @Test
    public void findConnections_toAlone_matchesEitherStoredEndpoint() {
        Connection one = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection other = connection(2, "AS3", "CLB", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one, other));

        assertEquals(List.of(one), manager.findConnections(null, "AS1", null, null, null));
    }

    @Test
    public void findConnections_twoEndpoints_matchesBothOrientations() {
        Connection forward = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection reverse = connection(2, "AS2", "AS1", TraversalType.RAMP,
                AccessibilityStatus.NO, ShelterStatus.NO);
        Connection other = connection(3, "AS1", "AS3", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(forward, reverse, other));

        assertEquals(List.of(forward, reverse), manager.findConnections("AS1", "AS2", null, null, null));
        assertEquals(List.of(forward, reverse), manager.findConnections("AS2", "AS1", null, null, null));
    }

    @Test
    public void findConnections_twoEndpoints_doNotMatchAcrossDifferentConnections() {
        Connection first = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection second = connection(2, "AS2", "AS3", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(first, second));

        assertTrue(manager.findConnections("AS1", "AS3", null, null, null).isEmpty());
    }

    @Test
    public void findConnections_allFilters_useAndSemantics() {
        Connection matching = connection(1, "AS1", "AS2", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO);
        Connection wrongType = connection(2, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.NO);
        Connection wrongStatus = connection(3, "AS1", "AS2", TraversalType.RAMP,
                AccessibilityStatus.NO, ShelterStatus.NO);
        Connection wrongShelter = connection(4, "AS1", "AS2", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection wrongEndpoint = connection(5, "AS3", "AS4", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO);
        ConnectionManager manager = new ConnectionManager(
                List.of(matching, wrongType, wrongStatus, wrongShelter, wrongEndpoint));

        assertEquals(List.of(matching), manager.findConnections("AS1", "AS2", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO));
    }

    @Test
    public void findConnections_unknownStatus_matchesOnlyExplicitUnknown() {
        Connection unknown = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.UNKNOWN, ShelterStatus.UNKNOWN);
        Connection no = connection(2, "AS2", "AS3", TraversalType.PATH,
                AccessibilityStatus.NO, ShelterStatus.NO);
        ConnectionManager manager = new ConnectionManager(List.of(unknown, no));

        assertEquals(List.of(unknown), manager.findConnections(null, null, null,
                AccessibilityStatus.UNKNOWN, ShelterStatus.UNKNOWN));
        assertEquals(List.of(no), manager.findConnections(null, null, null,
                AccessibilityStatus.NO, ShelterStatus.NO));
    }

    @Test
    public void findConnections_caseAndSpaces_ignoredForEndpointFilters() {
        Connection one = connection(1, "CLB", "AS6", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one));

        assertEquals(List.of(one), manager.findConnections(" as6 ", " clb ", null, null, null));
    }

    @Test
    public void findConnections_noFilters_rejected() {
        ConnectionManager manager = new ConnectionManager(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> manager.findConnections(null, null, null, null, null));
    }

    @Test
    public void findConnections_blankEndpoint_rejected() {
        ConnectionManager manager = new ConnectionManager(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> manager.findConnections("  ", null, null, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> manager.findConnections(null, "", TraversalType.PATH, null, null));
    }

    @Test
    public void findConnections_resultCannotBeModified() {
        Connection one = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one));

        assertThrows(UnsupportedOperationException.class,
                () -> manager.findConnections("AS1", null, null, null, null).clear());
    }

    @Test
    public void findConnections_bundledDataset_expectedResults() throws UniEnableException {
        List<Facility> facilities = new FacilityStorage().load().getRecords();
        ConnectionManager manager = new ConnectionManager(new ConnectionStorage(facilities).load().getRecords());

        assertEquals(10, manager.getConnections().size());
        assertEquals(List.of(1), manager.findConnections("AS6", "CLB", null, null, null)
                .stream().map(Connection::getId).toList());
        assertEquals(List.of(7), manager.findConnections(null, null, TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO).stream().map(Connection::getId).toList());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 8, 9, 10), manager.findConnections(null, null, null,
                AccessibilityStatus.YES, ShelterStatus.YES).stream().map(Connection::getId).toList());
        assertTrue(manager.findConnections(null, null, null, AccessibilityStatus.UNKNOWN, null).isEmpty());
    }

    private static Connection connection(int id, String from, String to, TraversalType type,
            AccessibilityStatus accessibility, ShelterStatus shelter) {
        return new Connection(id, from, to, 10, accessibility, type, shelter, null, null);
    }
}
