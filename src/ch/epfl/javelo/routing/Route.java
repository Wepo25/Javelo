package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public interface Route {

    /**
     * This method allows us to get the segment's index to a given position
     *
     * @param position - double : position given in meter.
     * @return - int : the index link to the position
     */
    int indexOfSegmentAt(double position);

    /**
     * This method gives us the itinerary's length.
     *
     * @return - double : the length.
     */
    double length();

    /**
     * This method allows us to get every edges of the itinerary.
     *
     * @return - List<Edges> : containing all the edges.
     */
    List<Edge> edges();

    /**
     * This method allows us to get every point located to the edges extremity from itinerary.
     *
     * @return - List<PointCh> : containing every pointCh located to edges extremity.
     */
    List<PointCh> points();

    /**
     * This method allows us to find a point located to a given distance on the itinerary.
     *
     * @param position - double.
     * @return - PointCh : the point corresponding to the position on the itinerary.
     */
    PointCh pointAt(double position);

    /**
     * This method allows us to get the height for a given position along the itinerary.
     *
     * @param position - double.
     * @return - double : the height corresponding to the position.
     */
    double elevationAt(double position);

    /**
     * This method allows us to get the NodeId  belonging to the itinerary and being closest to a given position.
     *
     * @param position - double.
     * @return - int : the identity of the closest node to the position.
     */
    int nodeClosestTo(double position);

    /**
     * This method allows us to get the point closest to an other given point.
     *
     * @param point - PointCh : reference point to find a closest around it.
     * @return - RoutePoint : closest point from the point passed in parameter.
     */
    RoutePoint pointClosestTo(PointCh point);


}
