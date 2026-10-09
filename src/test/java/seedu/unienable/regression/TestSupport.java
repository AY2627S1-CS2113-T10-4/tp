package seedu.unienable.regression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import seedu.unienable.UniEnable;

/**
 * Shared UTF-8 fixtures and console capture for independent regression cases.
 */
public final class TestSupport {
    private TestSupport() {
    }

    /**
     * Reads named tab-separated cases; literal backslash-n represents an input newline.
     */
    public static List<String[]> cases(String name) throws IOException {
        try (var source = TestSupport.class.getResourceAsStream("/regression/" + name + ".tsv")) {
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
     * Runs one isolated console session and restores the caller's streams even after failure.
     */
    public static String console(String input) {
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
