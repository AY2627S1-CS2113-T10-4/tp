package seedu.unienable.parser;

import java.time.Clock;
import java.util.Locale;

import seedu.unienable.command.ByeCommand;
import seedu.unienable.command.Command;
import seedu.unienable.command.HelpCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.command.HelpCommand;

/**
 * Recognizes add, list, list demand, delete, facility, route and bye commands.
 */
public class Parser {
    /**
     * Shared prefix for warnings reported by this parser.
     */
    private static final String WARNING_MESSAGE = "[WARNING]";

    /**
     * Handles facility commands using the available reference data.
     */
    private final FacilityCommandParser facilityCommandParser;

    /**
     * Handles list, list demand, and delete commands.
     */
    private final ListDeleteCommandParser listDeleteCommandParser = new ListDeleteCommandParser();

    /** Handles mark and unmark commands. */
    private final MarkCommandParser markCommandParser = new MarkCommandParser();

    /**
     * Handles route commands using the available connection reference data.
     */
    private final RouteParser routeParser;

    /**
     * Time source shared with the add command parser for deterministic validation.
     */
    private final Clock clock;

    /**
     * Creates a parser without loaded facility reference data.
     */
    public Parser() {
        this(null, null, Clock.systemDefaultZone());
    }

    /**
     * Creates a parser with the available facility reference data.
     *
     * @param facilityManager manager used by facility commands.
     */
    public Parser(FacilityManager facilityManager) {
        this(facilityManager, null, Clock.systemDefaultZone());
    }

    /**
     * Creates a parser with the available facility and connection reference data.
     *
     * @param facilityManager manager used by facility commands, or null when unavailable.
     * @param connectionManager manager used by route commands, or null when unavailable.
     */
    public Parser(FacilityManager facilityManager, ConnectionManager connectionManager) {
        this(facilityManager, connectionManager, Clock.systemDefaultZone());
    }

    /**
     * Creates a parser with the supplied clock and no reference data.
     * Injecting a clock keeps add-command validation deterministic in tests.
     *
     * @param clock time source used by add-command validation.
     */
    public Parser(Clock clock) {
        this(null, null, clock);
    }

    /**
     * Creates a parser with the available reference data and the supplied clock.
     *
     * @param facilityManager manager used by facility commands, or null when unavailable.
     * @param connectionManager manager used by route commands, or null when unavailable.
     * @param clock time source used by add-command validation.
     */
    public Parser(FacilityManager facilityManager, ConnectionManager connectionManager, Clock clock) {
        this.facilityCommandParser = new FacilityCommandParser(facilityManager);
        this.routeParser = new RouteParser(connectionManager);
        this.clock = clock;
    }

    /**
     * Dispatches supported user commands to their handlers.
     *
     * @param input command entered by the user.
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
            return routeParser.parseRoute(words);
        case "add":
            return new AddCommandParser(clock).parse(trimmedInput.substring(words[0].length()).trim());
        case "list":
            return listDeleteCommandParser.parseList(words);
        case "delete":
            return listDeleteCommandParser.parseDelete(words);
        case "mark":
            return markCommandParser.parseMark(words);
        case "unmark":
            return markCommandParser.parseUnmark(words);
        case "help":
            if (words.length != 1) {
                throw new ParseException(
                        WARNING_MESSAGE + " help does not take arguments.");
            }
            return new HelpCommand();
        default:
            throw new ParseException(WARNING_MESSAGE + " Unrecognized command. Type bye to exit.");
        }
    }

}
