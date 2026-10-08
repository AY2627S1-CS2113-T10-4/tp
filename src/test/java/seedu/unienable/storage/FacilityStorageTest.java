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
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Tests bundled facility data and isolated malformed-line recovery.
 */
class FacilityStorageTest {
    private final FacilityStorage storage = new FacilityStorage();

    @Test
    public void load_bundledDataset_preservesFacilitiesAndFeatures() throws UniEnableException {
        LoadResult<Facility> result = storage.load();
        assertFalse(result.hasWarnings());
        assertEquals(List.of("F01", "F02", "F03", "F04", "F05", "F06", "F07", "F08", "F09"),
                result.getRecords().stream().map(Facility::getId).toList());
        assertEquals(List.of("AS1", "AS2", "AS3", "AS4", "AS5", "AS6", "AS7", "AS8", "CLB"),
                result.getRecords().stream().map(Facility::getName).toList());
        assertEquals(List.of(2, 2, 2, 3, 3, 3, 3, 4, 3),
                result.getRecords().stream().map(facility -> facility.getFeatures().size()).toList());
        Facility as1 = result.getRecords().get(0);
        assertEquals("Faculty of Arts and Social Sciences, Block 1", as1.getDescription());
        assertEquals(FacilityFeature.Type.STEP_FREE_ENTRANCE, as1.getFeatures().get(0).getType());
        assertEquals(AccessibilityStatus.NO, as1.getFeatures().get(1).getStatus());
        assertEquals("Building has no lift; floors 4-5 are not accessible", as1.getFeatures().get(1).getNotes());
        Facility as8 = result.getRecords().get(7);
        assertEquals(FacilityFeature.Type.REST_POINT, as8.getFeatures().get(3).getType());
        assertEquals("PitStop@FASS", as8.getFeatures().get(3).getNotes());
        assertEquals("4th floor toilet requires staff card access",
                result.getRecords().get(8).getFeatures().get(2).getNotes());
    }

    @Test
    public void load_featuresBeforeFacilities_associatesByIdInSourceOrder() throws UniEnableException {
        LoadResult<Facility> result = load("FEATURE|F02|LIFT|YES|Second hub\n"
                + "FACILITY|F01|AS1|First\nFEATURE|F01|RAMP|NO|First hub\n"
                + "FEATURE|F02|REST_POINT|UNKNOWN|Unconfirmed\nFACILITY|F02|AS2|Second\n");
        assertFalse(result.hasWarnings());
        assertEquals("First hub", result.getRecords().get(0).getFeatures().get(0).getNotes());
        List<FacilityFeature> features = result.getRecords().get(1).getFeatures();
        assertEquals(List.of(FacilityFeature.Type.LIFT, FacilityFeature.Type.REST_POINT),
                features.stream().map(FacilityFeature::getType).toList());
        assertEquals(AccessibilityStatus.UNKNOWN, features.get(1).getStatus());
    }

