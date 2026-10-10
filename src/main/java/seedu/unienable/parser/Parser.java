package seedu.unienable.parser;

import java.util.Locale;

import seedu.unienable.command.ByeCommand;
import seedu.unienable.command.Command;
import seedu.unienable.command.accessibility.connection.RouteCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.logic.FacilityManager;

/**
 * Recognizes add, facility, route and bye commands.
 */
public class Parser {
    /**
     * Shared prefix for warnings reported by this parser.
     */
    private static final String WARNING_MESSAGE = "[WARNING]";

    /** Handles facility commands using the available reference data. */
    private final FacilityCommandParser facilityCommandParser;

    /**
     * Loaded connection reference data used by route commands; null when unavailable.
     */
    private final ConnectionManager connectionManager;

    /**
     * Creates a parser without loaded facility reference data.
     */
    public Parser() {
        this(null);
    }

    /**
     * Creates a parser with the available facility reference data.
     *
     * @param facilityManager manager used by facility commands
     */
    public Parser(FacilityManager facilityManager) {
        this(facilityManager, null);
    }

    /**
     * Creates a parser with the available facility and connection reference data.
     *
     * @param facilityManager manager used by facility commands, or null when unavailable
     * @param connectionManager manager used by route commands, or null when unavailable
     */
    public Parser(FacilityManager facilityManager, ConnectionManager connectionManager) {
        this.facilityCommandParser = new FacilityCommandParser(facilityManager);
        this.connectionManager = connectionManager;
    }

    /**
     * Dispatches supported user commands to their handlers.
     *
     * @param input command entered by the user
     * @return the command to execute
     * @throws ParseException if the command is invalid or unavailable
     */
    public Command parse(String input) throws ParseException {
        String trimmedInput = input.trim();
        String[] words = trimmedInput.split("\\s+");
        String commandWord = words[0].toLowerCase(Locale.ROOT);

        switch (commandWord) {
        case "bye":
            if (words.length != 1) {
                throw new ParseException(WARNING_MESSAGE + " Unrecognized command. Type bye to exit.");
            }
            return new ByeCommand();
        case "facility":
            return facilityCommandParser.parseFacility(words);
        case "route":
            if (words.length != 3
                    || !words[1].toLowerCase(Locale.ROOT).startsWith("from/")
                    || !words[2].toLowerCase(Locale.ROOT).startsWith("to/")
                    || words[1].length() <= 5
                    || words[2].length() <= 3) {
                throw new ParseException(WARNING_MESSAGE
                        + " Invalid route command.\nUsage: route from/START to/END");
            }
            if (connectionManager == null) {
                throw new ParseException(WARNING_MESSAGE
                        + " Connection reference data is unavailable.");
            }
            return new RouteCommand(connectionManager, words[1].substring(5), words[2].substring(3));
        case "add":
            return new AddCommandParser().parse(trimmedInput.substring(words[0].length()).trim());
        /*
        case "help":
            if (words.length != 1) {
                throw new ParseException(WARNING_MESSAGE + " help does not take arguments.");
            }
            return new HelpCommand();
        */
        // TODO [Branch 2]: Add list/delete dispatch here; keep parsing in ListDeleteCommandParser.
        // TODO [Branch 3]: Add mark/unmark dispatch here; keep parsing in MarkCommandParser.
        // One team integrator should coordinate these shared switch edits.
        default:
            throw new ParseException(WARNING_MESSAGE + " Unrecognized command. Type bye to exit.");
        }
    }

}
