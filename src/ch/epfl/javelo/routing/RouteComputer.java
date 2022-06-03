package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

import java.util.*;

import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * A class computing the best itinerary from point A to point B.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class RouteComputer {

    /**
     * A float value of +∞.
     */
    private final static float POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
    /**
     * A float value of -∞ used to indicate that a node has already been visited.
     */
    private final static float COMPUTED_DISTANCE = Float.NEGATIVE_INFINITY;

    private final Graph graph;
    private final CostFunction costFunction;


    /**
     * The constructor of the RouteComputer class.
     *
     * @param graph        The buffer containing the data we need to go from point A to point B.
     * @param costFunction A function used to pick the best path out of several ones
     *                     while not using length as the unique criteria.
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * This method allows us to compute the best route between two given points.
     *
     * @param startNodeId The index of the node at which we start our bike session.
     * @param endNodeId   The index of the node at which we end our bike session.
     * @return The best route to go from startNodeId to endNodeId.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        checkArgument(startNodeId != endNodeId);

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];

        Arrays.fill(distance, POSITIVE_INFINITY);
        distance[startNodeId] = 0;

        PriorityQueue<WeightedNode> nodesPriorityQueue = new PriorityQueue<>();

        nodesPriorityQueue.add(new WeightedNode(startNodeId, distance[startNodeId]));

        do {
            int id = nodesPriorityQueue.remove().nodeId;
            if (id == endNodeId) break;
            int quantity = graph.nodeOutDegree(id);
            for (int i = 0; i < quantity; i++) {
                int nextNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(id, i));
                if (COMPUTED_DISTANCE != distance[nextNodeId] && COMPUTED_DISTANCE != distance[id]) {
                    float first_distance = (float) (distance[id] +
                            costFunction.costFactor(id, graph.nodeOutEdgeId(id, i))
                                    * graph.edgeLength(graph.nodeOutEdgeId(id, i)));
                    float second_distance = (float) (first_distance + graph.nodePoint(nextNodeId)
                            .distanceTo(graph.nodePoint(endNodeId)));

                    if (first_distance < distance[nextNodeId]) {
                        distance[nextNodeId] = first_distance;
                        predecessor[nextNodeId] = id;
                        nodesPriorityQueue.add(new WeightedNode(nextNodeId, second_distance));
                    }
                }
            }
            distance[id] = COMPUTED_DISTANCE;
        } while (!nodesPriorityQueue.isEmpty());

        return createRoute(predecessor, endNodeId);
    }

    /**
     * This method creates a route from a list of nodes.
     *
     * @param backNodes The list of nodes used for back propagation.
     * @param nodeId    The id of the last node.
     * @return A new Route.
     */
    private Route createRoute(int[] backNodes, int nodeId) {
        List<Edge> edges = new ArrayList<>();
        int id = nodeId;
        while (backNodes[id] != 0) {
            boolean found = false;
            int edgeID = 0;
            while (!found) {
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(backNodes[id], edgeID)) == id) {
                    edges.add(Edge.of(graph, graph.nodeOutEdgeId(backNodes[id], edgeID), backNodes[id], id));
                    found = true;
                }
                edgeID++;
            }
            id = backNodes[id];
        }
        Collections.reverse(edges);
        return edges.isEmpty() ? null : new SingleRoute(edges);
    }
}


