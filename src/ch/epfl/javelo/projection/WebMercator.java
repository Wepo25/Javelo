package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static java.lang.Math.PI;

/**
 * This class not instantiable allows us to switch between WGS 84 and Web Mercator coordinates.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class WebMercator {

    /**
     * Private constructor.
     */
    private WebMercator() {
    }

    /**
     * This method allows us to compute the x coordinate with a point given Longitude.
     *
     * @param lon Longitude of a given point.
     * @return The x coordinate of the point located at this Longitude.
     */
    public static double x(double lon) {
        return (lon + PI) / (2.0 * PI);
    }

    /**
     * This method allows us to compute the x coordinate with a point given Latitude.
     *
     * @param lat Latitude of a given point.
     * @return The x coordinate of the point located at this Latitude.
     */
    public static double y(double lat) {
        return (1 / (2 * PI)) * (PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * This method allows us to compute the Longitude with a point given x coordinate.
     *
     * @param x X coordinate of a given point.
     * @return The Longitude of the point located at this x coordinate.
     */
    public static double lon(double x) {
        return 2 * PI * x - PI;
    }

    /**
     * This method allows us to compute the Latitude with a point given y coordinate.
     *
     * @param y Y coordinate of a given point.
     * @return The Latitude of the point located at this y coordinate.
     */
    public static double lat(double y) {
        return Math.atan(Math.sinh(PI - 2 * PI * y));
    }
}
