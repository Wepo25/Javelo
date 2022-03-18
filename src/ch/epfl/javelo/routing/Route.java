package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public interface Route {
    int indexOfSegmentAt(double position);

    double length();

    List<Edge> edges();

    List<PointCh> points();

    PointCh pointAt(double position);

    double elevationAt(double position);

    int nodeClosestTo(double position);

    RoutePoint pointClosestTo(PointCh point);


}
