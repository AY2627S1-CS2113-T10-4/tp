package seedu.unienable;

import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Connection;
import seedu.unienable.model.Facility;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.ConnectionStorage;
import seedu.unienable.storage.FacilityStorage;
import seedu.unienable.storage.LoadResult;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.Ui;

/**
 * Handles reference-data loading and individual command execution for the console.
 */
final class ConsoleHelper {
    /** Prevents instantiation of this stateless helper. */
    private ConsoleHelper() {
    }

    /**
     * Loads facilities before connections so connection endpoints can be validated.
     * A connection load failure keeps facility commands available; a facility load failure
     * reports the problem and keeps bootstrap commands available.
     *
     * @param ui user interface for dataset warnings and loading errors
     * @return parser with available reference data, or a fallback when facilities cannot load
     */
    static Parser createParser(Ui ui) {
        try {
            LoadResult<Facility> facilities = new FacilityStorage().load();
            for (String warning : facilities.getWarnings()) {
                ui.showMessage("Facility dataset warning: " + warning);
            }

            ConnectionManager connectionManager = null;
            try {
                LoadResult<Connection> connections = new ConnectionStorage(facilities.getRecords()).load();
                for (String warning : connections.getWarnings()) {
                    ui.showMessage("Connection dataset warning: " + warning);
                }
                connectionManager = new ConnectionManager(connections.getRecords());
            } catch (UniEnableException exception) {
                ui.showMessage("Connection dataset unavailable: " + exception.getMessage());
            }

            return new Parser(new FacilityManager(facilities.getRecords()), connectionManager);
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
    static boolean processNextCommand(Ui ui, Parser parser, ActivityList activities, Storage storage) {
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
