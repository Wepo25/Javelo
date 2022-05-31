package ch.epfl.javelo.routing;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.Math2.clamp;
import ch.epfl.javelo.projection.PointCh;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
     * @param seg List containing all the segments constituting this route.
     * @throws IllegalArgumentException (checkArgument) Throws an exception is the list of segment given is empty.
     */
    public MultiRoute(List<Route> seg) {
        checkArgument(!seg.isEmpty());
        segments = List.copyOf(seg);
        positions = createPositions(segments);
    }

    /**
     * This method allows us to get the index in terms of SingleRoute to a given position on the route.
     *
     * @param position position given in meter.
     * @return the index of the SingleRoute linked to the position.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        double boundedPosition = bounds(position);
        int index = 0;
        for (Route route : segments) {
            double length = route.length();
            if (length < boundedPosition) {
                index += route.indexOfSegmentAt(route.length()) + 1;
                boundedPosition -= length;
            } else {
                index += route.indexOfSegmentAt(boundedPosition);
                break;
            }
        }
        return index;
    }

    /**
     * This method gives us the MultiRoute's length.
     *
     * @return the total length of the MultiRoute.
     */
    @Override
    public double length() {
        return positions[positions.length - 1];
    }

    /**
     * This method allows us to get every edge of the MultiRoute.
     *
     * @return list containing all the edges.
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (Route route : segments) {
            edges.addAll(route.edges());
        }
        return Collections.unmodifiableList(edges);
    }

    /**
     * This method allows us to get every point located at the edge's extremity of the route.
     *
     * @return list containing every pointCh located to edge's extremity.
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
        return points;
    }

    /**
     * This method allows us to find a point located to a given distance on the MultiRoute.
     *
     * @param position Position along the route.
     * @return The point corresponding to the position on the MultiRoute.
     */
    @Override
    public PointCh pointAt(double position) {
        double boundedPosition = bounds(position);
        int index = indexOfSegmentOnRoute(boundedPosition);
        return segments.get(index).pointAt(boundedPosition - positions[index]);
    }

    /**
     * This method allows us to get the height for a given position along the MultiRoute.
     *
     * @param position position along the route.
     * @return the height corresponding to the position.
     */
    @Override
    public double elevationAt(double position) {
        double boundedPosition = bounds(position);
        int index = indexOfSegmentOnRoute(boundedPosition);
        return segments.get(index).elevationAt(boundedPosition - positions[index]);

    }

    /**
     * This method allows us to get the NodeId belonging to the route and closest to a given position.
     *
     * @param position position along the route.
     * @return the identity of the closest node to the position.
     */
    @Override
    public int nodeClosestTo(double position) {
        double boundedPosition = bounds(position);
        int index = indexOfSegmentOnRoute(boundedPosition);
        return segments.get(index).nodeClosestTo(boundedPosition - positions[index]);
    }

    /**
     * This method allows us to get the point closest to an other given point.
     *
     * @param point reference point to find the closest around it.
     * @return closest point from the point passed in parameter.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint points = RoutePoint.NONE;
        int counter = 0;
        for (Route segment : segments) {
            points = points.min(segment.pointClosestTo(point)
                    .withPositionShiftedBy(positions[counter++]));
        }
        return points;
    }

    /**
     * This method create an Array containing the length at a certain segment.
     *
     * @param segments list of route contained in this.
     * @return the Array containing the lengths of at segment index.
     */
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
     * Private method to find the right index of segment at the scale of this MultiRoute.
     *
     * @param position position given in meter.
     * @return the index link to the position.
     */
    private int indexOfSegmentOnRoute(double position) {
        double boundedPosition = bounds(position);
        int resultSearch = Arrays.binarySearch(positions, boundedPosition);
        int segmentIndex;
        segmentIndex = (resultSearch >= 0) ? resultSearch : -resultSearch - 2;
        return clamp(0, segmentIndex, segments.size() - 1);
    }

    /**
     * This method allows to clamp a position.
     *
     * @param position to be clamped.
     * @return the clamped position.
     */
    private double bounds(double position) {
        return clamp(0, position, length());
    }

}
