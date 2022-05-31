package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * A record representing an edge.
 *
 * @param fromNodeId Edge's starting nodeIdentity.
 * @param toNodeId   Edge's final nodeIdentity.
 * @param fromPoint  Edge's first PointCh.
 * @param toPoint    Edge's last PointCh.
 * @param length     Edge's length.
 * @param profile    Function giving the profile of the edge.
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * This method allows us to create an Edge with fewer parameters since it uses directly
     * the data contained in graph.
     *
     * @param graph      The graph containing all the map-related elements.
     * @param edgeId     The identity of the edge.
     * @param fromNodeId The identity of the node the edge is leaving.
     * @param toNodeId   The identity of the node the edge is targeting.
     * @return A record of Edge.
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(
                fromNodeId,
                toNodeId,
                graph.nodePoint(fromNodeId),
                graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId),
                graph.edgeProfile(edgeId));
    }

    /**
     * This method allows us to compute the closest position to a given point.
     *
     * @param point The point of reference.
     * @return The position on the edge that is the closest to the given point.
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(
                fromPoint.e(),
                fromPoint.n(),
                toPoint.e(),
                toPoint.n(),
                point.e(),
                point.n()
        );
    }

    /**
     * This method allows us to compute the point on the Edge at the given position.
     *
     * @param position The position at which we are computing the point.
     * @return The point on the Edge at the given position.
     */
    public PointCh pointAt(double position) {
        if (length != 0) {
            double e = Math2.interpolate(fromPoint.e(), toPoint.e(), position / length);
            double n = Math2.interpolate(fromPoint.n(), toPoint.n(), position / length);
            return new PointCh(e, n);
        } else return fromPoint;


    }

    /**
     * This method allows us to compute the elevation at a given position on an Edge.
     *
     * @param position The desired position.
     * @return The elevation at the given position.
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }


}




