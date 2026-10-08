package seedu.unienable.command.accessibility.facility;

import java.util.Optional;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Shows the recorded accessibility features of a single facility.
 */
public class FacilityViewCommand extends Command {
    private static final String USAGE = "Usage: facility view FACILITY\nExample: facility view AS4";

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
     * Displays the facility details, or a helpful message if the facility is unknown.
     *
     * @param activities activity collection, not modified by this command
     * @param storage activity storage, not used by this command
     * @return non-exiting result containing facility details or an error
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) {
        Optional<Facility> matchingFacility = facilityManager.findFacility(identifier);
        if (matchingFacility.isEmpty()) {
            return new CommandResult(formatUnknownFacility(), false);
        }
        Facility facility = matchingFacility.get();
        StringBuilder output = new StringBuilder();
        output.append("[").append(facility.getId()).append("] ").append(facility.getName());
        if (facility.getDescription() != null && !facility.getDescription().isBlank()) {
            output.append(" - ").append(facility.getDescription());
        }
        output.append("\n\nAccessibility Features:");
        if (facility.getFeatures().isEmpty()) {
            output.append("\nNo accessibility features recorded.");
        }
        for (FacilityFeature feature : facility.getFeatures()) {
            output.append("\n").append(feature.getType()).append(" | ").append(feature.getStatus());
            if (feature.getNotes() != null && !feature.getNotes().isBlank()) {
                output.append(" | ").append(feature.getNotes());
            }
        }
        output.append("\n\n").append(AccessibilityDisclaimer.TEXT);
        return new CommandResult(output.toString(), false);
    }

    /**
     * Formats a lookup failure with valid facility names and an example command.
     *
     * @return an error message for the unknown facility
     */
    private String formatUnknownFacility() {
        StringBuilder output = new StringBuilder("Error: Unknown facility '");
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
