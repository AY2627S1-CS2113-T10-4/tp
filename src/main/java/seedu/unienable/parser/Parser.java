package seedu.unienable.parser;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.command.accessibility.facility.FacilityListCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Recognizes bootstrap commands and the read-only facility list command.
 */
public class Parser {
    private final FacilityManager facilityManager;

    /**
     * Creates a parser without loaded facility reference data.
     */
    public Parser() {
        this.facilityManager = null;
    }

    /**
     * Creates a parser with the available facility reference data.
     *
     * @param facilityManager manager used by facility commands
     */
    public Parser(FacilityManager facilityManager) {
        this.facilityManager = facilityManager;
    }

    /**
     * Recognizes bye and facility list, leaving other commands for later increments.
     *
     * @param input command entered by the user
     * @return the command to execute
     * @throws ParseException if the command is invalid or unavailable
     */
    public Command parse(String input) throws ParseException {
        String trimmedInput = input.trim();
        if ("bye".equalsIgnoreCase(trimmedInput)) {
            return new Command() {
                @Override
                public CommandResult execute(ActivityList activities, Storage storage) {
                    return new CommandResult("Goodbye from UniEnable!", true);
                }
            };
        }

        String[] words = trimmedInput.split("\\s+");
        if (words.length >= 2 && "facility".equalsIgnoreCase(words[0])
                && "list".equalsIgnoreCase(words[1])) {
            if (words.length != 2) {
                throw new ParseException("Usage: facility list");
            }
            if (facilityManager == null) {
                throw new ParseException("Facility reference data is unavailable.");
            }
            return new FacilityListCommand(facilityManager);
        }

        throw new ParseException("This command is not implemented in the baseline. Type bye to exit.");
    }
}
