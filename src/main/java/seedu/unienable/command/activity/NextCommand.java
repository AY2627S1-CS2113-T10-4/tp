package seedu.unienable.command.activity;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.activity.ActivityListFormatter;

//@@author officialasyrafrahim
/**
 * Shows the earliest incomplete activity whose start is strictly after the
 * current local time.
 */
public class NextCommand extends Command {
    // Owner: Asyraf
    private static final Logger LOGGER = Logger.getLogger(NextCommand.class.getName());

    private final Clock clock;

    /**
     * Creates a next command using the system clock.
     */
    public NextCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a next command with an injectable clock for deterministic tests.
     *
     * @param clock time source used for the next boundary.
     */
    public NextCommand(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Returns the next incomplete activity without changing any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Activity> canonicalView = activities.getCanonicalView();
        Optional<Activity> next = activities.getNext(now);
        LOGGER.fine(() -> "Next activity as of " + now + ": "
                + next.map(Activity::getName).orElse("none") + ".");
        String message = ActivityListFormatter.formatNext(next, canonicalView);
        return new CommandResult(message, false);
    }
}
//@@author
