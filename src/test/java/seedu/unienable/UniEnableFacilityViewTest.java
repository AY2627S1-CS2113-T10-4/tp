package seedu.unienable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Tests the facility view command through the real UniEnable console entry point.
 */
public class UniEnableFacilityViewTest {

    /**
     * Tests that a facility view shows the bundled AS4 feature details and notes.
     */
    @Test
    public void main_facilityViewAS4_displaysAccessibilityDetails() {
        String output = runWithInput("facility view AS4\nbye\n");

        assertTrue(output.contains("[F04] AS4 - Faculty of Arts and Social Sciences, Block 4\n"));
        assertTrue(output.contains("STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"));
        assertTrue(output.contains("LIFT | YES | Lift serves floors 1-6\n"));
        assertTrue(output.contains("ACCESSIBLE_WASHROOM | YES | Floors 3-6\n"));
        assertTrue(output.contains(AccessibilityDisclaimer.TEXT));
        assertTrue(output.endsWith("Goodbye from UniEnable!\n"));
    }

    /**
     * Tests that a facility's stable ID is accepted without matching letter case.
     */
    @Test
    public void main_facilityViewLowerCaseId_displaysCorrectFacility() {
        String output = runWithInput("FACILITY VIEW f01\nbye\n");

        assertTrue(output.contains("[F01] AS1 - Faculty of Arts and Social Sciences, Block 1"));
        assertTrue(output.contains("LIFT | NO | Building has no lift; floors 4-5 are not accessible"));
    }

    /**
     * Tests that an unknown facility is rejected without terminating the console.
     */
    @Test
    public void main_facilityViewUnknown_displaysErrorAndContinues() {
        String output = runWithInput("facility view AS10\nfacility list\nbye\n");

        assertTrue(output.contains("Error: Unknown facility 'AS10'."));
        assertTrue(output.contains("Supported facilities:\nAS1, AS2, AS3, AS4, AS5, AS6, AS7, AS8, CLB"));
        assertTrue(output.contains("Known facilities in the local reference:"));
        assertTrue(output.endsWith("Goodbye from UniEnable!\n"));
    }

    /**
     * Tests that missing arguments produce usage information and the console continues.
     */
    @Test
    public void main_facilityViewMissingArgument_displaysUsage() {
        String output = runWithInput("facility view\nbye\n");

        assertEquals("Welcome to UniEnable! Type bye to exit.\n"
                + "Usage: facility view FACILITY\nExample: facility view AS4\n"
                + "Goodbye from UniEnable!\n", output);
    }

    /**
     * Tests that extra arguments are rejected without changing the command target.
     */
    @Test
    public void main_facilityViewExtraArgument_displaysUsage() {
        String output = runWithInput("facility view AS4 extra\nbye\n");

        assertTrue(output.contains("Usage: facility view FACILITY\nExample: facility view AS4"));
        assertTrue(output.endsWith("Goodbye from UniEnable!\n"));
    }

    /**
     * Runs the console with the provided input and captures its output.
     *
     * @param input lines to send to the application
     * @return normalized console output
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
