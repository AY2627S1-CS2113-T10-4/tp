package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import seedu.unienable.model.Connection;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;

/**
 * Protects endpoint identity and combined filters for accessible route graph construction.
 */
class ConnectionManagerTest {
    private final Connection first = new Connection(1, "AS1", "AS2", 10, AccessibilityStatus.YES,
            TraversalType.PATH, ShelterStatus.YES, null, null);
    private final Connection second = new Connection(2, "AS2", "AS3", 20, AccessibilityStatus.UNKNOWN,
            TraversalType.RAMP, ShelterStatus.NO, "Step", "Note");
    private final ConnectionManager connections = new ConnectionManager(List.of(first, second));

    @TestFactory
    Stream<DynamicTest> filters() throws Exception {
        return cases("filters").stream().map(row -> DynamicTest.dynamicTest(row[0], () -> {
            var matches = connections.findConnections(text(row[1]), text(row[2]),
                    row[3].isEmpty() ? null : TraversalType.valueOf(row[3]),
                    row[4].isEmpty() ? null : AccessibilityStatus.valueOf(row[4]),
                    row[5].isEmpty() ? null : ShelterStatus.valueOf(row[5]));
            assertEquals(row[6], String.join(",", matches.stream().map(c -> "" + c.getId()).toList()));
        }));
    }

    /**
     * Converts an omitted endpoint filter to null.
     */
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

    /**
     * Reads local UTF-8 cases; - means an omitted value and backslash-n means a newline.
     */
    private static List<String[]> cases(String name) throws IOException {
        try (var source = ConnectionManagerTest.class.getResourceAsStream("/" + name + ".tsv")) {
            if (source == null) {
                throw new IOException("Missing regression cases: " + name);
            }
            return Arrays.stream(new String(source.readAllBytes(), StandardCharsets.UTF_8).split("\\R"))
                    .filter(line -> !line.isBlank() && !line.startsWith("#"))
                    .map(line -> line.replace("\\n", "\n").split("\t", -1))
                    .map(fields -> Arrays.stream(fields).map(value -> value.equals("-") ? "" : value)
                            .toArray(String[]::new)).toList();
        }
    }
}
