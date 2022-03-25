package ch.epfl.javelo.routing;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SingleRoute implements Route {
    private List<Edge> edges;
    private double[] positions;

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        double length = 0;
        positions = new double[edges.size() +1];
        positions[0] = 0;
        for (int i = 0; i < edges().size(); i++) {
            length += edges.get(i).length();
            positions[i+1] = length;
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
        list.add(edges.get(0).fromPoint());
        for (Edge edge : edges) {
            list.add((edge.toPoint()));
        }
        return list;
    }

    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int edgeIndex = edgeIndex(position);
        double newPos = position - positions[edgeIndex] ;
            return edges.get(edgeIndex).pointAt(newPos);
    }


    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int edgeIndex = edgeIndex(position);
        double newPos = position - positions[edgeIndex] ;
        return edges.get(edgeIndex).elevationAt(newPos);
    }
    public int nodeClosestTo(double position){
        position = Math2.clamp(0, position, length());
        int edgeIndex = edgeIndex(position);
        double diff1 = position - positions[edgeIndex];
        double diff2 = positions[edgeIndex+1] - position;
        return (diff1 < diff2) ? edges.get(edgeIndex).fromNodeId() : edges.get(edgeIndex).toNodeId();
    }

    private int edgeIndex(double position) {
        position = Math2.clamp(0, position,length());
        int resultSearch = Arrays.binarySearch(positions, position);
        int edgeIndex;
        edgeIndex = (resultSearch >=0) ? resultSearch : -resultSearch-2 ;
        return Math2.clamp(0,edgeIndex, edges.size()-1);
    }

    public RoutePoint pointClosestTo(PointCh point){
        RoutePoint closest = RoutePoint.NONE;
        double position;
        double actualPosition;
        for (Edge edge: edges) {
             actualPosition= Math2.clamp(0,edge.positionClosestTo(point),edge.length());
             position = actualPosition- positions[edges.indexOf(edge)];
            System.out.println(position);
            System.out.println(actualPosition);
            closest = closest.min(edge.pointAt(actualPosition), actualPosition, point.distanceTo(edge.pointAt(position)));
        }return closest ;
    }

}