package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.ParseException;

/**
 * Checks edit syntax errors before any activity state can be changed.
 */
class EditCommandParserTest {
    private final Parser parser = new Parser();

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
        parser.parse("edit 1 dem/HIGH e/15:00 n/Updated d/2099-01-03 s/14:00");
        parser.parse("edit\t1\tn/Updated");
    }
}
