package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.accessibility.connection.RouteCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.ConnectionManager;

/**
 * Checks prefixed route syntax independently of route calculation and output tests.
 */
class RouteParserTest {
    private final Parser parser = new Parser(null, new ConnectionManager(List.of()));

    @Test
    void validRouteCreatesCommand() throws ParseException {
        assertInstanceOf(RouteCommand.class, parser.parse("route from/as2 to/clb"));
    }

    @Test
    void missingDestinationRejected() {
        assertThrows(ParseException.class, () -> parser.parse("route from/as2"));
    }

    @Test
    void invalidPrefixRejected() {
        assertThrows(ParseException.class, () -> parser.parse("route AS2 CLB"));
    }
}
