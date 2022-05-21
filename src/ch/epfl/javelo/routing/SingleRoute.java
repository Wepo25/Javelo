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
     * This method constructs a SingleRoute with a list of edges (edges) given and a table
     * containing the length at each edge position (positions).
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
     * This method is not usefull for this type of route which contain only one segment.
     * @param position - double : position given in meter.
     * @return -int : 0.
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return CONSTANT_INDEX_OF_SEGMENT;
    }

    /**
     * This method gives us the route's length.
     *
     * @return - double : the length.
     */
    @Override
    public double length() {
        return positions[positions.length -1];
    }

    /**
     * This method allows us to get every edges of the route.
     *
     * @return - List<Edges> : immutable and containing all the edges.
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * This method allows us to get every point located at the edges extremity of the route.
     *
     * @return - List<PointCh> : containing every pointCh located to edges extremity.
     */
    @Override
    public List<PointCh> points() {
        return pointList;
    }

    /**
     * This method allows us to find a point located to a given distance on the route.
     *
     * @param position - double : position along the route.
     * @return - PointCh : the point corresponding to the position on the route.
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
     * @param position - double : position along the route.
     * @return - double : the height corresponding to the position.
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
     * @param position - double : position along the route.
     * @return - int : the identity of the closest node to the position.
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
     * @param point - PointCh : reference point to find the closest around it.
     * @return - RoutePoint : closest point from the point passed in parameter.
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closest = RoutePoint.NONE;
        int counter = 0;
        for (Edge edge : edges) {
            double actualPosition = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            double position = actualPosition + positions[counter];
            closest = closest.min(edge.pointAt(actualPosition), position,
                    point.distanceTo(edge.pointAt(actualPosition)));
            ++counter;
        }
        return closest;
    }

    /**
     * This method create an Array containing the length at a certain edge.
     * @param edges - List<Edge> : list of edge contained in this.
     * @return - double[] : the Array containing the lengths of at edge index.
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
     * @param position - double : position.
     * @return - int : the index of the edge.
     */
    private int edgeIndex(double position) {
        double boundedPosition = bounds(position);
        int resultSearch = Arrays.binarySearch(positions, boundedPosition);
        int edgeIndex;
        edgeIndex = (resultSearch >= 0) ? resultSearch : -resultSearch - 2;
        return Math2.clamp(0, edgeIndex, edges.size() - 1);
    }

    private double bounds(double position){
        return Math2.clamp(0, position, length());
    }
}