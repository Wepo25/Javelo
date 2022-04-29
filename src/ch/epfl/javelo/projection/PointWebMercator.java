package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * This class is used to represent a point in the Web Mercator system.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public record PointWebMercator(double x, double y) {

    private static final int MIN_ZOOM = 8;
    /**
     * Constructor checking the validity of its arguments.
     *
     * @param x - double : X coordinate of a point.
     * @param y - double : Y coordinate of a point.
     * @throws IllegalArgumentException (checkArgument) : Throws an exception if the
     * given coordinates are smaller than 0 or greater than 1.
     */
    public PointWebMercator {
        checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     * This method allows us to get the coordinates of a given point at zoom level 0.
     *
     * @param zoomLevel - int : The zoom level between 0 and 19.
     * @param x         - double : X coordinate of a point at this specific zoom level.
     * @param y         - double : Y coordinate of a point at this specific zoom level.
     * @return - PointWebMercator : The given object with its coordinates at zoom level 0.
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -MIN_ZOOM - zoomLevel), Math.scalb(y, -MIN_ZOOM - zoomLevel));
    }

    /**
     * This method allows us to adapt the coordinates of PointCh to one of the world standards.
     *
     * @param pointCh - PointCh : A point defined with the Swiss coordinates system.
     * @return - PointWebMercator : The given point with coordinates expressed in the Mercator system.
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * This method allows us to adapt a point x coordinate to a specific level of zoom.
     *
     * @param zoomLevel - int : A given zoom level.
     * @return - double : This point's x coordinates with the given zoom applied.
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, MIN_ZOOM + zoomLevel);
    }

    /**
     * This method allows us to adapt a point y coordinate to a specific level of zoom.
     *
     * @param zoomLevel - int : A given zoom level.
     * @return - double : This point's y coordinates with the given zoom applied.
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, MIN_ZOOM + zoomLevel);
    }

    /**
     * This method allows us to get a point's Longitude.
     *
     * @return - double : This point's Longitude.
     */
    public double lon() {
        return WebMercator.lon(this.x);
    }

    /**
     * This method allows us to get a point's Latitude.
     *
     * @return - double : This point's Latitude.
     */
    public double lat() {
        return WebMercator.lat(this.y);
    }

    /**
     * This method allows us to turn a point expressed with the Mercator system into the Swiss one.
     *
     * @return - PointCh : This point expressed in the Swiss coordinates system,
     * or null if the point is not in the Swiss.
     */
    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        return SwissBounds.containsEN(e, n)? new PointCh(e, n) : null;
    }
}
