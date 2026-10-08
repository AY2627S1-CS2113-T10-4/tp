package seedu.unienable.command.accessibility.facility;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Lists the stable IDs and names of all facilities in the local reference dataset.
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
        StringBuilder output = new StringBuilder("Known facilities in the local reference:");
        for (Facility facility : facilityManager.getFacilities()) {
            output.append("\n[").append(facility.getId()).append("] ").append(facility.getName());
        }
        output.append("\n\n").append(AccessibilityDisclaimer.TEXT);
        return new CommandResult(output.toString(), false);
    }
}
