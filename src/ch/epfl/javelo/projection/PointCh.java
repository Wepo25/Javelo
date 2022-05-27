package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.pow;

/**
 * This class is used to create a point represented in swiss coordinate system.
 *
 * @param e - double : East coordinate of the pointCh.
 * @param n - double : North coordinate of the pointCh.
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public record PointCh(double e, double n) {

    /**
     * Constructor checking if the given point is indeed in Switzerland or not.
     *
     * @param e - double : The point's East coordinate in the Swiss system.
     * @param n - double : The point's North coordinate in the Swiss system.
     * @throws IllegalArgumentException (checkArgument) : Throws an exception
     *                                  if the point is not located in Switzerland.
     */
    public PointCh {
        checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * This method allows us to compute the squared distance between two points.
     *
     * @param that - PointCh : Another point located in Switzerland.
     * @return - double : The squared distance between this point and a given point.
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - e(), that.n - n());
    }

    /**
     * This method allows us to compute the distance between two points.
     *
     * @param that - PointCh : Another point located in Switzerland.
     * @return - double : The distance between this point and a given point.
     */
    public double distanceTo(PointCh that) {
        return pow(squaredDistanceTo(that), 0.5);
    }

    /**
     * This method allows us to get the Longitude of a given point in the WGS84 system expressed in radians.
     *
     * @return - double : The Longitude of this point in the WGS84 system expressed in radians.
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * This method allows us to get the Latitude of a given point in the WGS84 system expressed in radians.
     *
     * @return - double : The Latitude of this point in the WGS84 system expressed in radians.
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}
