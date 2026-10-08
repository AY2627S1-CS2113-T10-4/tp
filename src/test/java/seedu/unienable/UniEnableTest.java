package seedu.unienable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.unienable.ui.Ui;

/**
 * Verifies only the common console bootstrap, without exercising future features.
 */
class UniEnableTest {
    /**
     * Tests that a misspelled command warns and still allows bye to end the console.
     */
    @Test
    public void main_misspelledCommandThenMixedCaseBye_warnsAndExitsCleanly() {
        assertEquals(expectedWelcome()
                + expectedResponse("[WARNING] Unrecognized command. Type bye to exit.\n")
                + expectedResponse("Goodbye from UniEnable!\n"), runWithInput("faciliy\nByE\nignored\n"));
    }

    /**
     * Tests that bye with an extra argument warns instead of exiting the console early.
     */
    @Test
    public void main_byeWithExtraArgument_warnsAndContinues() {
        String output = runWithInput("bye extra\nByE\nignored\n");

        String expected = expectedWelcome()
                + expectedResponse("[WARNING] Unrecognized command. Type bye to exit.\n")
                + expectedResponse("Goodbye from UniEnable!\n");
        assertEquals(expected, output);
    }

    @Test
    public void main_endOfInput_exitsCleanly() {
        assertEquals(expectedWelcome(), runWithInput(""));
    }

    /**
     * Builds the expected welcome response with the logo below the greeting.
     *
     * @return complete bordered welcome response
     */
    private String expectedWelcome() {
        return expectedResponse("Welcome to UniEnable! Type bye to exit.\n" + Ui.getLogo() + "\n");
    }

    /**
     * Builds the expected console response with the example's 60-underscore borders.
     *
     * @param message expected message including its final line break
     * @return complete bordered response
     */
    private String expectedResponse(String message) {
        String line = "____________________________________________________________";
        return line + "\n" + message + line + "\n";
    }

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
