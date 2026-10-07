package seedu.unienable.parser;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

/**
 * Entry point for future command dispatch; currently handles only bootstrap exit.
 */
public class Parser {
    /**
     * Recognizes bye. Feature owners will extend this dispatch for other commands.
     */
    public Command parse(String input) throws ParseException {
        if (!"bye".equalsIgnoreCase(input.trim())) {
            throw new ParseException("This command is not implemented in the baseline. Type bye to exit.");
        }
        // Keep bootstrap exit here until the team adds its command implementations.
        return new Command() {
            @Override
            public CommandResult execute(ActivityList activities, Storage storage) {
                return new CommandResult("Goodbye from UniEnable!", true);
            }
        };
    }
}
