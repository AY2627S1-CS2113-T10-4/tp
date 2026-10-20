package seedu.unienable.command.activity;

import java.util.List;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.activity.ActivityListFormatter;

/**
 * Finds activities by a case-insensitive substring of their names.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a name-search command.
     *
     * @param keyword trimmed, non-blank name substring
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Searches without changing activity state or requesting persistence.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Activity> canonicalView = activities.getCanonicalView();
        List<Activity> matches = activities.findByName(keyword);
        String message = ActivityListFormatter.formatFindResults(keyword, matches, canonicalView);
        return new CommandResult(message, false);
    }
}
