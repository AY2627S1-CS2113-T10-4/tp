package seedu.unienable.logic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import seedu.unienable.model.Connection;

/**
 * Runs one Dijkstra search for DijkstraShortestPath.
 * Each search has its own mutable state so subsequent queries are independent.
 * Package access keeps this helper internal to the routing logic.
 */
final class DijkstraSolver {
    /**
     * Best known distance to each vertex; Long.MAX_VALUE denotes an unreachable vertex.
     */
    private final long[] dist;
    /**
     * Predecessor vertex indexes used to reconstruct the chosen route.
     */
    private final int[] previous;
    /**
     * Original links joining each vertex to its predecessor.
     */
    private final Connection[] previousConnection;
    private final Set<Integer> settled = new HashSet<>();
    private final PriorityQueue<Node> pq;
    private final int vertexCount;
    private List<List<Node>> adj;

    /**
     * Creates fresh search state for a graph containing the specified number of vertices.
     */
    DijkstraSolver(int vertexCount) {
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
    void dijkstra(List<List<Node>> adj, int src) {
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
     * Returns the calculated distance, or Long.MAX_VALUE if the vertex is unreachable.
     */
    long getDistance(int vertex) {
        return dist[vertex];
    }

    /**
     * Returns the predecessor vertex, or -1 when no predecessor was recorded.
     */
    int getPrevious(int vertex) {
        return previous[vertex];
    }

    /**
     * Returns the original link to the predecessor, or null when none was recorded.
     */
    Connection getPreviousConnection(int vertex) {
        return previousConnection[vertex];
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

    /**
     * Corresponds to the Node class in the provided priority-queue example.
     * A connection is attached when the node represents an adjacency-list edge.
     */
    record Node(int node, long cost, Connection connection) implements Comparator<Node> {
        private Node() {
            this(-1, 0, null);
        }

        private Node(int node, long cost) {
            this(node, cost, null);
        }

        @Override
        public int compare(Node first, Node second) {
            return Long.compare(first.cost, second.cost);
        }
    }
}
