package seedu.unienable.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Verifies that each console response has one border above and below its complete message.
 */
public class UiTest {
    private static final String LINE = "____________________________________________________________";

    /**
     * Tests that the exact ASCII logo appears below the greeting inside the welcome borders.
     */
    @Test
    public void showWelcome_logoBelowGreeting_displaysOneBorderedResponse() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), capturedOutput);

            ui.showWelcome();

            String logo = " _   _       _ _____                o      o   \n"
                    + "| | | |_ __ (_) ____|_ __   __ _   /|__   /|    ___\n"
                    + "| | | | '_ \\| |  _| | '_ \\ / _` | / '_ \\   |   / _ \\\n"
                    + "| |_| | | | | | |___| | | | (_| || |_) |   |   |  __/\n"
                    + " \\___/|_| |_|_|_____|_| |_|\\__,_| \\___/   / \\  \\___|";
            String expected = LINE + "\nWelcome to UniEnable! Type bye to exit.\n"
                    + logo + "\n" + LINE + "\n";
            assertEquals(expected, output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));
        }
    }

    /**
     * Tests that blank lines and all details stay inside one pair of response borders.
     */
    @Test
    public void showMessage_multilineResponse_preservesContentsInsideBorders() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), capturedOutput);

            ui.showMessage("AS4\n\nLIFT | YES | Floors 1-6");

            assertEquals(LINE + "\nAS4\n\nLIFT | YES | Floors 1-6\n" + LINE + "\n",
                    output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));
        }
    }

    /**
     * Tests that a warning and the following response each receive their own borders.
     */
    @Test
    public void showMessage_warningThenSuccess_bordersEachResponseSeparately() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), capturedOutput);

            ui.showMessage("[WARNING] Unknown facility 'AS99'.");
            ui.showMessage("Goodbye from UniEnable!");

            String expected = LINE + "\n[WARNING] Unknown facility 'AS99'.\n" + LINE + "\n"
                    + LINE + "\nGoodbye from UniEnable!\n" + LINE + "\n";
            assertEquals(expected, output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));
        }
    }
}
