package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * Record RoutePoint represent the closest route's point to a reference point given.
 * @param point - PointCh : point on the route.
 * @param position - double : position along the route in meters.
 * @param distanceToReference - double : distance (in meters) between the point and the reference.
 * @author Alexandre Mourot (346365)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * RoutePoint reference with nonsense values.
     */
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * This method allows us to get a RoutePoint similar to this except that
     * the position is shifted by the given difference.
     *
     * @param positionDifference - double : difference (can be negative).
     * @return - RoutePoint : similar to this but with position shifted.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);

    }

    /**
     * This method is usefull to save memory when comparing two RoutePoint. It return this if the distance
     * to the reference is less than the other's point one.
     *
     * @param that - RoutePoint : the RoutePoint to compare.
     * @return - RoutePoint : the closest to reference.
     */
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

