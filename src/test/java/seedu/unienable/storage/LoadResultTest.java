package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies load-result records, warnings, and collection immutability.
 */
public class LoadResultTest {

    @Test
    public void constructor_validRecords_returnsRecords() {
        LoadResult<String> result = new LoadResult<>(
                List.of("AS1", "AS2"), List.of());

        assertEquals(List.of("AS1", "AS2"), result.getRecords());
        assertEquals(List.of(), result.getWarnings());
        assertFalse(result.hasWarnings());
    }

    @Test
    public void constructor_withWarnings_reportsWarnings() {
        LoadResult<String> result = new LoadResult<>(
                List.of("AS1"), List.of("Line 2 was skipped"));

        assertTrue(result.hasWarnings());
        assertEquals(List.of("Line 2 was skipped"), result.getWarnings());
    }

    @Test
    public void constructor_mutableInputs_defensivelyCopies() {
        List<String> records = new ArrayList<>(List.of("AS1"));
        List<String> warnings = new ArrayList<>();

        LoadResult<String> result = new LoadResult<>(records, warnings);
        records.clear();
        records.add("AS2");
        warnings.add("Line 2 was skipped");

        assertEquals(List.of("AS1"), result.getRecords());
        assertEquals(List.of(), result.getWarnings());
        assertFalse(result.hasWarnings());
    }

    @Test
    public void constructor_emptyRecordsWithWarnings_preservesWarnings() {
        LoadResult<String> result = new LoadResult<>(
                List.of(), List.of("Line 1 was skipped"));

        assertTrue(result.getRecords().isEmpty());
        assertEquals(List.of("Line 1 was skipped"), result.getWarnings());
        assertTrue(result.hasWarnings());
    }

    @Test
    public void getRecords_mutationAttempt_rejected() {
        LoadResult<String> result = new LoadResult<>(List.of("AS1"), List.of());

        assertThrows(UnsupportedOperationException.class, () -> result.getRecords().add("AS2"));
        assertEquals(List.of("AS1"), result.getRecords());
    }

    @Test
    public void getWarnings_mutationAttempt_rejected() {
        LoadResult<String> result = new LoadResult<>(List.of("AS1"), List.of("Line 2 was skipped"));

        assertThrows(UnsupportedOperationException.class, () -> result.getWarnings().clear());
        assertEquals(List.of("Line 2 was skipped"), result.getWarnings());
        assertTrue(result.hasWarnings());
    }
}
