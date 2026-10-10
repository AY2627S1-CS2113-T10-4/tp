package seedu.unienable.command.accessibility.connection;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.logic.DijkstraShortestPath.Route;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Connection;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Displays the shortest recorded route using connections explicitly marked accessible.
 * The command only reads connection reference data; it never changes activities or storage.
 */
public final class RouteCommand extends Command {
    private static final String USAGE = "Usage: route from/START to/END\n"
            + "Example: route from/as2 to/clb";

    private final ConnectionManager connectionManager;
    private final String from;
    private final String to;

    /**
     * Creates a route request using facility names, not facility IDs.
     * Validation of the endpoints is delegated to ConnectionManager.
     *
     * @param connectionManager manager containing the recorded campus connections.
     * @param from starting facility name.
     * @param to destination facility name.
     */
    public RouteCommand(ConnectionManager connectionManager, String from, String to) {
        this.connectionManager = Objects.requireNonNull(connectionManager, "connectionManager");
        this.from = from;
        this.to = to;
    }

    /**
     * Finds and formats a shortest-distance route without modifying activity data.
     *
     * @param activities existing activities, not modified.
     * @param storage activity persistence service, not used.
     * @return a non-exiting result containing route details or a no-route explanation
     * @throws UniEnableException if an endpoint is blank or unknown to the connection network
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        final Optional<Route> route;
        try {
            route = connectionManager.findRoute(from, to);
        } catch (IllegalArgumentException exception) {
            throw new UniEnableException("[WARNING] " + exception.getMessage() + "\n" + USAGE);
        }

        if (route.isEmpty()) {
            String message = "No confirmed accessible route recorded from " + from.strip() + " to " + to.strip()
                    + ".\nOnly connections marked YES for accessibility are used.";
            return new CommandResult(appendDisclaimer(message), false);
        }

        return new CommandResult(appendDisclaimer(formatRoute(route.orElseThrow())), false);
    }

    /**
     * Uses ordered route stops rather than the stored endpoint direction of a connection.
     */
    private static String formatRoute(Route route) {
        StringBuilder output = new StringBuilder("Shortest recorded accessible route:\n");
        output.append(String.join(" -> ", route.stops()));
        output.append("\nTotal distance: ").append(route.distanceInMetres()).append(" metres");
        output.append("\n\nConnections:");

        List<Connection> segments = route.segments();
        if (segments.isEmpty()) {
            output.append("\nNo travel needed (start and destination are the same).");
        }
        for (int i = 0; i < segments.size(); i++) {
            Connection connection = segments.get(i);
            output.append("\n").append(i + 1).append(". ")
                    .append(route.stops().get(i)).append(" -> ").append(route.stops().get(i + 1))
                    .append(": ").append(connection.getDistanceInMetres()).append(" m (")
                    .append(formatTraversalType(connection.getType())).append(", ")
                    .append(formatShelterStatus(connection.getShelter())).append(")");

            if (hasText(connection.getKnownBarrier())) {
                output.append("\n   Recorded barrier: ").append(connection.getKnownBarrier().strip());
            }
            if (hasText(connection.getNotes())) {
                output.append("\n   Note: ").append(connection.getNotes().strip());
            }
        }
        return output.toString();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Converts an enum name such as SHELTERED_RAMP into a readable traversal label.
     */
    private static String formatTraversalType(TraversalType type) {
        String label = type.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }

    /**
     * Describes recorded shelter without implying that UNKNOWN confirms shelter.
     */
    private static String formatShelterStatus(ShelterStatus status) {
        return switch (status) {
        case YES -> "Sheltered";
        case NO -> "Unsheltered";
        case UNKNOWN -> "Shelter unconfirmed";
        };
    }

    /**
     * Appends the shared accessibility disclaimer after the route details.
     */
    private static String appendDisclaimer(String message) {
        return message + "\n\n" + AccessibilityDisclaimer.TEXT;
    }
}
