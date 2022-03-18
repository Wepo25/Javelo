package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

public record RoutePoint(PointCh point, double position, double distanceToReference) {
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);

    }

    public RoutePoint min(RoutePoint that) {
        if (this.distanceToReference <= that.distanceToReference) {
            return this;
        } else return that;
    }

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        if (distanceToReference <= thatDistanceToReference) {
            return this;
        } else return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}

