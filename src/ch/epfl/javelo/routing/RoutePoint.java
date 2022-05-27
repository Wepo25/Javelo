package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * Record RoutePoint represent the closest route's point to a reference point given.
 *
 * @param point               - PointCh : point on the route.
 * @param position            - double : position along the route in meters.
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
        return positionDifference == 0 ? this : new RoutePoint(point, position + positionDifference, distanceToReference);

    }

    /**
     * This method is useful to save memory when comparing two RoutePoint. It returns this if the distance
     * to the reference is less than the other's RoutePoint distance.
     *
     * @param that - RoutePoint : the RoutePoint to compare.
     * @return - RoutePoint : the closest to reference between this and that RoutePoint.
     */
    public RoutePoint min(RoutePoint that) {
        return (this.distanceToReference <= that.distanceToReference) ? this : that;
    }

    /**
     * This method is useful to save memory when comparing two RoutePoint. It returns this if the distance
     * to the reference is less than the other's RoutePoint distance.
     *
     * @param thatPoint               - PointCh : point on the route.
     * @param thatPosition            - double : position along the route in meters.
     * @param thatDistanceToReference - double : distance (in meters) between the point and the reference.
     * @return - RoutePoint : the closest to reference between this and the RoutePoint corresponding to the arguments.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return (distanceToReference <= thatDistanceToReference) ?
                this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}

