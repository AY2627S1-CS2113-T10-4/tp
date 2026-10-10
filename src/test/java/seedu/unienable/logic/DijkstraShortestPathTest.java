package seedu.unienable.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Connection;
import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;
import seedu.unienable.storage.ConnectionStorage;
import seedu.unienable.storage.FacilityStorage;

/**
 * Checks route building and the separate DijkstraSolver through DijkstraShortestPath.findRoute().
 * Compares all 81 campus pairs against JGraphT and checks that inaccessible shortcuts are excluded.
 */
class DijkstraShortestPathTest {
    @TestFactory
    List<DynamicTest> allCampusPairs_matchJGraphTPathAndDistance() throws UniEnableException {
        var facilities = new FacilityStorage().load().getRecords();
        var connections = new ConnectionStorage(facilities).load().getRecords();
        var shortestPath = new DijkstraShortestPath(connections);
        var graph = createReferenceGraph(connections);
        var names = graph.vertexSet().stream().sorted().toList();
        assertEquals(9, names.size());

        // The full library class name distinguishes it from our own DijkstraShortestPath.
        var reference = new org.jgrapht.alg.shortestpath.DijkstraShortestPath<>(graph);
        List<DynamicTest> tests = new ArrayList<>();
        for (String source : names) {
            for (String destination : names) {
                tests.add(DynamicTest.dynamicTest(source + " -> " + destination, () -> {
                    var expected = reference.getPath(source, destination);
                    var actual = shortestPath.findRoute(source, destination).orElseThrow();

                    assertEquals(expected.getVertexList(), actual.stops());
                    assertEquals(expected.getWeight(), (double) actual.distanceInMetres());
                }));
            }
        }
        return tests;
    }

    @Test
    void findRoute_avoidsInaccessibleShortcuts() {
        var connections = List.of(
                new Connection(1, "A", "B", 4,
                        AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null),
                new Connection(2, "B", "C", 6,
                        AccessibilityStatus.YES, TraversalType.PATH,
                        ShelterStatus.YES, null, null),
                new Connection(3, "A", "C", 1,
                        AccessibilityStatus.NO, TraversalType.PATH,
                        ShelterStatus.NO, null, null),
                new Connection(4, "A", "C", 2,
                        AccessibilityStatus.UNKNOWN, TraversalType.PATH,
                        ShelterStatus.UNKNOWN, null, null));

        var shortestPath = new DijkstraShortestPath(connections);
        var route = shortestPath.findRoute("A", "C").orElseThrow();

        assertEquals(List.of("A", "B", "C"), route.stops());
        assertEquals(10L, route.distanceInMetres());
    }

    /**
     * Builds an independent, bidirectional JGraphT graph using recorded distances as weights.
     * Only confirmed accessible connections are included as edges.
     */
    private static WeightedMultigraph<String, DefaultWeightedEdge> createReferenceGraph(List<Connection> connections) {
        var graph = new WeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (var connection : connections) {
            graph.addVertex(connection.getFrom());
            graph.addVertex(connection.getTo());
            if (connection.getAccessibility() == AccessibilityStatus.YES) {
                var edge = graph.addEdge(connection.getFrom(), connection.getTo());
                graph.setEdgeWeight(edge, connection.getDistanceInMetres());
            }
        }
        return graph;
    }
}
