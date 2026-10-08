package seedu.unienable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Tests facility feature searches using the real UniEnable console entry point.
 */
public class UniEnableFacilityFindTest {

    /**
     * Tests that omitting a status searches for facilities with confirmed YES lifts.
     */
    @Test
    public void main_findLiftDefaultStatus_displaysYesFacilities() {
        String output = runWithInput("facility find type/LIFT\nbye\n");

        assertTrue(output.contains("Facilities with LIFT (status: YES):"));
        assertTrue(output.contains("[F03] AS3"));
        assertTrue(output.contains("[F09] CLB"));
        assertFalse(output.contains("[F01] AS1"));
        assertFalse(output.contains("[F02] AS2"));
        assertTrue(output.contains(AccessibilityDisclaimer.TEXT));
    }

    /**
     * Tests that explicitly searching for NO lifts displays AS1 and AS2 only.
     */
    @Test
    public void main_findLiftNo_displaysTwoFacilities() {
        String output = runWithInput("facility find type/LIFT status/NO\nbye\n");

        assertTrue(output.contains("Facilities with LIFT (status: NO):\n[F01] AS1\n[F02] AS2"));
        assertFalse(output.contains("[F03] AS3"));
    }

    /**
     * Tests that a feature without records displays a clear no-match response.
     */
    @Test
    public void main_findAbsentFeature_displaysNoMatches() {
        String output = runWithInput("facility find type/AUTOMATIC_DOOR\nbye\n");

        assertTrue(output.contains("No matching facilities found."));
        assertTrue(output.contains(AccessibilityDisclaimer.TEXT));
    }

    /**
     * Tests that UNKNOWN status explains its meaning even when there are no matching records.
     */
    @Test
    public void main_findUnknownStatus_explainsUnconfirmed() {
        String output = runWithInput("FaCiLiTy FiNd type/lift status/unknown\nbye\n");

        assertTrue(output.contains("Facilities with LIFT (status: UNKNOWN):"));
        assertTrue(output.contains("No matching facilities found."));
        assertTrue(output.contains("UNKNOWN means the local dataset does not confirm the feature."));
    }

    /**
     * Tests that missing and extra arguments are rejected without ending the console.
     */
    @Test
    public void main_findWrongArgumentCount_displaysUsageAndContinues() {
        String output = runWithInput("facility find\n"
                + "facility find type/LIFT status/YES extra\nbye\n");

        String usage = "Usage: facility find type/FEATURE [status/YES|NO|UNKNOWN]";
        assertTrue(output.indexOf(usage) != output.lastIndexOf(usage));
        assertTrue(output.endsWith("Goodbye from UniEnable!\n"));
    }

    /**
     * Tests that unsupported feature types and statuses produce helpful errors.
     */
    @Test
    public void main_findInvalidTypeOrStatus_displaysSupportedValues() {
        String output = runWithInput("facility find type/ESCALATOR\n"
                + "facility find type/LIFT status/MAYBE\nbye\n");

        assertTrue(output.contains("[WARNING] Unknown facility feature type 'ESCALATOR'."));
        assertTrue(output.contains("Supported types: LIFT, RAMP"));
        assertTrue(output.contains("[WARNING] Invalid facility status 'MAYBE'."));
        assertTrue(output.contains("Supported statuses: YES, NO, UNKNOWN"));
    }

    /**
     * Tests that malformed prefixes do not accidentally match a valid search.
     */
    @Test
    public void main_findMalformedPrefixes_displaysUsage() {
        String output = runWithInput("facility find status/NO\n"
                + "facility find type/LIFT status/\nbye\n");

        assertTrue(output.contains("Usage: facility find type/FEATURE [status/YES|NO|UNKNOWN]"));
        assertFalse(output.contains("Facilities with LIFT"));
    }

    /**
     * Tests that new find commands do not break existing list and lookup commands.
     */
    @Test
    public void main_findThenListAndLookup_allCommandsWork() {
        String output = runWithInput("facility find type/REST_POINT\n"
                + "facility list\nfacility AS4\nbye\n");

        assertTrue(output.contains("Facilities with REST_POINT (status: YES):\n[F08] AS8"));
        assertTrue(output.contains("Known facilities in the local reference:"));
        assertTrue(output.contains("[F04] AS4 - Faculty of Arts and Social Sciences, Block 4"));
        assertTrue(output.endsWith("Goodbye from UniEnable!\n"));
    }

    /**
     * Tests that invalid names each warn once before a valid feature search succeeds.
     */
    @Test
    public void main_findInvalidNames_displaysWarningsAndContinues() {
        String output = runWithInput("facility find as99\nfacility find hello world\n"
                + "facility find type/REST_POINT\nbye\n");

        String warning = "[WARNING] Invalid facility find filters.\n"
                + "Usage: facility find type/FEATURE [status/YES|NO|UNKNOWN]\n"
                + "Example: facility find type/LIFT status/NO\n";
        String expected = "Welcome to UniEnable! Type bye to exit.\n"
                + warning + warning
                + "Facilities with REST_POINT (status: YES):\n[F08] AS8\n\n"
                + AccessibilityDisclaimer.TEXT + "\nGoodbye from UniEnable!\n";
        assertEquals(expected, output);
    }

    /**
     * Runs the console with the given inputs and captures its printed output.
     *
     * @param input commands to send to the application
     * @return console output with normalized line breaks
     */
    private String runWithInput(String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            UniEnable.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
