package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * A MultiRoute implementing Route.
 * Represent a complex itinerary named MultiRoute among this project.
 * It contains consist of several Route (segment) which can be either Single or Multi
 * however at the "end" of the chain there is always SingleRoutes.
 * Several methods used recursive call to reach these last SingleRoutes.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double[] positions;

    /**
     * This method constructs a MultiRoute with a list of Route given ,and a table (positions)
     * containing the length at each segment positions.
     *
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        positions = createPositions(segments);
    }

    private double[] createPositions(List<Route> segments) {
        final double[] positions;
        double length = 0;
        positions = new double[segments.size() + 1];
        positions[0] = 0;
        for (int i = 0; i < segments.size(); i++) {
            length += segments.get(i).length();
            positions[i + 1] = length;
        }
        return positions;
    }

    /**
     * Private method to optimize finding the right index of segment at the scale of this MultiRoute.
     *
     * @param position - double : position given in meter.
     * @return - int : the index link to the position.
     */
    private int indexOfSegmentOnRoute(double position) { // refaire + comme SingleRoute binary
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

    /**
     * This method allows us to get the index in terms of SingleRoute to a given position on the route.
     *
     * @param position - double : position given in meter.
     * @return - int : the index of the SingleRoute linked to the position.
     */
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


    /**
     * This method gives us the MultiRoute's length.
     *
     * @return - double : the total length of the MultiRoute.
     */
    @Override
    public double length() {
        return positions[positions.length - 1];
    }

    /**
     * This method allows us to get every edges of the MultiRoute.
     *
     * @return - List<Edges> : containing all the edges.
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (Route route : segments) {
            edges.addAll(route.edges());
        }
        return List.copyOf(edges);
    }

    /**
     * This method allows us to get every point located at the edges extremity of the route.
     *
     * @return - List<PointCh> : containing every pointCh located to edges extremity.
     */
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

    /**
     * This method allows us to find a point located to a given distance on the MultiRoute.
     *
     * @param position - double : position along the route.
     * @return - PointCh : the point corresponding to the position on the MultiRoute.
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).pointAt(position - positions[index]);
    }

    /**
     * This method allows us to get the height for a given position along the MultiRoute.
     *
     * @param position - double : position along the route.
     * @return - double : the height corresponding to the position.
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).elevationAt(position - positions[index]);

    }

    /**
     * This method allows us to get the NodeId belonging to the route and closest to a given position.
     *
     * @param position - double : position along the route.
     * @return - int : the identity of the closest node to the position.
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int index = indexOfSegmentOnRoute(position);
        return segments.get(index).nodeClosestTo(position - positions[index]);
    }

    /**
     * This method allows us to get the point closest to an other given point.
     *
     * @param point - PointCh : reference point to find the closest around it.
     * @return - RoutePoint : closest point from the point passed in parameter.
     */
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
