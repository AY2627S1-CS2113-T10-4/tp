package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Facility;

/**
 * Tests that facility IDs are unique regardless of letter case.
 */
class FacilityStorageDuplicateIdTest {
    private final FacilityStorage storage = new FacilityStorage();

    /**
     * Tests that a lowercase duplicate ID is skipped without reserving its facility name.
     */
    @Test
    public void load_lowercaseDuplicateId_skipsDuplicateAndAllowsNameReuse() throws UniEnableException {
        String data = "FACILITY|F01|AS1|First building\n"
                + "FACILITY|f01|AS2|Duplicate building\n"
                + "FACILITY|F02|AS2|Second building\n"
                + "FEATURE|F01|LIFT|YES|Main lift\n";

        LoadResult<Facility> result = storage.load(new StringReader(data));

        assertEquals(2, result.getRecords().size());
        assertEquals("F01", result.getRecords().get(0).getId());
        assertEquals("F02", result.getRecords().get(1).getId());
        assertEquals("AS2", result.getRecords().get(1).getName());
        assertEquals(1, result.getRecords().get(0).getFeatures().size());
        assertEquals(List.of("Line 2: Duplicate facility ID: f01"), result.getWarnings());
    }

    /**
     * Tests that the first lowercase ID is retained when a later uppercase duplicate appears.
     */
    @Test
    public void load_uppercaseDuplicateId_preservesFirstIdSpelling() throws UniEnableException {
        String data = "FACILITY|f01|AS1|First building\n"
                + "FACILITY|F01|AS2|Duplicate building\n"
                + "FEATURE|f01|RAMP|YES|Entrance ramp\n";

        LoadResult<Facility> result = storage.load(new StringReader(data));

        assertEquals(1, result.getRecords().size());
        assertEquals("f01", result.getRecords().get(0).getId());
        assertEquals(1, result.getRecords().get(0).getFeatures().size());
        assertEquals(List.of("Line 2: Duplicate facility ID: F01"), result.getWarnings());
    }

    /**
     * Tests that mixed-case duplicates are rejected without affecting unrelated valid records.
     */
    @Test
    public void load_mixedCaseDuplicateId_keepsOtherValidFacilities() throws UniEnableException {
        String data = "FACILITY|Fo1|AS1|First building\n"
                + "FACILITY|fO1|AS2|Duplicate building\n"
                + "FACILITY|F02|AS2|Second building\n";

        LoadResult<Facility> result = storage.load(new StringReader(data));

        assertEquals(2, result.getRecords().size());
        assertEquals("Fo1", result.getRecords().get(0).getId());
        assertEquals("AS2", result.getRecords().get(1).getName());
        assertEquals(List.of("Line 2: Duplicate facility ID: fO1"), result.getWarnings());
    }
}
