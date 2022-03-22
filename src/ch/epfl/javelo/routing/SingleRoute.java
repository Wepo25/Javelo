package ch.epfl.javelo.routing;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SingleRoute {
    private List<Edge> edges;
    private double[] tab;

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        double length = 0;
        for (int i = 0; i < edges().size(); i++) {
            tab[i] = length;
            length += edges.get(i).length();
        }

    }

    public int indexOfSegmentAt(double position) {
        return 0;
    }

    public double length() {
        double length = 0;
        for (Edge edge : edges) {
            length += edge.length();
        }
        return length;
    }

    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    public List<PointCh> points() {
        List<PointCh> list = new ArrayList<>();
        for (Edge edge : edges) {
            list.add(edge.fromPoint());
            list.add((edge.toPoint()));
        }
        return list;
    }

    public PointCh pointAt(double position) {
        int edgeIndex = edgeIndex(position);
        double newPos = position - tab[edgeIndex] ;
            return edges.get(edgeIndex).pointAt(newPos);
    }


    public double elevationAt(double position) {
        int edgeIndex = edgeIndex(position);
        double newPos = position - tab[edgeIndex] ;
        return edges.get(edgeIndex).elevationAt(newPos);
    }
    public int nodeClosestTo(double position){
        int edgeIndex = edgeIndex(position);
        double diff1 = position - tab[edgeIndex];
        double diff2 = tab[edgeIndex+1] - position;
        if (diff1 < diff2){
            return edges.get(edgeIndex).fromNodeId();
        }else return edges.get(edgeIndex).toNodeId();
// pas sur a voir si il y a un espacement regulier de nodeId entre from et to.
    }

    private int edgeIndex(double position) {
        position = Math2.clamp(0, position,length());
        int resultSearch = Arrays.binarySearch(tab, position);
        int edgeIndex;
        if (resultSearch >= 0) {
            return edgeIndex = resultSearch;
        } else {
            return edgeIndex = -resultSearch -2;
        }
    }

    public RoutePoint pointClosestTo(PointCh point){
        for (Edge edge: edges) {
            double position = position = Math2.clamp(0,position,length());
        }
        // 1. distance des extremité avec le point, 2.le point initial serat
    }

}