    @Test
    public void load_emptyOptionalText_returnsNullAndPreservesOtherText() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|\nFEATURE|F01|LIFT|YES\n"
                + "FEATURE|F01|RAMP|UNKNOWN|\nFACILITY|F02|AS2|  Café 学生  \n"
                + "FEATURE|F02|REST_POINT|NO|  Keep spacing  \n");
        assertFalse(result.hasWarnings());
        assertNull(result.getRecords().get(0).getDescription());
        assertNull(result.getRecords().get(0).getFeatures().get(0).getNotes());
        assertNull(result.getRecords().get(0).getFeatures().get(1).getNotes());
        assertEquals("  Café 学生  ", result.getRecords().get(1).getDescription());
        assertEquals("  Keep spacing  ", result.getRecords().get(1).getFeatures().get(0).getNotes());
    }

    @Test
    public void load_blankAndCommentLines_ignored() throws UniEnableException {
        LoadResult<Facility> result = load("\n  \n# Reference data\n  # Another comment\nFACILITY|F01|AS1|Block\n");
        assertFalse(result.hasWarnings());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    public void load_duplicateId_keepsFirstFacilityAndDoesNotReserveRejectedName() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|First\nFACILITY|F01|AS2|Duplicate\n"
                + "FACILITY|F02|AS2|Valid\n");
        assertEquals(List.of("AS1", "AS2"), result.getRecords().stream().map(Facility::getName).toList());
        assertWarning(result, 2, "Duplicate facility ID");
    }

    @Test
    public void load_duplicateNamesIgnoringCase_keepsFirstAndDoesNotReserveRejectedId() throws UniEnableException {
        for (String duplicateName : List.of("AS1", "as1")) {
            LoadResult<Facility> result = load("FACILITY|F01|AS1|First\nFACILITY|F02|" + duplicateName
                    + "|Duplicate\nFACILITY|F02|AS2|Valid\n");
            assertEquals(List.of("F01", "F02"), result.getRecords().stream().map(Facility::getId).toList());
            assertWarning(result, 2, "Duplicate facility name");
        }
    }

    @Test
    public void load_invalidFeatureType_skipsFeature() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|Block\nFEATURE|F01|ESCALATOR|YES|Notes\n");
        assertTrue(result.getRecords().get(0).getFeatures().isEmpty());
        assertWarning(result, 2, "Invalid feature type");
    }

    @Test
    public void load_invalidStatus_skipsFeature() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|Block\nFEATURE|F01|LIFT|MAYBE|Notes\n");
        assertTrue(result.getRecords().get(0).getFeatures().isEmpty());
        assertWarning(result, 2, "Invalid accessibility status");
    }

    @Test
    public void load_unknownFacilityReference_warnsWithoutRemovingValidFacility() throws UniEnableException {
        LoadResult<Facility> result = load("FEATURE|F99|LIFT|YES|Notes\nFACILITY|F01|AS1|Block\n");
        assertEquals(1, result.getRecords().size());
        assertWarning(result, 1, "Unknown facility ID");
    }

    @Test
    public void load_malformedRecords_skipsOnlyInvalidLines() throws UniEnableException {
        List<String> invalidLines = List.of("FACILITY|F02|AS2", "FACILITY|F02|AS2|Block|Extra",
                "FACILITY||AS2|Block", "FACILITY|F02||Block", "FACILITY| |AS2|Block",
                "FACILITY|F02| |Block", "FEATURE|F01|LIFT", "FEATURE|F01|LIFT|YES||Extra", "OTHER|Data");
        for (String invalidLine : invalidLines) {
            LoadResult<Facility> result = load("FACILITY|F01|AS1|Block\n" + invalidLine + "\n");
            assertEquals(1, result.getRecords().size(), invalidLine);
            assertEquals(1, result.getWarnings().size(), invalidLine);
            assertTrue(result.getWarnings().get(0).startsWith("Line 2:"), invalidLine);
        }
    }

    @Test
    public void load_invalidNeighbouringRecords_retainsValidRecordsAndPhysicalLineNumbers() throws UniEnableException {
        LoadResult<Facility> result = load("# Header\n\nFACILITY|F01|AS1|Block\nBROKEN\n"
                + "FEATURE|F01|LIFT|YES|Valid\nFEATURE|F01|RAMP|BAD|Invalid\nFACILITY|F02|AS2|Block\n");
        assertEquals(2, result.getRecords().size());
        assertEquals(1, result.getRecords().get(0).getFeatures().size());
        assertEquals(2, result.getWarnings().size());
        assertTrue(result.getWarnings().stream().anyMatch(warning -> warning.startsWith("Line 4:")));
        assertTrue(result.getWarnings().stream().anyMatch(warning -> warning.startsWith("Line 6:")));
    }

    @Test
    public void loadResource_missingResource_failsClearly() {
        UniEnableException exception = assertThrows(UniEnableException.class,
                () -> storage.loadResource("/missing-facility-test-resource.txt"));
        assertTrue(exception.getMessage().contains("Missing facility dataset resource"));
    }

    @Test
    public void load_ioFailure_reportsCauseRatherThanEmptySuccess() {
        IOException cause = new IOException("Simulated read failure");
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
        assertTrue(exception.getMessage().contains("Simulated read failure"));
        assertSame(cause, exception.getCause());
    }

    @Test
    public void load_callerOwnedReader_leavesReaderOpen() throws UniEnableException {
        boolean[] closed = {false};
        StringReader source = new StringReader("FACILITY|F01|AS1|Block") {
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

    private LoadResult<Facility> load(String text) throws UniEnableException {
        return storage.load(new StringReader(text));
    }

    private void assertWarning(LoadResult<Facility> result, int line, String reason) {
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).startsWith("Line " + line + ":"));
        assertTrue(result.getWarnings().get(0).contains(reason));
    }
}
