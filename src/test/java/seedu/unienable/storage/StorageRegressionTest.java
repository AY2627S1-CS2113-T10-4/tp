package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.regression.TestSupport;

/**
 * Exercises valid records and isolated malformed-data recovery without duplicating test methods.
 */
class StorageRegressionTest {
    private final FacilityStorage facilities = new FacilityStorage();
    private final ConnectionStorage connections = new ConnectionStorage(List.of(
            new Facility("F01", "AS1", null, List.of()), new Facility("F02", "AS2", null, List.of())));

    @TestFactory
    Stream<DynamicTest> records() throws Exception {
        return TestSupport.cases("storage").stream().map(row -> DynamicTest.dynamicTest(row[0], () -> {
            LoadResult<?> result = row[1].equals("F") ? facilities.load(new StringReader(row[2]))
                    : connections.load(new StringReader(row[2]));
            assertEquals(Integer.parseInt(row[3]), result.getRecords().size());
            assertEquals(Integer.parseInt(row[4]), result.getWarnings().size());
            assertEquals(!row[4].equals("0"), result.hasWarnings());
            assertEquals(row[5], String.join(",", result.getRecords().stream().map(this::describe).toList()));
            if (!row[6].isEmpty()) {
                assertTrue(String.join("\n", result.getWarnings()).contains(row[6]), result.getWarnings().toString());
            }
        }));
    }

    /**
     * Serializes every routing field so shortened tests still verify data is preserved exactly.
     */
    private String describe(Object record) {
        if (record instanceof Connection c) {
            return c.getId() + "|" + c.getFrom() + "|" + c.getTo() + "|" + c.getDistanceInMetres() + "|"
                    + c.getAccessibility() + "|" + c.getType() + "|" + c.getShelter() + "|"
                    + c.getKnownBarrier() + "|" + c.getNotes();
        }
        Facility f = (Facility) record;
        return f.getId() + "|" + f.getName() + "|" + f.getDescription() + "|" + String.join(";",
                f.getFeatures().stream().map(x -> x.getType() + "|" + x.getStatus() + "|" + x.getNotes()).toList());
    }

    @Test
    void bundledGraph_preservesAllEndpointsDistancesAndRestrictions() throws Exception {
        var source = new java.util.ArrayList<>(List.of(new Facility("F01", "AS1", null, List.of()),
                new Facility("F02", "AS2", null, List.of())));
        var snapshot = new ConnectionStorage(source);
        source.clear();
        assertEquals(1, snapshot.load(new StringReader("CONNECTION|1|AS1|AS2|10|YES|PATH|YES")).getRecords().size());
        var hubs = facilities.load();
        var edges = new ConnectionStorage(hubs.getRecords()).load();
        assertFalse(hubs.hasWarnings());
        assertFalse(edges.hasWarnings());
        assertEquals(List.of("AS1", "AS2", "AS3", "AS4", "AS5", "AS6", "AS7", "AS8", "CLB"),
                hubs.getRecords().stream().map(Facility::getName).toList());
        assertEquals(List.of(70, 45, 90, 60, 50, 55, 130, 35, 55, 65),
                edges.getRecords().stream().map(Connection::getDistanceInMetres).toList());
        List<String> names = hubs.getRecords().stream().map(Facility::getName).toList();
        for (Connection edge : edges.getRecords()) {
            assertTrue(names.contains(edge.getFrom()) && names.contains(edge.getTo()));
        }
        assertEquals("Narrow passageway (2F link via LT9/LT10)", edges.getRecords().get(3).getKnownBarrier());
        assertEquals("Via The Deck", edges.getRecords().get(3).getNotes());
    }

    @Test
    void failures_reportCausesAndMissingResources() {
        assertThrows(UniEnableException.class, () -> facilities.loadResource("/missing-facilities"));
        assertThrows(UniEnableException.class, () -> connections.loadResource("/missing-connections"));
        for (boolean facility : List.of(true, false)) {
            IOException cause = new IOException("read failed");
            Reader broken = new Reader() {
                @Override
                public int read(char[] buffer, int offset, int length) throws IOException {
                    throw cause;
                }
                @Override
                public void close() {
                    throw new AssertionError("Caller owns this reader");
                }
            };
            var error = assertThrows(UniEnableException.class,
                    () -> {
                        if (facility) {
                            facilities.load(broken);
                        } else {
                            connections.load(broken);
                        }
                    });
            assertEquals(cause, error.getCause());
            assertTrue(error.getMessage().contains("read failed"));
            Reader closeFailure = new FilterReader(new StringReader("")) {
                @Override
                public void close() throws IOException {
                    throw cause;
                }
            };
            var closeError = assertThrows(UniEnableException.class, () -> {
                if (facility) {
                    facilities.loadOwnedReader(closeFailure);
                } else {
                    connections.loadOwnedReader(closeFailure);
                }
            });
            assertEquals(cause, closeError.getCause());
        }
    }
}
