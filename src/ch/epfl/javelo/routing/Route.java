package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Interface Route which describe the behaviour of route.
 *
 * @author Alexande Mourot (346365)
 */
public interface Route {

    /**
     * This method allows us to get the segment's index to a given position on the route.
     *
     * @param position position given in meter.
     * @return the index link to the position.
     */
    int indexOfSegmentAt(double position);

    /**
     * This method gives us the route's length.
     *
     * @return the length.
     */
    double length();

    /**
     * This method allows us to get every edge of the route.
     *
     * @return a list containing all the edges.
     */
    List<Edge> edges();

    /**
     * This method allows us to get every point located at the edges' extremity of the route.
     *
     * @return a list containing every pointCh located to edge extremity.
     */
    List<PointCh> points();

    /**
     * This method allows us to find a point located to a given distance on the route.
     *
     * @param position position along the route.
     * @return the point corresponding to the position on the route.
     */
    PointCh pointAt(double position);

    /**
     * This method allows us to get the height for a given position along the route.
     *
     * @param position position along the route.
     * @return the height corresponding to the position.
     */
    double elevationAt(double position);

    /**
     * This method allows us to get the NodeId belonging to the route and closest to a given position.
     *
     * @param position position along the route.
     * @return the identity of the closest node to the position.
     */
    int nodeClosestTo(double position);

    /**
     * This method allows us to get the point closest to another given point.
     *
     * @param point reference point to find the closest around it.
     * @return closest point from the point passed in parameter.
     */
    RoutePoint pointClosestTo(PointCh point);


}
