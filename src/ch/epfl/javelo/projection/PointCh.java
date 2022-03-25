package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

public record PointCh(double e, double n) {

    /**
     * Constructor checking if the given point is indeed in Switzerland or not.
     *
     * @param e - double : The point's East coordinate in the Swiss system.
     * @param n - double : The point's North coordinate in the Swiss system.
     * @throw IllegalArgumentException (checkArgument) : Throws an exception if the point is not located in Switzerland.
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
        return abs(pow(that.e - e(), 2) + pow(that.n- n(), 2));
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
