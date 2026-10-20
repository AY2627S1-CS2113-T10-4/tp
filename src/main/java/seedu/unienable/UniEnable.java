package seedu.unienable;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Clock;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.Ui;

/**
 * Runs the UniEnable console until bye or end of input.
 */
public class UniEnable {
    /**
     * Starts the application and loads saved activity and facility data.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        run(System.in, System.out, Path.of("data", "activities.txt"));
    }

    /**
     * Runs a session with an injectable activity file for isolated integration tests.
     *
     * @param input console input
     * @param output console output
     * @param activityFile activity persistence file
     */
    static void run(InputStream input, PrintStream output, Path activityFile) {
        run(input, output, activityFile, Clock.systemDefaultZone());
    }

    /**
     * Runs a session with an injectable activity file and clock for deterministic integration tests.
     *
     * @param input console input
     * @param output console output
     * @param activityFile activity persistence file
     * @param clock time source used by add-command validation
     */
    static void run(InputStream input, PrintStream output, Path activityFile, Clock clock) {
        Ui ui = new Ui(input, output);
        Storage storage = new Storage(activityFile);
        ActivityList activities;
        try {
            activities = storage.load();
            for (String warning : storage.getWarnings()) {
                ui.showMessage("Activity data warning: " + warning);
            }
        } catch (UniEnableException exception) {
            ui.showMessage("Activity data unavailable: " + exception.getMessage());
            activities = new ActivityList();
        }
        Parser parser = ConsoleHelper.createParser(ui, clock);

        ui.showWelcome();
        while (ui.hasNextLine()) {
            if (ConsoleHelper.processNextCommand(ui, parser, activities, storage)) {
                break;
            }
        }
    }

}
