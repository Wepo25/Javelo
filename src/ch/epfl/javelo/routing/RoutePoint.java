package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;


/**
 * Record RoutePoint represent the closest route's point to a reference point given.
 *
 * @param point               point on the route.
 * @param position            position along the route in meters.
 * @param distanceToReference distance (in meters) between the point and the reference.
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
     * @param positionDifference difference (can be negative).
     * @return a route point similar to this but with position shifted.
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return positionDifference == 0 ? this : new RoutePoint(point,
                position + positionDifference,
                distanceToReference);

    }

    /**
     * This method is useful to save memory when comparing two RoutePoint. It returns this if the distance
     * to the reference is less than the other's RoutePoint distance.
     *
     * @param that the RoutePoint to compare.
     * @return the closest route point to reference between this and that RoutePoint.
     */
    public RoutePoint min(RoutePoint that) {
        return (this.distanceToReference <= that.distanceToReference) ? this : that;
    }

    /**
     * This method is useful to save memory when comparing two RoutePoint. It returns this if the distance
     * to the reference is less than the other's RoutePoint distance.
     *
     * @param thatPoint               point on the route.
     * @param thatPosition            position along the route in meters.
     * @param thatDistanceToReference distance (in meters) between the point and the reference.
     * @return the closest route point to reference between this and the RoutePoint corresponding to the arguments.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return (distanceToReference <= thatDistanceToReference) ?
                this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}

