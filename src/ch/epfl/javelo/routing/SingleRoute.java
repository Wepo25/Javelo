package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A SingleRoute implementing Route.
 * Represent a simple itinerary named route among this project.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class SingleRoute implements Route {

    private static final int CONSTANT_INDEX_OF_SEGMENT = 0;

    private final List<Edge> edges;
    private final double[] positions;

    private final List<PointCh> pointList;

    /**
     * Constructs a SingleRoute with a list of edges (edges) given and a table
     * containing the length at each edge position (positions).
     *
     * @param edges of the Route.
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        positions = createPositions(edges);

        List<PointCh> list = new ArrayList<>();
        list.add(edges.get(0).fromPoint());

        for (Edge edge : edges) {
            list.add((edge.toPoint()));
        }
        this.pointList = List.copyOf(list);
    }

    /**
     * This method is not useful for this type of route which contain only one segment.
     *
     * @param position position given in meter.
     * @return 0.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return CONSTANT_INDEX_OF_SEGMENT;
    }

    /**
     * This method gives us the route's length.
     *
     * @return the length.
     */
    @Override
    public double length() {
        return positions[positions.length - 1];
    }

    /**
     * This method allows us to get every edge of the route.
     *
     * @return list immutable and containing all the edges.
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * This method allows us to get every point located at the edges' extremity of the route.
     *
     * @return list containing every pointCh located to edge extremity.
     */
    @Override
    public List<PointCh> points() {
        return pointList;
    }

    /**
     * This method allows us to find a point located to a given distance on the route.
     *
     * @param position position along the route.
     * @return the point corresponding to the position on the route.
     */
    @Override
    public PointCh pointAt(double position) {
        double boundedPosition = bounds(position);
        int edgeIndex = edgeIndex(boundedPosition);
        double newPos = boundedPosition - positions[edgeIndex];
        return edges.get(edgeIndex).pointAt(newPos);
    }

    /**
     * This method allows us to get the height for a given position along the route.
     *
     * @param position position along the route.
     * @return the height corresponding to the position.
     */
    @Override
    public double elevationAt(double position) {
        double boundedPosition = bounds(position);
        int edgeIndex = edgeIndex(boundedPosition);
        double newPos = boundedPosition - positions[edgeIndex];
        return edges.get(edgeIndex).elevationAt(newPos);
    }

    /**
     * This method allows us to get the NodeId belonging to the route and being the closest to a given position.
     *
     * @param position position along the route.
     * @return the identity of the closest node to the position.
     */
    @Override
    public int nodeClosestTo(double position) {
        double boundedPosition = bounds(position);
        int edgeIndex = edgeIndex(boundedPosition);
        double diff1 = boundedPosition - positions[edgeIndex];
        double diff2 = positions[edgeIndex + 1] - boundedPosition;
        return (diff1 <= diff2) ? edges.get(edgeIndex).fromNodeId() : edges.get(edgeIndex).toNodeId();
    }

    /**
     * This method allows us to get the point closest to an other given point.
     *
     * @param point reference point to find the closest around it.
     * @return closest point from the point passed in parameter.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closest = RoutePoint.NONE;
        int counter = 0;
        for (Edge edge : edges) {
            double actualPosition = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            double position = actualPosition + positions[counter++];
            closest = closest.min(edge.pointAt(actualPosition), position,
                    point.distanceTo(edge.pointAt(actualPosition)));
        }
        return closest;
    }

    /**
     * This method creates an Array containing the length at a certain edge.
     *
     * @param edges list of edge contained in this.
     * @return The Array containing the lengths of at edge index.
     */
    private double[] createPositions(List<Edge> edges) {
        final double[] positions;
        double length = 0;
        positions = new double[edges.size() + 1];
        positions[0] = 0;
        for (int i = 0; i < edges().size(); i++) {
            length += edges.get(i).length();
            positions[i + 1] = length;
        }
        return positions;
    }

    /**
     * This method allows us to find the edge index at a given positions.
     *
     * @param position position.
     * @return the index of the edge.
     */
    private int edgeIndex(double position) {
        double boundedPosition = bounds(position);
        int resultSearch = Arrays.binarySearch(positions, boundedPosition);
        int edgeIndex;
        edgeIndex = (resultSearch >= 0) ? resultSearch : -resultSearch - 2;
        return Math2.clamp(0, edgeIndex, edges.size() - 1);
    }

    /**
     * This method allows to clamp a position.
     *
     * @param position to be clamped.
     * @return the clamped position.
     */
    private double bounds(double position) {
        return Math2.clamp(0, position, length());
    }
}