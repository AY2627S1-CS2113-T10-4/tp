package seedu.unienable;

import seedu.unienable.model.ActivityList;
import seedu.unienable.parser.Parser;
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
        Parser parser = ConsoleHelper.createParser(ui);

        ui.showWelcome();
        while (ui.hasNextLine()) {
            if (ConsoleHelper.processNextCommand(ui, parser, activities, storage)) {
                break;
            }
        }
    }

}
