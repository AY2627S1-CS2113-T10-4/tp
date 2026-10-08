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
    /**
     * Checks that connections are returned in source order through an unmodifiable list.
     */
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

    /**
     * Checks that clearing the caller's list does not remove connections from the manager.
     */
    @Test
    public void constructor_mutatedSourceList_doesNotChangeManager() {
        Connection one = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        List<Connection> source = new ArrayList<>(List.of(one));
        ConnectionManager manager = new ConnectionManager(source);
        source.clear();

        assertEquals(List.of(one), manager.getConnections());
    }

    /**
     * Checks that an existing ID returns the original connection while missing IDs return no result.
     */
    @Test
    public void findConnection_existingAndMissing_returnsAppropriateResult() {
        Connection one = connection(12, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one));

        assertSame(one, manager.findConnection(12).orElseThrow());
        assertFalse(manager.findConnection(13).isPresent());
        assertFalse(manager.findConnection(-1).isPresent());
    }

    /**
     * Checks that a from-only filter matches either endpoint and excludes unrelated connections.
     */
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

    /**
     * Checks that a to-only filter matches either endpoint and excludes unrelated connections.
     */
    @Test
    public void findConnections_toAlone_matchesEitherStoredEndpoint() {
        Connection one = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection other = connection(2, "AS3", "CLB", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one, other));

        assertEquals(List.of(one), manager.findConnections(null, "AS1", null, null, null));
    }

    /**
     * Checks that two endpoint filters match both stored orientations in source order.
     */
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

    /**
     * Checks that two endpoints must belong to the same connection.
     */
    @Test
    public void findConnections_twoEndpoints_doNotMatchAcrossDifferentConnections() {
        Connection first = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection second = connection(2, "AS2", "AS3", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(first, second));

        assertTrue(manager.findConnections("AS1", "AS3", null, null, null).isEmpty());
    }

    /**
     * Checks that a connection must satisfy every supplied filter rather than just one.
     */
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

    /**
     * Checks that recorded UNKNOWN and NO accessibility and shelter statuses remain distinct.
     */
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

    /**
     * Checks that endpoint searches ignore capitalization and surrounding spaces.
     */
    @Test
    public void findConnections_caseAndSpaces_ignoredForEndpointFilters() {
        Connection one = connection(1, "CLB", "AS6", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one));

        assertEquals(List.of(one), manager.findConnections(" as6 ", " clb ", null, null, null));
    }

    /**
     * Checks that searching without any filters raises an argument exception.
     */
    @Test
    public void findConnections_noFilters_rejected() {
        ConnectionManager manager = new ConnectionManager(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> manager.findConnections(null, null, null, null, null));
    }

    /**
     * Checks that a blank endpoint is rejected even when another filter is supplied.
     */
    @Test
    public void findConnections_blankEndpoint_rejected() {
        ConnectionManager manager = new ConnectionManager(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> manager.findConnections("  ", null, null, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> manager.findConnections(null, "", TraversalType.PATH, null, null));
    }

    /**
     * Checks that callers cannot modify the connection-filter results.
     */
    @Test
    public void findConnections_resultCannotBeModified() {
        Connection one = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        ConnectionManager manager = new ConnectionManager(List.of(one));

        assertThrows(UnsupportedOperationException.class,
                () -> manager.findConnections("AS1", null, null, null, null).clear());
    }

    /**
     * Checks bundled reverse searches, combined filters, source order, and absent UNKNOWN matches.
     */
    @Test
    public void findConnections_bundledDataset_expectedResults() throws UniEnableException {
        List<Facility> facilities = new FacilityStorage().load().getRecords();
        List<Connection> connections = new ConnectionStorage(facilities).load().getRecords();
        ConnectionManager manager = new ConnectionManager(connections);

        assertEquals(10, manager.getConnections().size());
        List<Connection> reverseMatches = manager.findConnections("AS6", "CLB", null, null, null);
        List<Connection> unshelteredRamps = manager.findConnections(null, null, TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO);
        List<Connection> accessibleSheltered = manager.findConnections(null, null, null,
                AccessibilityStatus.YES, ShelterStatus.YES);

        assertEquals(List.of(1), connectionIds(reverseMatches));
        assertEquals(List.of(7), connectionIds(unshelteredRamps));
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 8, 9, 10), connectionIds(accessibleSheltered));
        assertTrue(manager.findConnections(null, null, null, AccessibilityStatus.UNKNOWN, null).isEmpty());
    }

    /**
     * Creates a connection with the fields used by filtering tests.
     *
     * @param id the connection ID
     * @param from the first stored facility name
     * @param to the second stored facility name
     * @param type the traversal type
     * @param accessibility the recorded accessibility status
     * @param shelter the recorded shelter status
     * @return a ten-metre connection without optional text
     */
    private static Connection connection(int id, String from, String to, TraversalType type,
            AccessibilityStatus accessibility, ShelterStatus shelter) {
        return new Connection(id, from, to, 10, accessibility, type, shelter, null, null);
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
