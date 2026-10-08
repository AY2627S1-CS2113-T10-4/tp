package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;

/**
 * Tests bundled connections and validation against explicitly provided facility names.
 */
class ConnectionStorageTest {
    private final ConnectionStorage storage = new ConnectionStorage(List.of(
            new Facility("F01", "AS1", null, List.of()), new Facility("F02", "AS2", null, List.of())));

    @Test
    public void load_bundledDataset_preservesAllRecordsAndReferences() throws UniEnableException {
        LoadResult<Facility> facilities = new FacilityStorage().load();
        LoadResult<Connection> result = new ConnectionStorage(facilities.getRecords()).load();
        assertFalse(facilities.hasWarnings());
        assertFalse(result.hasWarnings());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                result.getRecords().stream().map(Connection::getId).toList());
        assertEquals(List.of(70, 45, 90, 60, 50, 55, 130, 35, 55, 65),
                result.getRecords().stream().map(Connection::getDistanceInMetres).toList());
        assertEquals(List.of("CLB/AS6", "AS6/AS8", "AS6/AS1", "AS1/AS4", "AS4/AS7", "AS4/AS5",
            "AS1/AS2", "AS2/AS3", "AS8/CLB", "AS5/AS7"),
                result.getRecords().stream()
                        .map(connection -> connection.getFrom() + "/" + connection.getTo()).toList());
        Set<String> names = Set.copyOf(facilities.getRecords().stream().map(Facility::getName).toList());
        for (Connection connection : result.getRecords()) {
            assertTrue(names.contains(connection.getFrom()));
            assertTrue(names.contains(connection.getTo()));
            assertEquals(AccessibilityStatus.YES, connection.getAccessibility());
            assertEquals(connection.getId() == 7 ? TraversalType.RAMP : TraversalType.PATH, connection.getType());
            assertEquals(connection.getId() == 7 ? ShelterStatus.NO : ShelterStatus.YES, connection.getShelter());
        }
        assertEquals("Narrow passageway (2F link via LT9/LT10)", result.getRecords().get(3).getKnownBarrier());
        assertEquals("Via The Deck", result.getRecords().get(3).getNotes());
        assertEquals("Sheltered linkway via Central Library", result.getRecords().get(0).getNotes());
        assertNull(result.getRecords().get(1).getKnownBarrier());
        assertNull(result.getRecords().get(1).getNotes());
    }

    @Test
    public void load_reversedEndpointOrder_preservesStoredNamesWithoutCreatingReverseRecord()
            throws UniEnableException {
        LoadResult<Connection> result = load("CONNECTION|9|AS2|AS1|10|YES|PATH|YES\n");
        assertFalse(result.hasWarnings());
        assertEquals(1, result.getRecords().size());
        assertEquals("AS2", result.getRecords().get(0).getFrom());
        assertEquals("AS1", result.getRecords().get(0).getTo());
    }

    @Test
    public void load_eightNineAndTenFields_supportsOptionalBarrierAndNotes() throws UniEnableException {
        LoadResult<Connection> result = load("CONNECTION|1|AS1|AS2|10|YES|PATH|YES\n"
                + "CONNECTION|2|AS1|AS2|20|YES|PATH|YES|Narrow\n"
                + "CONNECTION|3|AS1|AS2|30|YES|PATH|YES|Step|Keep notes\n");
        assertFalse(result.hasWarnings());
        assertEquals(3, result.getRecords().size());
        assertNull(result.getRecords().get(0).getKnownBarrier());
        assertNull(result.getRecords().get(0).getNotes());
        assertEquals("Narrow", result.getRecords().get(1).getKnownBarrier());
        assertNull(result.getRecords().get(1).getNotes());
        assertEquals("Step", result.getRecords().get(2).getKnownBarrier());
        assertEquals("Keep notes", result.getRecords().get(2).getNotes());
    }

    @Test
    public void load_emptyOptionalStrings_returnsNullAndPreservesOtherText() throws UniEnableException {
        LoadResult<Connection> result = load("CONNECTION|1|AS1|AS2|10|YES|PATH|YES||\n"
                + "CONNECTION|2|AS1|AS2|10|YES|PATH|YES|\n"
                + "CONNECTION|3|AS1|AS2|10|YES|PATH|YES||  Café 学生  \n");
        assertFalse(result.hasWarnings());
        assertNull(result.getRecords().get(0).getKnownBarrier());
        assertNull(result.getRecords().get(0).getNotes());
        assertNull(result.getRecords().get(1).getKnownBarrier());
        assertNull(result.getRecords().get(1).getNotes());
        assertEquals("  Café 学生  ", result.getRecords().get(2).getNotes());
    }

    @Test
    public void load_duplicateIds_keepsFirstAndPreservesSourceOrder() throws UniEnableException {
        LoadResult<Connection> result = load("CONNECTION|2|AS1|AS2|10|YES|PATH|YES\n"
                + "CONNECTION|2|AS2|AS1|20|YES|RAMP|NO\nCONNECTION|1|AS2|AS1|30|YES|PATH|YES\n");
        assertEquals(List.of(2, 1), result.getRecords().stream().map(Connection::getId).toList());
        assertEquals(10, result.getRecords().get(0).getDistanceInMetres());
        assertWarning(result, 2, "Duplicate connection ID");
    }

    @Test
    public void load_invalidNumbers_skipsOnlyInvalidRecord() throws UniEnableException {
        for (String number : List.of("abc", "1.5", "2147483648")) {
            assertInvalid("CONNECTION|" + number + "|AS1|AS2|10|YES|PATH|YES", "Invalid connection ID");
            assertInvalid("CONNECTION|1|AS1|AS2|" + number + "|YES|PATH|YES", "Invalid distance");
        }
    }

    @Test
    public void load_nonPositiveDistances_rejected() throws UniEnableException {
        for (int distance : List.of(0, -1)) {
            assertInvalid("CONNECTION|1|AS1|AS2|" + distance + "|YES|PATH|YES", "distance must be positive");
        }
    }

    @Test
    public void load_invalidEnums_rejectedWithSpecificWarning() throws UniEnableException {
        assertInvalid("CONNECTION|1|AS1|AS2|10|MAYBE|PATH|YES", "Invalid accessibility status");
        assertInvalid("CONNECTION|1|AS1|AS2|10|YES|STAIRS|YES", "Invalid traversal type");
        assertInvalid("CONNECTION|1|AS1|AS2|10|YES|PATH|MAYBE", "Invalid shelter status");
    }

    @Test
    public void load_supportedEnums_preservesEveryStatusAndType() throws UniEnableException {
        for (AccessibilityStatus accessibility : AccessibilityStatus.values()) {
            for (ShelterStatus shelter : ShelterStatus.values()) {
                for (TraversalType type : TraversalType.values()) {
                    LoadResult<Connection> result = load("CONNECTION|1|AS1|AS2|10|" + accessibility
                            + "|" + type + "|" + shelter);
                    assertFalse(result.hasWarnings());
                    Connection connection = result.getRecords().get(0);
                    assertEquals(accessibility, connection.getAccessibility());
                    assertEquals(shelter, connection.getShelter());
                    assertEquals(type, connection.getType());
                }
            }
        }
    }

    @Test
    public void load_unknownEndpointsAndFacilityIds_rejected() throws UniEnableException {
        assertInvalid("CONNECTION|1|AS9|AS2|10|YES|PATH|YES", "Unknown facility endpoint");
        assertInvalid("CONNECTION|1|AS1|AS9|10|YES|PATH|YES", "Unknown facility endpoint");
        assertInvalid("CONNECTION|1|F01|F02|10|YES|PATH|YES", "Unknown facility endpoint");
    }

    @Test
    public void load_selfConnection_rejected() throws UniEnableException {
        assertInvalid("CONNECTION|1|AS1|AS1|10|YES|PATH|YES", "Self-connection");
    }

    @Test
    public void load_malformedRecordsAndEmptyMandatoryFields_rejected() throws UniEnableException {
        for (String line : List.of("BROKEN", "OTHER|1|AS1|AS2|10|YES|PATH|YES",
                "CONNECTION|1|AS1|AS2|10|YES|PATH", "CONNECTION|1|AS1|AS2|10|YES|PATH|YES|||Extra")) {
            assertInvalid(line, "CONNECTION requires 8 to 10 fields");
        }
        for (int index = 1; index < 8; index++) {
            String[] fields = "CONNECTION|1|AS1|AS2|10|YES|PATH|YES".split("\\|", -1);
            fields[index] = "";
            assertInvalid(String.join("|", fields), "Mandatory connection field");
        }
    }

    @Test
    public void load_invalidNeighbours_doesNotReserveIdsAndRetainsPhysicalLineNumbers() throws UniEnableException {
        LoadResult<Connection> result = load("# Header\n\nCONNECTION|1|AS1|AS9|10|YES|PATH|YES\n"
                + "CONNECTION|1|AS1|AS2|10|YES|PATH|YES\nBROKEN\nCONNECTION|2|AS2|AS1|20|NO|RAMP|UNKNOWN\n");
        assertEquals(List.of(1, 2), result.getRecords().stream().map(Connection::getId).toList());
        assertEquals(2, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).startsWith("Line 3:"));
        assertTrue(result.getWarnings().get(1).startsWith("Line 5:"));
    }

    @Test
    public void load_commentsAndBlankLines_ignored() throws UniEnableException {
        LoadResult<Connection> result = load("\n  \n# Reference data\n  # Another comment\n"
                + "CONNECTION|1|AS1|AS2|10|YES|PATH|YES\n");
        assertFalse(result.hasWarnings());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    public void loadResource_missingResource_failsClearly() {
        UniEnableException exception = assertThrows(UniEnableException.class,
                () -> storage.loadResource("/missing-connection-test-resource.txt"));
        assertTrue(exception.getMessage().contains("Missing connection dataset resource"));
    }

    @Test
    public void load_ioFailure_reportsUnderlyingCause() {
        IOException cause = new IOException("Simulated connection read failure");
        Reader source = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw cause;
            }

            @Override
            public void close() {
                // No resources are owned by this synthetic reader.
            }
        };
        UniEnableException exception = assertThrows(UniEnableException.class, () -> storage.load(source));
        assertTrue(exception.getMessage().contains("Simulated connection read failure"));
        assertSame(cause, exception.getCause());
    }

    @Test
    public void constructor_callerChangesFacilities_usesSnapshot() throws UniEnableException {
        List<Facility> facilities = new ArrayList<>(List.of(new Facility("F01", "AS1", null, List.of()),
                new Facility("F02", "AS2", null, List.of())));
        ConnectionStorage loader = new ConnectionStorage(facilities);
        facilities.clear();
        LoadResult<Connection> result = loader.load(new StringReader("CONNECTION|1|AS1|AS2|10|YES|PATH|YES"));
        assertFalse(result.hasWarnings());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    public void load_callerOwnedReader_leavesReaderOpen() throws UniEnableException {
        boolean[] closed = {false};
        StringReader source = new StringReader("CONNECTION|1|AS1|AS2|10|YES|PATH|YES") {
            @Override
            public void close() {
                closed[0] = true;
                super.close();
            }
        };
        storage.load(source);
        assertFalse(closed[0]);
        source.close();
    }

    private LoadResult<Connection> load(String text) throws UniEnableException {
        return storage.load(new StringReader(text));
    }

    private void assertInvalid(String invalidLine, String reason) throws UniEnableException {
        LoadResult<Connection> result = load(invalidLine + "\nCONNECTION|20|AS1|AS2|10|YES|PATH|YES\n");
        assertEquals(List.of(20), result.getRecords().stream().map(Connection::getId).toList());
        assertWarning(result, 1, reason);
    }

    private void assertWarning(LoadResult<Connection> result, int line, String reason) {
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).startsWith("Line " + line + ":"));
        assertTrue(result.getWarnings().get(0).contains(reason));
    }
}
