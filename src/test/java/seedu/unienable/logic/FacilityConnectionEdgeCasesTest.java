package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;

/**
 * Covers lookup and filtering edge cases that are not covered by the initial manager tests.
 */
class FacilityConnectionEdgeCasesTest {
    /**
     * Checks that an ID match takes precedence over another facility's matching name.
     */
    @Test
    public void findFacility_identifierMatchesIdAndAnotherName_idTakesPrecedence() {
        Facility nameMatch = facility("F01", "F02");
        Facility idMatch = facility("F02", "AS2");
        FacilityManager manager = new FacilityManager(List.of(nameMatch, idMatch));

        assertEquals(idMatch, manager.findFacility("f02").orElseThrow());
    }

    /**
     * Checks that a null facility identifier raises a null-pointer exception.
     */
    @Test
    public void findFacility_nullIdentifier_rejected() {
        FacilityManager manager = new FacilityManager(List.of());

        assertThrows(NullPointerException.class, () -> manager.findFacility(null));
    }

    /**
     * Checks that null feature types and statuses are rejected.
     */
    @Test
    public void findByFeature_nullTypeOrStatus_rejected() {
        FacilityManager manager = new FacilityManager(List.of());

        assertThrows(NullPointerException.class,
                () -> manager.findByFeature(null, AccessibilityStatus.YES));
        assertThrows(NullPointerException.class,
                () -> manager.findByFeature(FacilityFeature.Type.LIFT, null));
    }

    /**
     * Checks that an empty facility collection produces empty listings and search results.
     */
    @Test
    public void facilityManager_emptyDataset_returnsNoMatches() {
        FacilityManager manager = new FacilityManager(List.of());

        assertTrue(manager.getFacilities().isEmpty());
        assertTrue(manager.findFacility("AS1").isEmpty());
        assertTrue(manager.findByFeature(FacilityFeature.Type.LIFT).isEmpty());
    }

    /**
     * Checks that a shelter-only filter distinguishes YES, NO, and UNKNOWN.
     */
    @Test
    public void findConnections_shelterOnly_matchesExactStatuses() {
        Connection yes = connection(1, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection no = connection(2, "AS2", "AS3", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.NO);
        Connection unknown = connection(3, "AS3", "AS4", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.UNKNOWN);
        ConnectionManager manager = new ConnectionManager(List.of(yes, no, unknown));

        assertEquals(List.of(yes), manager.findConnections(null, null, null, null, ShelterStatus.YES));
        assertEquals(List.of(no), manager.findConnections(null, null, null, null, ShelterStatus.NO));
        assertEquals(List.of(unknown), manager.findConnections(null, null, null, null, ShelterStatus.UNKNOWN));
    }

    /**
     * Checks that a type-only filter keeps source order rather than sorting by connection ID.
     */
    @Test
    public void findConnections_typeOnly_preservesOriginalOrder() {
        Connection first = connection(9, "AS1", "AS2", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.YES);
        Connection other = connection(3, "AS2", "AS3", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO);
        Connection last = connection(1, "AS3", "AS4", TraversalType.PATH,
                AccessibilityStatus.NO, ShelterStatus.UNKNOWN);
        ConnectionManager manager = new ConnectionManager(List.of(first, other, last));

        assertEquals(List.of(first, last), manager.findConnections(null, null, TraversalType.PATH,
                null, null));
    }

    /**
     * Checks that a mismatch in any combined criterion produces no results.
     */
    @Test
    public void findConnections_conflictingFilters_returnsEmpty() {
        Connection only = connection(1, "AS1", "AS2", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.NO);
        ConnectionManager manager = new ConnectionManager(List.of(only));

        assertTrue(manager.findConnections("AS2", "AS1", TraversalType.RAMP,
                AccessibilityStatus.NO, ShelterStatus.NO).isEmpty());
        assertTrue(manager.findConnections("AS2", "AS1", TraversalType.PATH,
                AccessibilityStatus.YES, ShelterStatus.NO).isEmpty());
        assertTrue(manager.findConnections("AS2", "AS1", TraversalType.RAMP,
                AccessibilityStatus.YES, ShelterStatus.YES).isEmpty());
    }

    /**
     * Checks that an empty connection collection produces no matches for a valid filter.
     */
    @Test
    public void connectionManager_emptyDataset_returnsNoMatchesForValidFilter() {
        ConnectionManager manager = new ConnectionManager(List.of());

        assertTrue(manager.getConnections().isEmpty());
        assertFalse(manager.findConnection(1).isPresent());
        assertTrue(manager.findConnections("AS1", null, null, null, null).isEmpty());
    }

    /**
     * Creates a facility for identity lookup tests without recorded features.
     *
     * @param id the facility ID
     * @param name the facility name
     * @return a facility without a description or features
     */
    private static Facility facility(String id, String name) {
        return new Facility(id, name, null, List.of());
    }

    /**
     * Creates a connection with the fields needed by edge-case filters.
     *
     * @param id the connection ID
     * @param from the first stored facility name
     * @param to the second stored facility name
     * @param type the traversal type
     * @param accessibility the recorded accessibility status
     * @param shelter the recorded shelter status
     * @return a fifty-metre connection without optional text
     */
    private static Connection connection(int id, String from, String to, TraversalType type,
            AccessibilityStatus accessibility, ShelterStatus shelter) {
        return new Connection(id, from, to, 50, accessibility, type, shelter, null, null);
    }
}
