package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

public class MultiRoute implements Route{
    private final List<Route> segments;
    private double[] positions;


    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        double length =0;
        positions = new double[segments.size() +1];
        positions[0] = 0;
        for (int i = 0; i < edges().size(); i++) {
            length += segments.get(i).length();
            positions[i+1] = length;
        }
    }

    @Override // utiliser indexSegmentAt dedans si mulitroute imbirqué
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length());
        double distance = 0;
        for(Route route : segments){
            if(distance+route.length()>=position){
                return route.indexOfSegmentAt(position); // a verif;
            }
            else{
                distance+= route.length();
            }
        }
        return segments.indexOf(segments.get(segments.size()-1));// a verif si on peut eviter de mettre un ligne jamais atteinte
    }
// opti possible si on créer 1 tableau avec les longueur et que l'on prend le dernier index
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
        return List.copyOf(edges);
    }

    @Override // bon ordre ? avec HashSet
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        Set<PointCh> set = new HashSet<PointCh>();
        for(Route route : segments){
            points.addAll(route.points());
        }
        set.addAll(points);
        return List.copyOf(set);
    }

    @Override // potentielle problème position fait size +1; normalement ok
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentAt(position);
        return segments.get(index).pointAt(position- positions[index]);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentAt(position);
        return segments.get(index).elevationAt(position- positions[index]);

        }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentAt(position);
        return segments.get(index).nodeClosestTo(position- positions[index]);
    }



    @Override
    public RoutePoint pointClosestTo(PointCh point) {
       List<RoutePoint> closestPoints = new ArrayList<RoutePoint>();
        for( Route segment: segments){
            closestPoints.add(segment.pointClosestTo(point));
        }
        RoutePoint closest = RoutePoint.NONE;
        for( RoutePoint closestPoint : closestPoints){
            closest = closestPoint.min(closest);
        }
        return closest;
    }

}
