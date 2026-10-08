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
        Parser parser = new Parser();
        ActivityList activities = new ActivityList();
        Storage storage = new Storage();

        try {
            LoadResult<Facility> loaded = new FacilityStorage().load();
            parser = new Parser(new FacilityManager(loaded.getRecords()));
            for (String warning : loaded.getWarnings()) {
                ui.showMessage("Facility dataset warning: " + warning);
            }
        } catch (UniEnableException exception) {
            ui.showMessage("Facility dataset unavailable: " + exception.getMessage());
        }

        ui.showMessage("Welcome to UniEnable! Type bye to exit.");
        while (ui.hasNextLine()) {
            try {
                CommandResult result = parser.parse(ui.readLine()).execute(activities, storage);
                ui.showMessage(result.getMessage());
                if (result.isExit()) {
                    break;
                }
            } catch (UniEnableException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }
}
