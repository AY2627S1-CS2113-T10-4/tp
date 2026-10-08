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

    /**
     * Checks all bundled facility IDs, names, feature counts, and selected descriptions and notes.
     */
    @Test
    public void load_bundledDataset_preservesFacilitiesAndFeatures() throws UniEnableException {
        LoadResult<Facility> result = storage.load();
        List<Facility> facilities = result.getRecords();
        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<Integer> featureCounts = new ArrayList<>();
        for (Facility facility : facilities) {
            ids.add(facility.getId());
            names.add(facility.getName());
            featureCounts.add(facility.getFeatures().size());
        }

        assertFalse(result.hasWarnings());
        assertEquals(List.of("F01", "F02", "F03", "F04", "F05", "F06", "F07", "F08", "F09"), ids);
        assertEquals(List.of("AS1", "AS2", "AS3", "AS4", "AS5", "AS6", "AS7", "AS8", "CLB"), names);
        assertEquals(List.of(2, 2, 2, 3, 3, 3, 3, 4, 3), featureCounts);
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

    /**
     * Checks that features can precede their facilities while preserving facility and feature order.
     */
    @Test
    public void load_featuresBeforeFacilities_associatesByIdInSourceOrder() throws UniEnableException {
        LoadResult<Facility> result = load("FEATURE|F02|LIFT|YES|Second hub\n"
                + "FACILITY|F01|AS1|First\nFEATURE|F01|RAMP|NO|First hub\n"
                + "FEATURE|F02|REST_POINT|UNKNOWN|Unconfirmed\nFACILITY|F02|AS2|Second\n");
        assertFalse(result.hasWarnings());
        assertEquals("First hub", result.getRecords().get(0).getFeatures().get(0).getNotes());
        List<FacilityFeature> features = result.getRecords().get(1).getFeatures();
        List<FacilityFeature.Type> types = new ArrayList<>();
        for (FacilityFeature feature : features) {
            types.add(feature.getType());
        }
        assertEquals(List.of(FacilityFeature.Type.LIFT, FacilityFeature.Type.REST_POINT), types);
        assertEquals(AccessibilityStatus.UNKNOWN, features.get(1).getStatus());
    }

    /**
     * Checks that missing optional text becomes null while Unicode text and spacing are preserved.
     */
    @Test
    public void load_emptyOptionalText_returnsNullAndPreservesOtherText() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|\nFEATURE|F01|LIFT|YES\n"
                + "FEATURE|F01|RAMP|UNKNOWN|\nFACILITY|F02|AS2|  Café  \n"
                + "FEATURE|F02|REST_POINT|NO|  Keep spacing  \n");
        assertFalse(result.hasWarnings());
        assertNull(result.getRecords().get(0).getDescription());
        assertNull(result.getRecords().get(0).getFeatures().get(0).getNotes());
        assertNull(result.getRecords().get(0).getFeatures().get(1).getNotes());
        assertEquals("  Café  ", result.getRecords().get(1).getDescription());
        assertEquals("  Keep spacing  ", result.getRecords().get(1).getFeatures().get(0).getNotes());
    }

    /**
     * Checks that blank lines and comments produce neither records nor warnings.
     */
    @Test
    public void load_blankAndCommentLines_ignored() throws UniEnableException {
        LoadResult<Facility> result = load("\n  \n# Reference data\n  # Another comment\nFACILITY|F01|AS1|Block\n");
        assertFalse(result.hasWarnings());
        assertEquals(1, result.getRecords().size());
    }

    /**
     * Checks that a duplicate ID is skipped without reserving the rejected facility's name.
     */
    @Test
    public void load_duplicateId_keepsFirstFacilityAndDoesNotReserveRejectedName() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|First\nFACILITY|F01|AS2|Duplicate\n"
                + "FACILITY|F02|AS2|Valid\n");
        List<String> names = new ArrayList<>();
        for (Facility facility : result.getRecords()) {
            names.add(facility.getName());
        }
        assertEquals(List.of("AS1", "AS2"), names);
        assertWarning(result, 2, "Duplicate facility ID");
    }

    /**
     * Checks that duplicate names, including different capitalization, do not reserve rejected IDs.
     */
    @Test
    public void load_duplicateNamesIgnoringCase_keepsFirstAndDoesNotReserveRejectedId() throws UniEnableException {
        for (String duplicateName : List.of("AS1", "as1")) {
            LoadResult<Facility> result = load("FACILITY|F01|AS1|First\nFACILITY|F02|" + duplicateName
                    + "|Duplicate\nFACILITY|F02|AS2|Valid\n");
            List<String> ids = new ArrayList<>();
            for (Facility facility : result.getRecords()) {
                ids.add(facility.getId());
            }
            assertEquals(List.of("F01", "F02"), ids);
            assertWarning(result, 2, "Duplicate facility name");
        }
    }

    /**
     * Checks that an unsupported feature type is skipped with a line-numbered warning.
     */
    @Test
    public void load_invalidFeatureType_skipsFeature() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|Block\nFEATURE|F01|ESCALATOR|YES|Notes\n");
        assertTrue(result.getRecords().get(0).getFeatures().isEmpty());
        assertWarning(result, 2, "Invalid feature type");
    }

    /**
     * Checks that an unsupported accessibility status is skipped with a line-numbered warning.
     */
    @Test
    public void load_invalidStatus_skipsFeature() throws UniEnableException {
        LoadResult<Facility> result = load("FACILITY|F01|AS1|Block\nFEATURE|F01|LIFT|MAYBE|Notes\n");
        assertTrue(result.getRecords().get(0).getFeatures().isEmpty());
        assertWarning(result, 2, "Invalid accessibility status");
    }

    /**
     * Checks that an unknown feature reference warns without removing a valid facility.
     */
    @Test
    public void load_unknownFacilityReference_warnsWithoutRemovingValidFacility() throws UniEnableException {
        LoadResult<Facility> result = load("FEATURE|F99|LIFT|YES|Notes\nFACILITY|F01|AS1|Block\n");
        assertEquals(1, result.getRecords().size());
        assertWarning(result, 1, "Unknown facility ID");
    }

    /**
     * Checks that malformed records and blank required fields leave valid records intact.
     */
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

    /**
     * Checks that invalid neighboring records preserve valid features and physical warning line numbers.
     */
    @Test
    public void load_invalidNeighbouringRecords_retainsValidRecordsAndPhysicalLineNumbers() throws UniEnableException {
        LoadResult<Facility> result = load("# Header\n\nFACILITY|F01|AS1|Block\nBROKEN\n"
                + "FEATURE|F01|LIFT|YES|Valid\nFEATURE|F01|RAMP|BAD|Invalid\nFACILITY|F02|AS2|Block\n");
        assertEquals(2, result.getRecords().size());
        assertEquals(1, result.getRecords().get(0).getFeatures().size());
        assertEquals(2, result.getWarnings().size());
        boolean hasLine4Warning = false;
        boolean hasLine6Warning = false;
        for (String warning : result.getWarnings()) {
            if (warning.startsWith("Line 4:")) {
                hasLine4Warning = true;
            }
            if (warning.startsWith("Line 6:")) {
                hasLine6Warning = true;
            }
        }
        assertTrue(hasLine4Warning);
        assertTrue(hasLine6Warning);
    }

    /**
     * Checks that a missing bundled resource raises a descriptive exception.
     */
    @Test
    public void loadResource_missingResource_failsClearly() {
        UniEnableException exception = assertThrows(UniEnableException.class,
                () -> storage.loadResource("/missing-facility-test-resource.txt"));
        assertTrue(exception.getMessage().contains("Missing facility dataset resource"));
    }

    /**
     * Checks that a read failure reports its message and retains the original exception as its cause.
     */
    @Test
    public void load_ioFailure_reportsCauseRatherThanEmptySuccess() {
        IOException cause = new IOException("Simulated read failure");
        Reader source = new Reader() {
            /**
             * Simulates a read failure so the loader's exception handling can be checked.
             *
             * @param buffer the destination buffer
             * @param offset the starting buffer position
             * @param length the requested character count
             * @return no value because every read fails
             * @throws IOException always, using the original simulated failure
             */
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw cause;
            }

            /**
             * Does nothing because the failing test reader owns no resources.
             */
            @Override
            public void close() {
                // No resources are owned by this synthetic reader.
            }
        };
        UniEnableException exception = assertThrows(UniEnableException.class, () -> storage.load(source));
        assertTrue(exception.getMessage().contains("Simulated read failure"));
        assertSame(cause, exception.getCause());
    }

    /**
     * Checks that loading does not close a reader owned by the caller.
     */
    @Test
    public void load_callerOwnedReader_leavesReaderOpen() throws UniEnableException {
        boolean[] closed = {false};
        StringReader source = new StringReader("FACILITY|F01|AS1|Block") {
            /**
             * Records closure so the test can detect the loader closing a caller-owned reader.
             */
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

    /**
     * Loads a small in-memory dataset without changing bundled resources.
     *
     * @param text facility and feature records to test
     * @return the loaded records and warnings
     * @throws UniEnableException if reading the dataset fails
     */
    private LoadResult<Facility> load(String text) throws UniEnableException {
        return storage.load(new StringReader(text));
    }

    /**
     * Checks that exactly one warning reports the expected line and reason.
     *
     * @param result the load result to inspect
     * @param line the expected physical line number
     * @param reason the expected warning text
     */
    private void assertWarning(LoadResult<Facility> result, int line, String reason) {
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).startsWith("Line " + line + ":"));
        assertTrue(result.getWarnings().get(0).contains(reason));
    }
}
