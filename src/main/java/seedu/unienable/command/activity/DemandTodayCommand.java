package seedu.unienable.command.activity;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;

//@@author lukelouyu
/**
 * Shows the number of HIGH-demand activities scheduled today, including completed ones.
 */
public class DemandTodayCommand extends Command {
    // Owner: Luke
    private final Clock clock;

    /**
     * Creates a demand query using the computer's local time zone.
     */
    public DemandTodayCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a demand query with an injectable clock for deterministic date tests.
     *
     * @param clock time source and zone used to determine today's date.
     */
    public DemandTodayCommand(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Resolves today's date at execution time and returns its count without changing activities.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        LocalDate today = LocalDate.now(clock);
        long count = activities.countHighDemandOn(today);
        String message = "High-demand activities scheduled today (" + today + "): " + count;
        if (count == 0) {
            message = "[WARNING] " + message;
        }
        return new CommandResult(message, false);
    }
}
//@@author
