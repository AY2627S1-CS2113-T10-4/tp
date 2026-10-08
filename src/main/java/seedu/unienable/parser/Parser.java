package seedu.unienable.parser;

import java.util.Locale;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.command.accessibility.facility.FacilityFindCommand;
import seedu.unienable.command.accessibility.facility.FacilityListCommand;
import seedu.unienable.command.accessibility.facility.FacilityViewCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.storage.Storage;

/**
 * Recognizes bootstrap commands and read-only facility list, view, and find commands.
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
     * Recognizes bye, facility list, facility view, and facility find.
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

        if (words.length >= 2 && "facility".equalsIgnoreCase(words[0])
                && "view".equalsIgnoreCase(words[1])) {
            if (words.length != 3) {
                throw new ParseException("Usage: facility view FACILITY\nExample: facility view AS4");
            }
            if (facilityManager == null) {
                throw new ParseException("Facility reference data is unavailable.");
            }
            return new FacilityViewCommand(facilityManager, words[2]);
        }

        if (words.length >= 2 && "facility".equalsIgnoreCase(words[0])
                && "find".equalsIgnoreCase(words[1])) {
            return parseFacilityFind(words);
        }

        throw new ParseException("This command is not implemented in the baseline. Type bye to exit.");
    }

    /**
     * Parses the required feature type and optional accessibility status.
     *
     * @param words words in the command line
     * @return the facility feature search command
     * @throws ParseException if the feature or status is invalid
     */
    private Command parseFacilityFind(String[] words) throws ParseException {
        String usage = "Usage: facility find type/FEATURE [status/YES|NO|UNKNOWN]"
                + "\nExample: facility find type/LIFT status/NO";
        if (words.length < 3 || words.length > 4
                || !words[2].regionMatches(true, 0, "type/", 0, 5)
                || words[2].length() == 5) {
            throw new ParseException(usage);
        }

        String featureName = words[2].substring(5);
        FacilityFeature.Type type;
        try {
            type = FacilityFeature.Type.valueOf(featureName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ParseException("Unknown facility feature type '" + featureName + "'.\n"
                    + "Supported types: LIFT, RAMP, SHELTERED_RAMP, ACCESSIBLE_WASHROOM, "
                    + "STEP_FREE_ENTRANCE, REST_POINT, AUTOMATIC_DOOR, OTHER\n" + usage);
        }

        AccessibilityStatus status = AccessibilityStatus.YES;
        if (words.length == 4) {
            if (!words[3].regionMatches(true, 0, "status/", 0, 7)
                    || words[3].length() == 7) {
                throw new ParseException(usage);
            }
            String statusName = words[3].substring(7);
            try {
                status = AccessibilityStatus.valueOf(statusName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new ParseException("Invalid facility status '" + statusName + "'.\n"
                        + "Supported statuses: YES, NO, UNKNOWN\n" + usage);
            }
        }

        if (facilityManager == null) {
            throw new ParseException("Facility reference data is unavailable.");
        }
        return new FacilityFindCommand(facilityManager, type, status);
    }
}
