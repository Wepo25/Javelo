package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

/**
 * A class computing the best itinerary from point A to point B.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;


    /**
     * The constructor of the RouteComputer class.
     *
     * @param graph - Graph : The buffer containing the data we need to go from point A to point B.
     * @param costFunction - CostFunction : A function used to pick the best path out of several ones
     *                     while not using length as the unique criteria.
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * This method allows us to compute the best route between two given points.
     *
     * @param startNodeId - int : The index of the node at which we start our bike session.
     * @param endNodeId - int : The index of the node at which we end our bike session.
     * @return - Route : The best route to go from startNodeId to endNodeId.
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        Preconditions.checkArgument(startNodeId != endNodeId);

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }

        }

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;

        PriorityQueue<WeightedNode> p = new PriorityQueue<>();
        List<Integer> nodePath = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        p.add(new WeightedNode(startNodeId, distance[startNodeId]));

        do {
            int id = p.remove().nodeId;
            if (id == endNodeId) {
                break;
            }
            int quantity = graph.nodeOutDegree(id);
            for (int i = 0; i < quantity; i++) {
                int NP = graph.edgeTargetNodeId(graph.nodeOutEdgeId(id, i));
                if (Float.compare(Float.NEGATIVE_INFINITY, distance[NP]) != 0 && Float.compare(Float.NEGATIVE_INFINITY, distance[id]) != 0) {
                    float d = (float) (distance[id] + costFunction.costFactor(id, graph.nodeOutEdgeId(id, i)) * graph.edgeLength(graph.nodeOutEdgeId(id, i)));
                    if (d < distance[NP]) {
                        distance[NP] = d;
                        predecessor[NP] = id;
                        p.add(new WeightedNode(NP, d));
                    }
                }
            }
            distance[id] = Float.NEGATIVE_INFINITY;
        } while (!p.isEmpty());

        int i = endNodeId;
        nodePath.add(i);

        while (i != 0) {
            i = predecessor[i];
            nodePath.add(i);
        }

        Collections.reverse(nodePath);

        for (int j = 1; j < nodePath.size() - 1; j++) {
            boolean found = false;
            int edgeID = 0;
            while (!found) {
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(nodePath.get(j), edgeID)) == nodePath.get(j + 1)) {
                    edges.add(Edge.of(graph, graph.nodeOutEdgeId(nodePath.get(j), edgeID), nodePath.get(j), nodePath.get(j + 1)));
                    found = true;
                }
                edgeID++;
            }

        }
        if(edges.isEmpty()){
            return null;
        }
        return new SingleRoute(edges);
    }
}


