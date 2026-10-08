package seedu.unienable;

import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.FacilityStorage;
import seedu.unienable.storage.LoadResult;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.Ui;

/**
 * Runs the UniEnable console until bye or end of input.
 */
public class UniEnable {
    /**
     * Starts the application and loads its read-only facility reference data.
     * Activity persistence is not yet connected in the baseline.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        Ui ui = new Ui(System.in, System.out);
        ActivityList activities = new ActivityList();
        Storage storage = new Storage();
        // TODO [Branch 3]: Load saved activities here and report recoverable warnings through Ui.
        Parser parser = createParser(ui);

        ui.showWelcome();
        while (ui.hasNextLine()) {
            if (processNextCommand(ui, parser, activities, storage)) {
                break;
            }
        }
    }

    /**
     * Loads facility reference data and creates the parser used by the console.
     * If loading fails, reports the problem and keeps bootstrap commands available.
     *
     * @param ui user interface for dataset warnings and loading errors
     * @return parser with facility data, or a fallback parser when loading fails
     */
    private static Parser createParser(Ui ui) {
        try {
            LoadResult<Facility> loaded = new FacilityStorage().load();
            Parser parser = new Parser(new FacilityManager(loaded.getRecords()));
            for (String warning : loaded.getWarnings()) {
                ui.showMessage("Facility dataset warning: " + warning);
            }
            return parser;
        } catch (UniEnableException exception) {
            ui.showMessage("Facility dataset unavailable: " + exception.getMessage());
            return new Parser();
        }
    }

    /**
     * Reads and executes one command, then displays its result or a recoverable warning.
     * A failed command keeps the console running so the user can enter another command.
     *
     * @param ui user interface for command input and output
     * @param parser parser that recognizes the user's command
     * @param activities shared activity collection
     * @param storage activity persistence service
     * @return true when the command requests exit; false after other commands or warnings
     */
    private static boolean processNextCommand(Ui ui, Parser parser, ActivityList activities, Storage storage) {
        try {
            CommandResult result = parser.parse(ui.readLine()).execute(activities, storage);
            // TODO [Branch 3]: Add automatic persistence at the agreed point after activity changes.
            // Coordinate with Branches 1 and 2 so failed or read-only commands do not save activity data.
            ui.showMessage(result.getMessage());
            return result.isExit();
        } catch (UniEnableException exception) {
            ui.showMessage(exception.getMessage());
            return false;
        }
    }
}
