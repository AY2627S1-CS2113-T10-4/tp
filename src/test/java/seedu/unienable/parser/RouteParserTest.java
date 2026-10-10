package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.accessibility.connection.RouteCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.ConnectionManager;

/**
 * Checks route syntax, missing-input warnings and delegation from the main parser.
 */
class RouteParserTest {
    private final ConnectionManager connectionManager = new ConnectionManager(List.of());
    private final RouteParser parser = new RouteParser(connectionManager);

    @Test
    void validRouteCreatesCommand() throws ParseException {
        assertInstanceOf(RouteCommand.class, parser.parseRoute(new String[]{"route", "from/as2", "to/clb"}));
    }

    @Test
    void mainParserDelegatesRouteRequests() throws ParseException {
        var mainParser = new Parser(null, connectionManager);
        assertInstanceOf(RouteCommand.class, mainParser.parse("route from/as2 to/clb"));
        var error = assertThrows(ParseException.class, () -> mainParser.parse("route from/as8 to/"));
        assertEquals("[WARNING] Please enter a destination.\nUsage: route from/START to/END", error.getMessage());
    }

    @Test
    void missingEndpointsRequestRequiredInformation() {
        String[][] scenarios = {
            {"route", "Please enter a source and destination."},
            {"route from/", "Please enter a source and destination."},
            {"route to/", "Please enter a source and destination."},
            {"route from/ to/", "Please enter a source and destination."},
            {"route from/as8", "Please enter a destination."},
            {"route from/as8 to/", "Please enter a destination."},
            {"route to/clb", "Please enter a source."},
            {"route from/ to/clb", "Please enter a source."}
        };
        for (String[] scenario : scenarios) {
            var error = assertThrows(ParseException.class,
                    () -> parser.parseRoute(scenario[0].split("\\s+")), scenario[0]);
            assertEquals("[WARNING] " + scenario[1] + "\nUsage: route from/START to/END",
                    error.getMessage(), scenario[0]);
        }
    }

    @Test
    void malformedArgumentsRejected() {
        for (String input : List.of("route snxjsnd", "route AS2 CLB", "route from/as8 to/clb extra")) {
            var error = assertThrows(ParseException.class,
                    () -> parser.parseRoute(input.split("\\s+")), input);
            assertEquals("[WARNING] Invalid route command.\nUsage: route from/START to/END",
                    error.getMessage(), input);
        }
    }
}
