//@@author lukelouyu
package seedu.unienable.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Checks that console output keeps readable text and cannot carry terminal control sequences.
 */
class UiTest {
    private static final String BORDER = "_".repeat(60);

    @Test
    void showMessage_escapeSequence_isShownAsQuestionMark() {
        String output = show("Name\u001b[31mRed\u001b[0m");

        assertFalse(output.contains("\u001b"), output);
        assertEquals(framed("Name?[31mRed?[0m"), output);
    }

    @Test
    void showMessage_otherControlCharacters_areReplacedButLineBreaksKept() {
        String output = show("Tab\tBell\u0007Return\rDelete\u007fC1\u009b\nSecond line");

        assertEquals(framed("Tab?Bell?Return?Delete?C1?\nSecond line"), output);
    }

    @Test
    void showMessage_printableUnicodeAndAscii_areUnchanged() {
        String message = "复习 வணக்கம் Café 🎉\nO'Brien's lecture (Group 2)!";

        assertEquals(framed(message), show(message));
    }

    /**
     * Displays one message through a UI connected to in-memory streams.
     */
    private static String show(String message) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        new Ui(new ByteArrayInputStream(new byte[0]), output).showMessage(message);
        return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }

    /**
     * Returns the expected bordered layout for one message.
     */
    private static String framed(String message) {
        return BORDER + "\n" + message + "\n" + BORDER + "\n";
    }
}
//@@author
