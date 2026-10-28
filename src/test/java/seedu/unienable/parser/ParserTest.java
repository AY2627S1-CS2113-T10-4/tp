//@@author lukelouyu
package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.ParseException;

/**
 * Checks command-language warnings without replacing command-specific input validation.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void nonAsciiCommandWords_showEnglishOnlyWarning() {
        for (String input : List.of("加入", "加入 extra", "வணக்கம்", "படி", "café", "🎉", "he李lp", "lİst")) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse(input), input);
            assertEquals("[WARNING] Please write the command in English (ASCII characters only).",
                    error.getMessage(), input);
        }
    }

    @Test
    void unknownAsciiAndBlankCommands_keepUnrecognizedWarning() {
        for (String input : List.of("bogus", "", " \t ")) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse(input), input);
            assertEquals("[WARNING] Unrecognized command. Type bye to exit.", error.getMessage(), input);
        }
    }

    @Test
    void nonAsciiActivityNames_keepNameSpecificWarning() {
        for (String input : List.of("add n/复习 d/2099-12-12 s/09:00 e/10:00", "edit 1 n/படி")) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse(input), input);
            assertEquals("[WARNING] Please write the activity name in English (ASCII characters only).",
                    error.getMessage(), input);
        }
    }
}
//@@author
