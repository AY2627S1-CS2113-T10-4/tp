package seedu.unienable.command.accessibility.facility;

import java.util.Optional;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;
import seedu.unienable.ui.accessibility.FacilityDetailsFormatter;

/**
 * Shows the recorded accessibility features of a single facility.
 */
public class FacilityViewCommand extends Command {
    private static final String USAGE = "Usage: facility HUB\nExample: facility AS4";

    private final FacilityManager facilityManager;
    private final String identifier;

    /**
     * Creates a command to view a facility by its name or stable ID.
     *
     * @param facilityManager manager containing the local facility reference data
     * @param identifier facility ID or name entered by the user
     */
    public FacilityViewCommand(FacilityManager facilityManager, String identifier) {
        this.facilityManager = facilityManager;
        this.identifier = identifier;
    }

    /**
     * Displays facility details or reports an unknown facility through the checked error contract.
     *
     * @param activities activity collection, not modified by this command
     * @param storage activity storage, not used by this command
     * @return non-exiting result containing facility details
     * @throws UniEnableException if the facility is unknown
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        Optional<Facility> matchingFacility = facilityManager.findFacility(identifier);
        if (matchingFacility.isEmpty()) {
            throw new UniEnableException(formatUnknownFacility());
        }
        Facility facility = matchingFacility.get();
        String output = FacilityDetailsFormatter.format(facility)
                + "\n\n" + AccessibilityDisclaimer.TEXT;
        return new CommandResult(output, false);
    }

    /**
     * Formats a lookup failure with valid facility names and an example command.
     *
     * @return a warning message for the unknown facility
     */
    private String formatUnknownFacility() {
        StringBuilder output = new StringBuilder("[WARNING] Unknown facility '");
        output.append(identifier).append("'.\n\nSupported facilities:\n");
        if (facilityManager.getFacilities().isEmpty()) {
            output.append("None");
        } else {
            for (int i = 0; i < facilityManager.getFacilities().size(); i++) {
                if (i > 0) {
                    output.append(", ");
                }
                output.append(facilityManager.getFacilities().get(i).getName());
            }
        }
        output.append("\n\n").append(USAGE);
        return output.toString();
    }
}
