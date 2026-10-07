package seedu.unienable;

import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.Ui;

/**
 * Runs the minimal UniEnable console bootstrap until bye or end of input.
 */
public class UniEnable {
    /**
     * Starts the application. Persistence is not connected in this baseline.
     */
    public static void main(String[] args) {
        Ui ui = new Ui(System.in, System.out);
        Parser parser = new Parser();
        ActivityList activities = new ActivityList();
        Storage storage = new Storage();
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
