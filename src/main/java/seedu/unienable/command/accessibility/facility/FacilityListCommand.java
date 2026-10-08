package seedu.unienable.command.accessibility.facility;

import java.util.List;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.FacilityOutputFormatter;

/**
 * Lists all recorded details of facilities in the local reference dataset.
 */
public class FacilityListCommand extends Command {
    private final FacilityManager facilityManager;

    /**
     * Creates a command that reads facilities from the supplied manager.
     *
     * @param facilityManager manager containing the loaded facility data
     */
    public FacilityListCommand(FacilityManager facilityManager) {
        this.facilityManager = facilityManager;
    }

    /**
     * Returns the facility list and shared accessibility disclaimer without changing user data.
     *
     * @param activities shared activity collection, not modified by this command
     * @param storage activity persistence service, not used by this command
     * @return non-exiting result containing the facility list
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        List<Facility> facilities = facilityManager.getFacilities();
        String message = FacilityOutputFormatter.formatList(facilities);
        return new CommandResult(message, false);
    }
}
