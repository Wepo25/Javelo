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
                return route.indexOfSegmentAt(position-positions[segments.indexOf(route)]); // a verif;
            }
            else{
                distance+= route.length();
            }
        }
        return segments.indexOf(segments.get(segments.size()-1));// a verif si on peut eviter de mettre un ligne jamais atteinte
    }
    private int indexOfSegmentOnRoute(double position){
        position = Math2.clamp(0, position, length());
        double distance = 0;
        for(Route route : segments){
            if (distance+route.length() >= position){
                return segments.indexOf(route);
            }else{
                distance += route.length();
            }
        }return -1;
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

    @Override // bon ordre ? avec HashSet mais treeSet ne marche pas non plus car PointCh non comprable;
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for(Route route : segments){
            points.addAll(route.points());
            points.remove(points.size()-1); // enlève le dernier de chaque car il est rajouté a chaque fois
        }
        return List.copyOf(points);
    }

    @Override // index donne au total et pas couche( ex 7 au lieu de 2);
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).pointAt(position- positions[index]);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).elevationAt(position- positions[index]);

        }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).nodeClosestTo(position- positions[index]);
    }



    @Override
    public RoutePoint pointClosestTo(PointCh point) {
       RoutePoint points = RoutePoint.NONE;
        for( Route segment: segments){
           points = points.min(segment.pointClosestTo(point)
                   .withPositionShiftedBy(positions[segments.indexOf(segment)]));
        }
        return points;
    }

}
