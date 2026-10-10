package seedu.unienable.command.accessibility.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.ConnectionManager;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Connection;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.ConnectionStorage;
import seedu.unienable.storage.FacilityStorage;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Checks route output, endpoint errors and read-only command execution.
 */
class RouteCommandTest {
    private final ActivityList activities = new ActivityList();
    /**
     * Fails immediately if a route request reads or writes activity storage.
     */
    private final Storage noStorageAccess = new Storage() {
        @Override
        public ActivityList load() {
            throw new AssertionError("Route commands must not load activities");
        }

        @Override
        public void save(ActivityList ignored) {
            throw new AssertionError("Route commands must not save activities");
        }
    };

    @Test
    void execute_campusRouteShowsDistanceSegmentsAndDisclaimer() throws UniEnableException {
        var facilities = new FacilityStorage().load().getRecords();
        var connections = new ConnectionStorage(facilities).load().getRecords();
        var command = new RouteCommand(new ConnectionManager(connections), " as2 ", "cLb");

        CommandResult result = command.execute(activities, noStorageAccess);
        String message = result.getMessage();

        assertFalse(result.isExit());
        assertTrue(message.contains("AS2 -> AS1 -> AS6 -> CLB"));
        assertTrue(message.contains("Total distance: 290 metres"));
        assertTrue(message.contains("1. AS2 -> AS1: 130 m (Ramp, Unsheltered)"));
        assertTrue(message.contains("2. AS1 -> AS6: 90 m (Path, Sheltered)"));
        assertTrue(message.contains("3. AS6 -> CLB: 70 m (Path, Sheltered)"));
        assertTrue(message.contains("Note: Main covered walkway"));
        assertTrue(message.endsWith(AccessibilityDisclaimer.TEXT));
        assertTrue(activities.getActivities().isEmpty());
    }

    @Test
    void execute_reversedConnectionsDisplayActualTravelDirection() throws UniEnableException {
        var manager = new ConnectionManager(List.of(
                connection(1, "B", "A", 4, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null),
                connection(2, "C", "B", 6, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.NO, null, null)));
        var result = new RouteCommand(manager, "A", "C").execute(activities, noStorageAccess);

        assertTrue(result.getMessage().contains("A -> B -> C"));
        assertTrue(result.getMessage().contains("1. A -> B: 4 m (Path, Sheltered)"));
        assertTrue(result.getMessage().contains("2. B -> C: 6 m (Path, Unsheltered)"));
        assertTrue(result.getMessage().contains("Total distance: 10 metres"));
    }

    @Test
    void execute_inaccessibleShortcutIsExcluded() throws UniEnableException {
        var manager = new ConnectionManager(List.of(
                connection(1, "A", "B", 4, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null),
                connection(2, "B", "C", 6, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null),
                connection(3, "A", "C", 1, AccessibilityStatus.NO, TraversalType.PATH,
                        ShelterStatus.NO, null, null)));

        String message = new RouteCommand(manager, "A", "C").execute(activities, noStorageAccess).getMessage();

        assertTrue(message.contains("A -> B -> C"));
        assertTrue(message.contains("Total distance: 10 metres"));
        assertFalse(message.contains("A -> C: 1 m"));
    }

    @Test
    void execute_disconnectedDestinationExplainsUnconfirmedAccessibility() throws UniEnableException {
        var manager = new ConnectionManager(List.of(
                connection(1, "A", "B", 4, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null),
                connection(2, "B", "C", 6, AccessibilityStatus.UNKNOWN, TraversalType.PATH,
                        ShelterStatus.UNKNOWN, null, null)));

        CommandResult result = new RouteCommand(manager, "A", "C").execute(activities, noStorageAccess);

        assertFalse(result.isExit());
        assertTrue(result.getMessage().contains("No confirmed accessible route recorded from A to C."));
        assertTrue(result.getMessage().contains("Only connections marked YES"));
        assertTrue(result.getMessage().endsWith(AccessibilityDisclaimer.TEXT));
    }

