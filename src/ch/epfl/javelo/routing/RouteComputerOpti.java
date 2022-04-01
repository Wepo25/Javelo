package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

public final class RouteComputerOpti {

    private final Graph graph;
    private final CostFunction costFunction;


    public RouteComputerOpti(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        Preconditions.checkArgument(startNodeId != endNodeId);

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }

        }

        int visitedNode = 0;
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
                    visitedNode += 1;
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
            int h = 0;
            while (!found) {
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(nodePath.get(j), h)) == nodePath.get(j + 1)) {
                    edges.add(new Edge(nodePath.get(j), nodePath.get(j + 1), graph.nodePoint(nodePath.get(j)), graph.nodePoint(nodePath.get(j + 1)), graph.edgeLength(graph.nodeOutEdgeId(nodePath.get(j), h)), graph.edgeProfile(graph.nodeOutEdgeId(nodePath.get(j), h))));
                    found = true;
                }
                h++;
            }

        }
        System.out.println(visitedNode);
        return new SingleRoute(edges);
    }
}


