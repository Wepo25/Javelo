package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

public class MultiRoute implements Route {
    private final List<Route> segments;
    private final double[] positions;


    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        double length = 0;
        positions = new double[segments.size() + 1];
        positions[0] = 0;
        for (int i = 0; i < segments.size(); i++) {
            length += segments.get(i).length();
            positions[i + 1] = length;
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = 0;
        double length = 0;

        for (Route route : segments) {
            length = route.length();
            if (length < position) {
                index += route.indexOfSegmentAt(route.length()) + 1;
                position -= length;
            } else {
                index += route.indexOfSegmentAt(position);
                break;
            }
        }
        return index;
    }

    private int indexOfSegmentOnRoute(double position) {
        position = Math2.clamp(0, position, length());
        double distance = 0;
        for (Route route : segments) {
            if (distance + route.length() >= position) {
                return segments.indexOf(route);
            } else {
                distance += route.length();
            }
        }
        return -1;
    }

    @Override
    public double length() {
        double distance = 0;
        for (Route route : segments) {
            distance += route.length();
        }
        return distance;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (Route route : segments) {
            edges.addAll(route.edges());
        }
        return List.copyOf(edges);
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();

        for (Route route : segments) {
            points.addAll(route.points());
            points.remove(points.size() - 1);

        }
        List<PointCh> list = segments.get(segments.size() - 1).points();
        points.add(list.get(list.size() - 1));
        return List.copyOf(points);
    }

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).pointAt(position - positions[index]);
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).elevationAt(position - positions[index]);

    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).nodeClosestTo(position - positions[index]);
    }


    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint points = RoutePoint.NONE;
        for (Route segment : segments) {
            points = points.min(segment.pointClosestTo(point)
                    .withPositionShiftedBy(positions[segments.indexOf(segment)]));
        }
        return points;
    }

}
