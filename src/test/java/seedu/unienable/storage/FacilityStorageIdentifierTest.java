package seedu.unienable.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.Facility;

/**
 * Tests identifier validation and agreement between storage uniqueness and facility lookup.
 */
class FacilityStorageIdentifierTest {
    private final FacilityStorage storage = new FacilityStorage();

    /**
     * Tests that padded IDs are rejected before registering a name or associating features.
     */
    @Test
    public void load_paddedIds_warnsAndKeepsValidRecordsFindable() throws UniEnableException {
        List<String> paddedIds = List.of(" F01", "F01 ", " F01 ", "\tF01", "F01\t", "\u2003F01\u2003");
        for (String paddedId : paddedIds) {
            String data = "FACILITY|" + paddedId + "|AS1|Invalid building\n"
                    + "FACILITY|F01|AS1|Valid building\n"
                    + "FEATURE|F01|LIFT|YES|Main lift\n";

            LoadResult<Facility> result = storage.load(new StringReader(data));

            assertEquals(List.of("Line 1: Facility ID and name must not have surrounding whitespace."),
                    result.getWarnings());
            assertEquals(1, result.getRecords().size());
            Facility facility = result.getRecords().get(0);
            assertEquals("F01", facility.getId());
            assertEquals(1, facility.getFeatures().size());
            FacilityManager manager = new FacilityManager(result.getRecords());
            assertSame(facility, manager.findFacility(" f01 ").orElseThrow());
            assertSame(facility, manager.findFacility(" as1 ").orElseThrow());
        }
    }

    /**
     * Tests that padded names do not reserve IDs and that descriptions and notes remain unchanged.
     */
    @Test
    public void load_paddedNames_warnsAndPreservesValidText() throws UniEnableException {
        List<String> paddedNames = List.of(" AS1", "AS1 ", " AS1 ", "\tAS1", "AS1\t", "\u2003AS1\u2003");
        for (String paddedName : paddedNames) {
            String data = "FACILITY|F01|" + paddedName + "|Invalid building\n"
                    + "FACILITY|f01|As1| Description with spaces \n"
                    + "FEATURE|f01|RAMP|YES| Notes with spaces \n";

            LoadResult<Facility> result = storage.load(new StringReader(data));

            assertEquals(List.of("Line 1: Facility ID and name must not have surrounding whitespace."),
                    result.getWarnings());
            assertEquals(1, result.getRecords().size());
            Facility facility = result.getRecords().get(0);
            assertEquals("f01", facility.getId());
            assertEquals("As1", facility.getName());
            assertEquals(" Description with spaces ", facility.getDescription());
            assertEquals(" Notes with spaces ", facility.getFeatures().get(0).getNotes());
            FacilityManager manager = new FacilityManager(result.getRecords());
            assertSame(facility, manager.findFacility("F01").orElseThrow());
            assertSame(facility, manager.findFacility("AS1").orElseThrow());
        }
    }

    /**
     * Tests Unicode-equivalent IDs while preserving the first spelling and its feature association.
     */
    @Test
    public void load_unicodeDuplicateIds_warnsAndPreservesFirstFacility() throws UniEnableException {
        List<String> duplicateIds = List.of("Fi1", "FI1", "F\u01311");
        for (String duplicateId : duplicateIds) {
            String data = "FACILITY|F\u01301|AS1|First building\n"
                    + "FACILITY|" + duplicateId + "|AS2|Duplicate building\n"
                    + "FACILITY|F02|AS2|Second building\n"
                    + "FEATURE|F\u01301|LIFT|YES|Main lift\n";

            LoadResult<Facility> result = storage.load(new StringReader(data));

            assertEquals(List.of("Line 2: Duplicate facility ID: " + duplicateId), result.getWarnings());
            assertEquals(2, result.getRecords().size());
            Facility first = result.getRecords().get(0);
            assertEquals("F\u01301", first.getId());
            assertEquals(1, first.getFeatures().size());
            assertEquals("AS2", result.getRecords().get(1).getName());
            FacilityManager manager = new FacilityManager(result.getRecords());
            assertSame(first, manager.findFacility(duplicateId).orElseThrow());
            assertSame(first, manager.findFacility("F\u01301").orElseThrow());
        }
    }

    /**
     * Tests that Unicode duplicate detection also works when the ASCII spelling appears first.
     */
    @Test
    public void load_asciiIdBeforeUnicodeDuplicate_preservesAsciiSpelling() throws UniEnableException {
        String data = "FACILITY|Fi1|AS1|First building\n"
                + "FACILITY|F\u01301|AS2|Duplicate building\n"
                + "FEATURE|Fi1|RAMP|YES|Entrance ramp\n";

        LoadResult<Facility> result = storage.load(new StringReader(data));

        assertEquals(List.of("Line 2: Duplicate facility ID: F\u01301"), result.getWarnings());
        assertEquals(1, result.getRecords().size());
        Facility first = result.getRecords().get(0);
        assertEquals("Fi1", first.getId());
        assertEquals(1, first.getFeatures().size());
        FacilityManager manager = new FacilityManager(result.getRecords());
        assertSame(first, manager.findFacility("F\u01301").orElseThrow());
    }

    /**
     * Tests that Unicode-equivalent names are rejected without reserving the rejected record's ID.
     */
    @Test
    public void load_unicodeDuplicateNames_warnsAndAllowsIdReuse() throws UniEnableException {
        String data = "FACILITY|F01|AS\u0130|First building\n"
                + "FACILITY|F02|ASi|Duplicate building\n"
                + "FACILITY|F02|AS2|Second building\n";

        LoadResult<Facility> result = storage.load(new StringReader(data));

        assertEquals(List.of("Line 2: Duplicate facility name: ASi"), result.getWarnings());
        assertEquals(2, result.getRecords().size());
        assertEquals("F02", result.getRecords().get(1).getId());
        assertEquals("AS2", result.getRecords().get(1).getName());
        FacilityManager manager = new FacilityManager(result.getRecords());
        assertSame(result.getRecords().get(0), manager.findFacility("ASi").orElseThrow());
    }

    /**
     * Tests that uppercase expansion does not incorrectly merge IDs that lookup treats as distinct.
     */
    @Test
    public void load_distinctUnicodeIdsWithSameUppercaseExpansion_keepsBothFacilities()
            throws UniEnableException {
        String data = "FACILITY|F\u00df1|AS1|First building\n"
                + "FACILITY|FSS1|AS2|Second building\n";

        LoadResult<Facility> result = storage.load(new StringReader(data));

        assertFalse(result.hasWarnings());
        assertEquals(2, result.getRecords().size());
        FacilityManager manager = new FacilityManager(result.getRecords());
        assertSame(result.getRecords().get(0), manager.findFacility("F\u00df1").orElseThrow());
        assertSame(result.getRecords().get(1), manager.findFacility("fss1").orElseThrow());
    }
}
