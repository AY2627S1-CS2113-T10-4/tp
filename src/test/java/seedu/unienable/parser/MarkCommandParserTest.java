package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import seedu.unienable.command.activity.MarkCommand;
import seedu.unienable.command.activity.UnmarkCommand;
import seedu.unienable.exception.ParseException;

/**
 * Tests syntax validation and top-level routing for mark commands.
 */
class MarkCommandParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_acceptsCaseInsensitiveMarkAndUnmark() throws Exception {
        assertInstanceOf(MarkCommand.class, parser.parse("MARK 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 2"));
    }

    @Test
    void parse_rejectsMissingNonNumericAndExtraArguments() {
        assertThrows(ParseException.class, () -> parser.parse("mark"));
        assertThrows(ParseException.class, () -> parser.parse("mark abc"));
        assertThrows(ParseException.class, () -> parser.parse("mark 1 extra"));
        assertThrows(ParseException.class, () -> parser.parse("unmark -1"));
    }
}
