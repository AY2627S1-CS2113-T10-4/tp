package seedu.unienable.command.activity;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

//@@author lukelouyu
import seedu.unienable.command.common.Command;
import seedu.unienable.command.common.CommandResult;
//@@author
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.activity.ActivityListFormatter;

//@@author officialasyrafrahim
/**
 * Lists incomplete activities whose start is strictly before the current local
 * time, in chronological order with labels for today, this week, and earlier.
 */
public class OverdueCommand extends Command {
    // Owner: Asyraf
    private static final Logger LOGGER = Logger.getLogger(OverdueCommand.class.getName());

    private final Clock clock;

    /**
     * Creates an overdue command using the system clock.
     */
    public OverdueCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates an overdue command with an injectable clock for deterministic tests.
     *
     * @param clock time source used for the overdue boundary.
     */
    public OverdueCommand(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Returns the overdue view without changing any user data.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Activity> canonicalView = activities.getCanonicalView();
        List<Activity> view = activities.getOverdueView(now);
        LOGGER.fine(() -> "Displayed " + view.size() + " overdue activities as of " + now + ".");
        String message = ActivityListFormatter.formatOverdueList(view, canonicalView, now);
        return new CommandResult(message, false);
    }
}
//@@author
