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
        if(graph.nodeCount()<endNodeId || startNodeId < 0){
            return null;
        }
        System.out.println(graph.nodeOutDegree(149195));
        System.out.println(graph.nodeOutDegree(153181));


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
            if(quantity == 0) {
                return null;
            }
            for (int i = 0; i < quantity; i++) {
                int NP = graph.edgeTargetNodeId(graph.nodeOutEdgeId(id, i));
                if(Float.compare(Float.NEGATIVE_INFINITY, distance[NP])!=0 && Float.compare(Float.NEGATIVE_INFINITY, distance[id])!=0) {
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
                int index = graph.nodeOutEdgeId(nodePath.get(j), h);
                if (graph.edgeTargetNodeId(index) == nodePath.get(j + 1)) {
                    edges.add(Edge.of(graph, index,nodePath.get(j),  nodePath.get(j + 1)));

                    found = true;
                }
                h ++;
            }

        }
        if(edges.isEmpty()){
            return null;
        }
        return new SingleRoute(edges);
    }
}


