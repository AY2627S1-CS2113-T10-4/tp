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
     * Tests that facility list shows every recorded field for all nine facilities in dataset order.
     */
    @Test
    public void main_facilityList_displaysAllBundledFacilities() {
        String actualOutput = runWithInput("facility list\nbye\n");

        String expectedOutput = "Welcome to UniEnable! Type bye to exit.\n"
                + "Known facilities in the local reference:\n\n"
                + "[F01] AS1 - Faculty of Arts and Social Sciences, Block 1\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | NO | Building has no lift; floors 4-5 are not accessible\n\n"
                + "[F02] AS2 - Faculty of Arts and Social Sciences, Block 2\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | NO | Building has no lift\n\n"
                + "[F03] AS3 - Faculty of Arts and Social Sciences, Block 3\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | YES | Lift serves the main floors\n\n"
                + "[F04] AS4 - Faculty of Arts and Social Sciences, Block 4\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | YES | Lift serves floors 1-6\n"
                + "ACCESSIBLE_WASHROOM | YES | Floors 3-6\n\n"
                + "[F05] AS5 - Faculty of Arts and Social Sciences, Block 5\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | YES | Floor 6 is not accessible\n"
                + "ACCESSIBLE_WASHROOM | YES | Near the lift lobby\n\n"
                + "[F06] AS6 - Computing Drive link building (School of Computing)\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | YES | Floors 1, B1, and 6 are not accessible\n"
                + "ACCESSIBLE_WASHROOM | YES | Upper floor, shared with SoC\n\n"
                + "[F07] AS7 - The Shaw Foundation Building\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Ground floor entrance\n"
                + "LIFT | YES | Basement (B1) is not accessible\n"
                + "ACCESSIBLE_WASHROOM | YES | Ground floor\n\n"
                + "[F08] AS8 - Faculty of Arts and Social Sciences, Block 8\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Kent Ridge Crescent entrance\n"
                + "LIFT | YES | Lift serves floors 1-4\n"
                + "ACCESSIBLE_WASHROOM | YES | Near the lift lobby\n"
                + "REST_POINT | YES | PitStop@FASS\n\n"
                + "[F09] CLB - Central Library\n\nAccessibility Features:\n"
                + "STEP_FREE_ENTRANCE | YES | Main entrance\n"
                + "LIFT | YES | Shared upper floors linked to School of Computing\n"
                + "ACCESSIBLE_WASHROOM | YES | 4th floor toilet requires staff card access\n\n"
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

        assertTrue(actualOutput.contains("[F01] AS1 - Faculty of Arts and Social Sciences, Block 1\n"));
        assertTrue(actualOutput.contains("[F09] CLB - Central Library\n"));
    }

    /**
     * Tests that facility list rejects an unexpected argument without exiting.
     */
    @Test
    public void main_facilityListExtraArgument_displaysWarningThenContinues() {
        String actualOutput = runWithInput("facility list as\nbye\n");

        assertEquals("Welcome to UniEnable! Type bye to exit.\n"
                + "[WARNING] facility list does not accept arguments.\nUsage: facility list\n"
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
