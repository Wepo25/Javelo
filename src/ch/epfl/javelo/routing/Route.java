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
     * @param position - double : position given in meter.
     * @return - int : the index link to the position.
     */
    int indexOfSegmentAt(double position);

    /**
     * This method gives us the route's length.
     *
     * @return - double : the length.
     */
    double length();

    /**
     * This method allows us to get every edges of the route.
     *
     * @return - List<Edges> : containing all the edges.
     */
    List<Edge> edges();

    /**
     * This method allows us to get every point located at the edges extremity of the route.
     *
     * @return - List<PointCh> : containing every pointCh located to edges extremity.
     */
    List<PointCh> points();

    /**
     * This method allows us to find a point located to a given distance on the route.
     *
     * @param position - double : position along the route.
     * @return - PointCh : the point corresponding to the position on the route.
     */
    PointCh pointAt(double position);

    /**
     * This method allows us to get the height for a given position along the route.
     *
     * @param position - double : position along the route.
     * @return - double : the height corresponding to the position.
     */
    double elevationAt(double position);

    /**
     * This method allows us to get the NodeId belonging to the route and closest to a given position.
     *
     * @param position - double : position along the route.
     * @return - int : the identity of the closest node to the position.
     */
    int nodeClosestTo(double position);

    /**
     * This method allows us to get the point closest to an other given point.
     *
     * @param point - PointCh : reference point to find the closest around it.
     * @return - RoutePoint : closest point from the point passed in parameter.
     */
    RoutePoint pointClosestTo(PointCh point);


}
