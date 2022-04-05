package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * This class allows us to switch between WGS 84 and Web Mercator coordinates.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public class WebMercator {


    private final static double pi = Math.PI;

    /**
     * Private constructor.
     */
    private WebMercator() {
    }

    /**
     * This method allows us to compute the x coordinate with a point given Longitude.
     *
     * @param lon - double : Longitude of a given point.
     * @return - double : The x coordinate of the point located at this Longitude.
     */
    public static double x(double lon) {
        return (lon + pi) / (2.0 * pi);
    }

    /**
     * This method allows us to compute the x coordinate with a point given Latitude.
     *
     * @param lat - double : Latitude of a given point.
     * @return - double : The x coordinate of the point located at this Latitude.
     */
    public static double y(double lat) {
        return (1 / (2 * pi)) * (pi - Math2.asinh(Math.tan(lat)));
    }

    /**
     * This method allows us to compute the Longitude with a point given x coordinate.
     *
     * @param x - double : X coordinate of a given point.
     * @return - double : The Longitude of the point located at this x coordinate.
     */
    public static double lon(double x) {
        return 2 * pi * x - pi;
    }

    /**
     * This method allows us to compute the Latitude with a point given y coordinate.
     *
     * @param y - double : Y coordinate of a given point.
     * @return - double : The Latitude of the point located at this y coordinate.
     */
    public static double lat(double y) {
        return Math.atan(Math.sinh(pi - 2 * pi * y));
    }
}
