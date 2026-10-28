package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import seedu.unienable.command.activity.EditCommand;
import seedu.unienable.exception.ParseException;

//@@author Oliver-koh
/**
 * Checks edit syntax errors before any activity state can be changed.
 */
class EditCommandParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_mistypedPrefixInName_rejectsWholeEdit() {
        assertThrows(ParseException.class, () -> parser.parse("edit 1 n/Class demm/HIGH"));
        assertThrows(ParseException.class, () -> parser.parse("edit 1 n/Class imp/5 comn/3"));
    }

    @Test
    void parse_rejectsMissingIndexOrFields() {
        assertThrows(ParseException.class, () -> parser.parse("edit"));
        assertThrows(ParseException.class, () -> parser.parse("edit 1"));
    }

    @Test
    void parse_rejectsDuplicateAndUnknownFields() {
        assertThrows(ParseException.class, () -> parser.parse("edit 1 n/One n/Two"));
        assertThrows(ParseException.class, () -> parser.parse("edit 1 loc/NUS"));
    }

    @Test
    void parse_acceptsFieldsInAnyOrder() throws Exception {
        assertInstanceOf(EditCommand.class,
                parser.parse("edit 1 dem/HIGH e/15:00 n/Updated d/2099-01-03 s/14:00"));
        assertInstanceOf(EditCommand.class, parser.parse("edit\t1\tn/Updated"));
    }

    @TestFactory
    Stream<DynamicTest> parse_malformedFields_warnWithUsage() {
        String[] arguments = {
            "-1 n/Updated", "2147483648 n/Updated", "1 n/", "1 unexpected n/Updated",
            "1 d/2099-2-03", "1 d/2099-02-29", "1 s/9:00", "1 e/24:00",
            "1 dem/EXTREME", "1 imp/0", "1 urg/6", "1 com/5.0"
        };
        return Stream.of(arguments).map(input -> DynamicTest.dynamicTest("edit " + input, () -> {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse("edit " + input));
            assertTrue(exception.getMessage().startsWith("[WARNING]"), input);
            assertTrue(exception.getMessage().contains("Usage: edit INDEX"), input);
        }));
    }
}
//@@author
