package seedu.unienable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import seedu.unienable.ui.Ui;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Exercises the console entry point and the user's full command sequence.
 */
class UniEnableTest {
    @TestFactory
    Stream<DynamicTest> commands() throws Exception {
        return cases("console").stream().map(row -> DynamicTest.dynamicTest(row[0], () -> {
            String output = console(row[1] + "\nbye\nignored\n");
            assertTrue(output.contains(row[2]), output);
            if (!row[3].isEmpty()) {
                assertFalse(output.contains(row[3]), output);
            }
            assertTrue(output.endsWith("Goodbye from UniEnable!\n" + "_".repeat(60) + "\n"), output);
        }));
    }

    @Test
    void fullSession_retainsFiveActivitiesAndRecoversAfterWarnings() throws Exception {
        String input = String.join("\n", cases("session").stream().map(row -> row[1]).toList());
        String output = console(input + "\n");
        for (String[] row : cases("session")) {
            assertTrue(output.contains(row[2]), row[0] + "\n" + output);
        }
        assertEquals(5, output.split("Added activity:", -1).length - 1);
        assertEquals(15, output.split("\\[WARNING\\]", -1).length - 1);
        assertTrue(output.contains("Activities in this session: 5"));
        assertTrue(output.contains(AccessibilityDisclaimer.TEXT));
    }

    @Test
    void emptyInput_preservesWelcomeFormatting() {
        String border = "_".repeat(60) + "\n";
        assertEquals(border + "Welcome to UniEnable! Type bye to exit.\n" + Ui.getLogo() + "\n" + border,
                console(""));
    }

    /**
     * Reads local UTF-8 cases; - means an omitted value and backslash-n means a newline.
     */
    private static List<String[]> cases(String name) throws IOException {
        try (var source = UniEnableTest.class.getResourceAsStream("/" + name + ".tsv")) {
            if (source == null) {
                throw new IOException("Missing regression cases: " + name);
            }
            return Arrays.stream(new String(source.readAllBytes(), StandardCharsets.UTF_8).split("\\R"))
                    .filter(line -> !line.isBlank() && !line.startsWith("#"))
                    .map(line -> line.replace("\\n", "\n").split("\t", -1))
                    .map(fields -> Arrays.stream(fields).map(value -> value.equals("-") ? "" : value)
                            .toArray(String[]::new)).toList();
        }
    }

    /**
     * Captures an isolated console session and restores the caller's streams after execution.
     */
    private static String console(String input) {
        var originalInput = System.in;
        var originalOutput = System.out;
        var output = new ByteArrayOutputStream();
        try (var captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            UniEnable.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
