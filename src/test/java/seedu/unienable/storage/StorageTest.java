package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;

/**
 * Tests activity persistence, missing files, and recoverable malformed rows.
 */
class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void load_missingFileReturnsAnEmptyListWithoutWarnings() throws Exception {
        Storage storage = new Storage(tempDir.resolve("activities.txt"));

        ActivityList activities = storage.load();

        assertTrue(activities.getActivities().isEmpty());
        assertTrue(storage.getWarnings().isEmpty());
    }

    @Test
    void saveAndLoad_roundTripsAllActivityFields() throws Exception {
        Path file = tempDir.resolve("nested").resolve("activities.txt");
        Storage storage = new Storage(file);
        Activity done = new Activity("Exam", LocalDate.of(2026, 10, 18), LocalTime.of(9, 0),
                LocalTime.of(11, 0), DemandLevel.HIGH, true);
        Activity pending = new Activity("Gym", LocalDate.of(2026, 10, 20), LocalTime.of(17, 0),
                LocalTime.of(18, 0), DemandLevel.LOW, false);

        storage.save(new ActivityList(List.of(done, pending)));
        ActivityList loaded = storage.load();

        assertEquals(List.of("Exam|2026-10-18|09:00|11:00|HIGH|true",
                "Gym|2026-10-20|17:00|18:00|LOW|false"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
        assertEquals(2, loaded.getActivities().size());
        assertTrue(loaded.getActivities().get(0).isDone());
        assertFalse(loaded.getActivities().get(1).isDone());
        assertTrue(storage.getWarnings().isEmpty());
    }

    @Test
    void load_skipsMalformedRowsAndReportsLineNumbers() throws Exception {
        Path file = tempDir.resolve("activities.txt");
        Files.writeString(file, String.join("\n",
                "Valid|2026-10-18|09:00|11:00|HIGH|false",
                "broken row",
                "BadDate|2026-02-30|09:00|10:00|LOW|false"), StandardCharsets.UTF_8);
        Storage storage = new Storage(file);

        ActivityList loaded = storage.load();

        assertEquals(1, loaded.getActivities().size());
        assertEquals(2, storage.getWarnings().size());
        assertTrue(storage.getWarnings().get(0).startsWith("Line 2:"));
        assertTrue(storage.getWarnings().get(1).startsWith("Line 3:"));
    }

    @Test
    void save_afterMalformedLoad_preservesOriginalInBackupBeforeRepairingFile() throws Exception {
        Path file = tempDir.resolve("activities.txt");
        Files.writeString(file, "Valid|2026-10-18|09:00|11:00|HIGH|false\ninvalid",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file);
        ActivityList loaded = storage.load();

        storage.save(loaded);

        assertTrue(Files.exists(tempDir.resolve("activities.txt.corrupt.bak")));
        assertEquals("Valid|2026-10-18|09:00|11:00|HIGH|false\n",
                Files.readString(file, StandardCharsets.UTF_8));
        assertTrue(Files.readString(tempDir.resolve("activities.txt.corrupt.bak"),
                StandardCharsets.UTF_8).contains("invalid"));
    }
}
