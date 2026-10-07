package seedu.unienable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Verifies only the common console bootstrap, without exercising future features.
 */
class UniEnableTest {
    @Test
    public void main_unimplementedInputThenMixedCaseBye_exitsCleanly() {
        assertEquals("Welcome to UniEnable! Type bye to exit.\n"
                + "This command is not implemented in the baseline. Type bye to exit.\n"
                + "Goodbye from UniEnable!\n", runWithInput("unknown\nByE\nignored\n"));
    }

    @Test
    public void main_endOfInput_exitsCleanly() {
        assertEquals("Welcome to UniEnable! Type bye to exit.\n", runWithInput(""));
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