    @Test
    void execute_sameStartAndEndHasZeroDistance() throws UniEnableException {
        var manager = new ConnectionManager(List.of(
                connection(1, "A", "B", 10, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null)));

        CommandResult result = new RouteCommand(manager, " a ", "A").execute(activities, noStorageAccess);

        assertTrue(result.getMessage().contains("Shortest recorded accessible route:\nA\n"));
        assertTrue(result.getMessage().contains("Total distance: 0 metres"));
        assertTrue(result.getMessage().contains("No travel needed"));
    }

    @Test
    void execute_unknownAndBlankEndpointsUseCheckedException() {
        var manager = new ConnectionManager(List.of(
                connection(1, "A", "B", 10, AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null)));

        assertTrue(assertThrows(UniEnableException.class,
                () -> new RouteCommand(manager, "F01", "A").execute(activities, noStorageAccess))
                .getMessage().contains("Unknown connection startpoint: F01"));
        assertTrue(assertThrows(UniEnableException.class,
                () -> new RouteCommand(manager, "A", " ").execute(activities, noStorageAccess))
                .getMessage().contains("Usage: route from/START to/END"));
        assertTrue(assertThrows(UniEnableException.class,
                () -> new RouteCommand(manager, null, "A").execute(activities, noStorageAccess))
                .getMessage().contains("Usage: route from/START to/END"));
    }

    @Test
    void execute_unknownStartAndDestinationHaveDifferentWarnings() throws UniEnableException {
        var facilities = new FacilityStorage().load().getRecords();
        var connections = new ConnectionStorage(facilities).load().getRecords();
        var parser = new Parser(null, new ConnectionManager(connections));

        var unknownStart = assertThrows(UniEnableException.class,
                () -> parser.parse("route from/abc to/as8").execute(activities, noStorageAccess));
        var unknownDestination = assertThrows(UniEnableException.class,
                () -> parser.parse("route from/as8 to/abc").execute(activities, noStorageAccess));

        assertTrue(unknownStart.getMessage().startsWith("[WARNING] Unknown connection startpoint: abc\n"));
        assertTrue(unknownDestination.getMessage().startsWith("[WARNING] Unknown connection endpoint: abc\n"));
    }

    @Test
    void execute_knownBarrierAndUnknownShelterAreVisible() throws UniEnableException {
        var manager = new ConnectionManager(List.of(
                connection(1, "A", "B", 12, AccessibilityStatus.YES, TraversalType.SHELTERED_RAMP,
                        ShelterStatus.UNKNOWN, "Narrow entrance", "Check before travel")));

        String message = new RouteCommand(manager, "A", "B").execute(activities, noStorageAccess).getMessage();

        assertTrue(message.contains("12 m (Sheltered ramp, Shelter unconfirmed)"));
        assertTrue(message.contains("Recorded barrier: Narrow entrance"));
        assertTrue(message.contains("Note: Check before travel"));
    }

    @Test
    void execute_repeatedRequestsProduceTheSameMessage() throws UniEnableException {
        var activity = new Activity("Lecture", LocalDate.of(2026, 10, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), DemandLevel.LOW, false);
        activities.addActivity(activity);
        var manager = new ConnectionManager(List.of(
                connection(1, "A", "B", 12, AccessibilityStatus.YES, TraversalType.RAMP,
                        ShelterStatus.NO, null, null)));
        var command = new RouteCommand(manager, "A", "B");

        String first = command.execute(activities, noStorageAccess).getMessage();
        String second = command.execute(activities, noStorageAccess).getMessage();

        assertEquals(first, second);
        assertEquals(List.of(activity), activities.getActivities());
    }

    /**
     * Creates reference links for command scenarios without relying on storage parsing.
     */
    private static Connection connection(int id, String from, String to, int distance,
            AccessibilityStatus accessibility, TraversalType type, ShelterStatus shelter,
            String barrier, String notes) {
        return new Connection(id, from, to, distance, accessibility, type, shelter, barrier, notes);
    }
}
