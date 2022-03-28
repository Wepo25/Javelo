package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiRoute implements Route{
    private final List<Route> segments;


    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length());
        double distance = 0;
        for(Route route : segments){
            if(distance+route.length()>=position){
                return segments.indexOf(route);
            }
            else{
                distance+= route.length();
            }
        }
        return 0;
    }

    @Override
    public double length() {
        double distance = 0;
        for(Route route : segments){
            distance+= route.length();
        }
        return distance;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for(Route route : segments){
            edges.addAll(route.edges());
        }
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        Set<PointCh> set = new HashSet<PointCh>();
        for(Route route : segments){
            points.addAll(route.points());
        }
        set.addAll(points);
        return List.copyOf(set);
    }

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentAt(position);
        double distance = 0;
        for (int i = 0; i <= index; i++) {
            distance += segments.get(i).length();
        }
        Route route = segments.get(index);
        return route.pointAt(position-distance);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        List<Edge> edges = edges();
        Edge edgeAtPos = edges.get(0);
        PointCh pointAtPos = pointAt(position);
        double distance = edgeAtPos.positionClosestTo(pointAtPos);
        for (Edge edge : edges) {
            if(edge.positionClosestTo(pointAtPos)<distance){
                distance = edge.positionClosestTo(pointAtPos);
                edgeAtPos = edge;
            }
        }
        return edgeAtPos.elevationAt((edgeAtPos.length()*position)/length());
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        List<Edge> edges = edges();
        Edge edgeAtPos = edges.get(0);
        PointCh pointAtPos = pointAt(position);
        double totalDistance = 0;
        double distance = edgeAtPos.positionClosestTo(pointAtPos);
        for (Edge edge : edges) {
            if(edge.positionClosestTo(pointAtPos)<distance){
                distance = edge.positionClosestTo(pointAtPos);
                edgeAtPos = edge;
            }
        }
        for (int i = 0; i < edges.indexOf(edgeAtPos); i++) {
            totalDistance += edges.get(i).length();
        }
        double d1 = position - totalDistance;
        double d2 = totalDistance+ edgeAtPos.length() - position;
        return (d1 <= d2) ? edgeAtPos.fromNodeId() : edgeAtPos.toNodeId();
    }



    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }

}
