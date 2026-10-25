package seedu.unienable.parser;

import java.time.Clock;
import java.util.Locale;

import seedu.unienable.command.ByeCommand;
import seedu.unienable.command.Command;
import seedu.unienable.command.HelpCommand;
import seedu.unienable.command.activity.DemandTodayCommand;
import seedu.unienable.command.activity.FindCommand;
import seedu.unienable.command.activity.NextCommand;
import seedu.unienable.command.activity.OverdueCommand;
import seedu.unienable.command.activity.ViewCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.logic.FacilityManager;

/**
 * Recognizes add, edit, find, list, view, list demand, list priority, overdue, next,
 * delete, facility, route and bye commands.
 * Recognizes activity, facility, route, help and exit commands.
 */
public class Parser {
    /**
     * Shared prefix for warnings reported by this parser.
     */
    private static final String WARNING_MESSAGE = "[WARNING]";

    //@@author lukelouyu
    /**
     * Handles facility commands using the available reference data.
     */
    private final FacilityCommandParser facilityCommandParser;
    //@@author

    //@@author officialasyrafrahim
    /**
     * Handles list, list demand, list priority and delete commands.
     */
    private final ListDeleteCommandParser listDeleteCommandParser = new ListDeleteCommandParser();
    //@@author

    //@@author Oliver-koh
    /**
     * Handles mark and unmark commands.
     */
    private final MarkCommandParser markCommandParser = new MarkCommandParser();
    //@@author

    //@@author lukelouyu
    /**
     * Handles route commands using the available connection reference data.
     */
    private final RouteParser routeParser;
    //@@author

    /**
     * Time source shared by add/edit validation and date/time queries for deterministic tests.
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
     * Injecting a clock keeps add/edit validation and date/time queries deterministic in tests.
     *
     * @param clock time source used by add/edit validation and date/time queries.
     */
    public Parser(Clock clock) {
        this(null, null, clock);
    }

    /**
     * Creates a parser with the available reference data and the supplied clock.
     *
     * @param facilityManager manager used by facility commands, or null when unavailable.
     * @param connectionManager manager used by route commands, or null when unavailable.
     * @param clock time source used by add/edit validation and date/time queries.
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
        //@@author lukelouyu
        case "bye":
            if (words.length != 1) {
                throw new ParseException(WARNING_MESSAGE + " Unrecognized command. Type bye to exit.");
            }
            return new ByeCommand();
        case "facility":
            return facilityCommandParser.parseFacility(words);
        case "route":
            return routeParser.parseRoute(words);
        //@@author
        //@@author Atharva005
        case "add":
            return new AddCommandParser(clock).parse(trimmedInput.substring(words[0].length()).trim());
        //@@author
        //@@author Oliver-koh
        case "edit":
            return new EditCommandParser(clock).parse(trimmedInput.substring(words[0].length()).trim());
        case "find":
            String keyword = trimmedInput.substring(words[0].length()).trim();
            if (keyword.isBlank()) {
                throw new ParseException(WARNING_MESSAGE
                        + " find needs a non-blank activity name keyword.\nUsage: find KEYWORD");
            }
            return new FindCommand(keyword);
        //@@author
        //@@author officialasyrafrahim
        case "list":
            return listDeleteCommandParser.parseList(words);
        //@@author
        //@@author lukelouyu
        case "demand":
            if (words.length != 2 || !"today".equalsIgnoreCase(words[1])) {
                throw new ParseException(WARNING_MESSAGE
                        + " Expected demand today without extra arguments.\nUsage: demand today");
            }
            return new DemandTodayCommand(clock);
        //@@author
        //@@author officialasyrafrahim
        case "overdue":
            if (words.length != 1) {
                throw new ParseException(WARNING_MESSAGE
                        + " overdue does not take arguments.\nUsage: overdue");
            }
            return new OverdueCommand(clock);
        case "next":
            if (words.length != 1) {
                throw new ParseException(WARNING_MESSAGE
                        + " next does not take arguments.\nUsage: next");
            }
            return new NextCommand(clock);
        case "view":
            if (words.length != 2) {
                throw new ParseException(WARNING_MESSAGE + " Usage: view today|tomorrow|week");
            }
            return switch (words[1].toLowerCase(Locale.ROOT)) {
            case "today" -> new ViewCommand(ViewCommand.Range.TODAY, clock);
            case "tomorrow" -> new ViewCommand(ViewCommand.Range.TOMORROW, clock);
            case "week" -> new ViewCommand(ViewCommand.Range.WEEK, clock);
            default -> throw new ParseException(WARNING_MESSAGE + " Usage: view today|tomorrow|week");
            };
        case "delete":
            return listDeleteCommandParser.parseDelete(words);
        //@@author
        //@@author Oliver-koh
        case "mark":
            return markCommandParser.parseMark(words);
        case "unmark":
            return markCommandParser.parseUnmark(words);
        //@@author
        //@@author Atharva005
        case "help":
            if (words.length != 1) {
                throw new ParseException(
                        WARNING_MESSAGE + " help does not take arguments.");
            }
            return new HelpCommand();
        //@@author
        default:
            throw new ParseException(WARNING_MESSAGE + " Unrecognized command. Type bye to exit.");
        }
    }

}
