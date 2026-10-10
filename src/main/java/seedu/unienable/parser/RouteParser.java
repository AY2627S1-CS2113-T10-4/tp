package seedu.unienable.parser;

import java.util.Locale;

import seedu.unienable.command.accessibility.connection.RouteCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.ConnectionManager;

/**
 * Parses route commands and reports any missing source or destination.
 */
public class RouteParser {
    /** Shared prefix for warnings reported by this parser. */
    private static final String WARNING_MESSAGE = "[WARNING]";

    /** Shows the required prefixes when a route request is incomplete or malformed. */
    private static final String ROUTE_USAGE = "\nUsage: route from/START to/END";

    /** Loaded connection reference data used by route commands; null when unavailable. */
    private final ConnectionManager connectionManager;

    /**
     * Creates a parser with the available connection reference data.
     *
     * @param connectionManager manager used by route commands, or null when unavailable
     */
    public RouteParser(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Parses route prefixes and asks for any missing source or destination.
     *
     * @param words route command and its arguments
     * @return the route command with both endpoints supplied
     * @throws ParseException if arguments are missing, malformed or reference data is unavailable
     */
    public RouteCommand parseRoute(String[] words) throws ParseException {
        String from = "";
        String to = "";
        if (words.length > 3
                || (words.length == 3
                && (!words[1].toLowerCase(Locale.ROOT).startsWith("from/")
                || !words[2].toLowerCase(Locale.ROOT).startsWith("to/")))) {
            throw new ParseException(WARNING_MESSAGE + " Invalid route command." + ROUTE_USAGE);
        }

        for (int i = 1; i < words.length; i++) {
            String argument = words[i].toLowerCase(Locale.ROOT);
            if (argument.startsWith("from/")) {
                from = words[i].substring(5);
            } else if (argument.startsWith("to/")) {
                to = words[i].substring(3);
            } else {
                throw new ParseException(WARNING_MESSAGE + " Invalid route command." + ROUTE_USAGE);
            }
        }

        if (from.isEmpty() && to.isEmpty()) {
            throw new ParseException(WARNING_MESSAGE + " Please enter a source and destination." + ROUTE_USAGE);
        }
        if (from.isEmpty()) {
            throw new ParseException(WARNING_MESSAGE + " Please enter a source." + ROUTE_USAGE);
        }
        if (to.isEmpty()) {
            throw new ParseException(WARNING_MESSAGE + " Please enter a destination." + ROUTE_USAGE);
        }
        if (connectionManager == null) {
            throw new ParseException(WARNING_MESSAGE + " Connection reference data is unavailable.");
        }
        return new RouteCommand(connectionManager, from, to);
    }
}
