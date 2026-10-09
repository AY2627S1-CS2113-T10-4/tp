package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Facility;

/**
 * Checks facility reference loading, malformed-record recovery, and I/O ownership.
 */
class FacilityStorageTest {
    private final FacilityStorage facilities = new FacilityStorage();

    @TestFactory
    Stream<DynamicTest> records() throws Exception {
        var rows = cases("storage").stream().filter(row -> row[1].equals("F"));
        return rows.map(row -> DynamicTest.dynamicTest(row[0], () -> {
            LoadResult<Facility> result = facilities.load(new StringReader(row[2]));
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
     * Serializes each facility field and ordered features for exact fixture comparisons.
     */
    private String describe(Facility f) {
        return f.getId() + "|" + f.getName() + "|" + f.getDescription() + "|" + String.join(";",
                f.getFeatures().stream().map(x -> x.getType() + "|" + x.getStatus() + "|" + x.getNotes()).toList());
    }

    @Test
    void bundledDataset_preservesFacilitiesWithoutWarnings() throws Exception {
        var hubs = facilities.load();
        assertFalse(hubs.hasWarnings());
        assertEquals(List.of("AS1", "AS2", "AS3", "AS4", "AS5", "AS6", "AS7", "AS8", "CLB"),
                hubs.getRecords().stream().map(Facility::getName).toList());
    }

    @Test
    void failures_reportCausesAndMissingResources() {
        assertThrows(UniEnableException.class, () -> facilities.loadResource("/missing-facilities"));
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
        var error = assertThrows(UniEnableException.class, () -> facilities.load(broken));
        assertEquals(cause, error.getCause());
        assertTrue(error.getMessage().contains("read failed"));
        Reader closeFailure = new FilterReader(new StringReader("")) {
            @Override
            public void close() throws IOException {
                throw cause;
            }
        };
        var closeError = assertThrows(UniEnableException.class, () -> facilities.loadOwnedReader(closeFailure));
        assertEquals(cause, closeError.getCause());
    }

    /**
     * Reads local UTF-8 cases; - means an omitted value and backslash-n means a newline.
     */
    private static List<String[]> cases(String name) throws IOException {
        try (var source = FacilityStorageTest.class.getResourceAsStream("/" + name + ".tsv")) {
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
