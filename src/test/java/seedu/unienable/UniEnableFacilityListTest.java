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
 * Tests the facility list command through the actual console entry point.
 */
public class UniEnableFacilityListTest {

    /**
     * Tests that facility list shows all nine bundled facilities in dataset order.
     */
    @Test
    public void main_facilityList_displaysAllBundledFacilities() {
        String actualOutput = runWithInput("facility list\nbye\n");

        String expectedOutput = "Welcome to UniEnable! Type bye to exit.\n"
                + "Known facilities in the local reference:\n"
                + "[F01] AS1\n"
                + "[F02] AS2\n"
                + "[F03] AS3\n"
                + "[F04] AS4\n"
                + "[F05] AS5\n"
                + "[F06] AS6\n"
                + "[F07] AS7\n"
                + "[F08] AS8\n"
                + "[F09] CLB\n\n"
                + AccessibilityDisclaimer.TEXT + "\n"
                + "Goodbye from UniEnable!\n";
        assertEquals(expectedOutput, actualOutput);
    }

    /**
     * Tests that command words ignore letter case and repeated whitespace.
     */
    @Test
    public void main_facilityListMixedCase_displaysFacilities() {
        String actualOutput = runWithInput("FACILITY   LIST\nbye\n");

        assertTrue(actualOutput.contains("[F01] AS1\n"));
        assertTrue(actualOutput.contains("[F09] CLB\n"));
    }

    /**
     * Tests that facility list rejects an unexpected argument without exiting.
     */
    @Test
    public void main_facilityListExtraArgument_displaysUsageThenContinues() {
        String actualOutput = runWithInput("facility list AS1\nbye\n");

        assertEquals("Welcome to UniEnable! Type bye to exit.\n"
                + "Usage: facility list\n"
                + "Goodbye from UniEnable!\n", actualOutput);
    }

    /**
     * Runs the console with supplied input and captures its printed output.
     *
     * @param input lines to send to UniEnable
     * @return console output with normalized line endings
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
