package seedu.unienable.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import seedu.unienable.model.Connection;
import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Finds the shortest recorded accessible route between campus facilities.
 * Connections are bidirectional; only links marked YES are traversable.
 * The route minimizes recorded distance, not time or number of connections.
 */
public final class DijkstraShortestPath {
    private final Map<String, Integer> indexByName = new LinkedHashMap<>();
    private final List<String> names = new ArrayList<>();
    private final List<List<Node>> adjacency;

    /**
     * Builds an adjacency list from the existing connection model.
     * Endpoints from inaccessible or unconfirmed links remain known vertices.
     *
     * @param connections reference data supplied by ConnectionManager/ConnectionStorage
     */
    public DijkstraShortestPath(List<Connection> connections) {
        Objects.requireNonNull(connections, "connections");
        for (Connection connection : connections) {
            Objects.requireNonNull(connection, "connection");
            if (connection.getDistanceInMetres() <= 0) {
                throw new IllegalArgumentException("Connection distance must be positive");
            }
            addEndpoint(connection.getFrom());
            addEndpoint(connection.getTo());
        }

        adjacency = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            adjacency.add(new ArrayList<>());
        }
        for (Connection connection : connections) {
            if (connection.getAccessibility() != AccessibilityStatus.YES) {
                continue;
            }
            int from = indexByName.get(key(connection.getFrom()));
            int to = indexByName.get(key(connection.getTo()));
            int distance = connection.getDistanceInMetres();
            adjacency.get(from).add(new Node(to, distance, connection));
            adjacency.get(to).add(new Node(from, distance, connection));
        }
    }

    /**
     * Finds a shortest distance route, ignoring case and surrounding spaces.
     *
     * @return the route, or empty if the facilities cannot be connected with YES links
     * @throws IllegalArgumentException if either endpoint is unknown or blank
     */
    public Optional<Route> findRoute(String from, String to) {
        int source = lookup(from);
        int destination = lookup(to);
        DijkstraSolver solver = new DijkstraSolver(names.size());
        solver.dijkstra(adjacency, source);
        if (solver.dist[destination] == Long.MAX_VALUE) {
            return Optional.empty();
        }

        List<String> stops = new ArrayList<>();
        List<Connection> segments = new ArrayList<>();
        int current = destination;
        stops.add(names.get(current));
        while (current != source) {
            segments.add(solver.previousConnection[current]);
            current = solver.previous[current];
            stops.add(names.get(current));
        }
        Collections.reverse(stops);
        Collections.reverse(segments);
        return Optional.of(new Route(stops, segments, solver.dist[destination]));
    }

    /**
     * Assigns one vertex index to each normalized facility name.
     */
    private void addEndpoint(String name) {
        String normalized = key(name);
        if (!indexByName.containsKey(normalized)) {
            indexByName.put(normalized, names.size());
            names.add(name.strip());
        }
    }

    /**
     * Resolves an endpoint or rejects it rather than treating a typo as a disconnected route.
     */
    private int lookup(String name) {
        Integer index = indexByName.get(key(name));
        if (index == null) {
            throw new IllegalArgumentException("Unknown connection endpoint: " + name);
        }
        return index;
    }

    /**
     * Makes endpoint lookup independent of case, whitespace and the system locale.
     */
    private static String key(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Connection endpoint must not be blank");
        }
        return name.strip().toUpperCase(Locale.ROOT);
    }

    /**
     * A path with its ordered facility names, original connections and total metres.
     * Each segment joins the corresponding adjacent stops in either stored direction.
     * A long total accommodates routes whose combined distance exceeds the int range.
     */
    public record Route(List<String> stops, List<Connection> segments, long distanceInMetres) {
        /**
         * Copies the path lists so callers cannot alter a completed route.
         */
        public Route {
            stops = List.copyOf(stops);
            segments = List.copyOf(segments);
        }
    }

    /**
     * Corresponds to the Node class in the provided priority-queue example.
     * A connection is attached when the node represents an adjacency-list edge.
     */
    private static final class Node implements Comparator<Node> {
        private final int node;
        private final long cost;
        private final Connection connection;

        private Node() {
            this(-1, 0, null);
        }

        private Node(int node, long cost) {
            this(node, cost, null);
        }

        private Node(int node, long cost, Connection connection) {
            this.node = node;
            this.cost = cost;
            this.connection = connection;
        }

        @Override
        public int compare(Node first, Node second) {
            return Long.compare(first.cost, second.cost);
        }
    }

    /**
     * Each search has its own mutable state so subsequent queries are independent.
     */
    private static final class DijkstraSolver {
        /**
         * Best known distance to each vertex; Long.MAX_VALUE denotes an unreachable vertex.
         */
        private final long[] dist;
        /**
         * Predecessor vertices and original links used to reconstruct the chosen route.
         */
        private final int[] previous;
        private final Connection[] previousConnection;
        private final Set<Integer> settled = new HashSet<>();
        private final PriorityQueue<Node> pq;
        private final int vertexCount;
        private List<List<Node>> adj;

        private DijkstraSolver(int vertexCount) {
            this.vertexCount = vertexCount;
            dist = new long[vertexCount];
            previous = new int[vertexCount];
            previousConnection = new Connection[vertexCount];
            Arrays.fill(previous, -1);
            pq = new PriorityQueue<>(Math.max(1, vertexCount), new Node());
        }

        /**
         * Runs the supplied Dijkstra algorithm with predecessor tracking added.
         */
        private void dijkstra(List<List<Node>> adj, int src) {
            this.adj = adj;
            Arrays.fill(dist, Long.MAX_VALUE);
            pq.add(new Node(src, 0));
            dist[src] = 0;

            while (settled.size() != vertexCount) {
                if (pq.isEmpty()) {
                    return;
                }
                int u = pq.remove().node;
                if (settled.contains(u)) {
                    continue;
                }
                settled.add(u);
                eNeighbours(u);
            }
        }

        /**
         * Relaxes every not-yet-settled neighbour of the current vertex.
         */
        private void eNeighbours(int u) {
            for (Node v : adj.get(u)) {
                if (settled.contains(v.node)) {
                    continue;
                }
                // Positive int edge weights and int vertex indexes keep simple path totals within long.
                long candidate = dist[u] + v.cost;
                if (candidate < dist[v.node]) {
                    dist[v.node] = candidate;
                    previous[v.node] = u;
                    previousConnection[v.node] = v.connection;
                    pq.add(new Node(v.node, dist[v.node]));
                }
            }
        }
    }
}
