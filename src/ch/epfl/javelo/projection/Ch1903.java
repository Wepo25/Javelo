package ch.epfl.javelo.projection;


/**
 * This final not instantiable class is used to convert swiss coordinate into WGS 84 and inversely.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Ch1903 {

    /**
     * Private constructor.
     */
    private Ch1903() {
    }

    private static final int HOUR_IN_SECONDS = 3600;

    private static final double LON_SUBTRACTION = 26782.5;
    private static final double LAT_SUBTRACTION = 169028.66;

    private static final int EAST_SUBTRACTION = 2600000;
    private static final int NORTH_SUBTRACTION = 1200000;

    private static final double EAST_NORTH_FACTOR = Math.pow(10, -4);

    private static final double LAT_LON_FACTOR = Math.pow(10, -6);

    /**
     * This method allows is to switch between WGS84 coordinates to the Swiss coordinates by giving us the point's East coordinate.
     *
     * @param lon - double : Longitude of a point in the WGS84 system.
     * @param lat - double : Latitude of a point in the WGS84 system.
     * @return - double : The East coordinate of the point in the Swiss system.
     */
    public static double e(double lon, double lat) {
        double lon1 = EAST_NORTH_FACTOR * (HOUR_IN_SECONDS * (Math.toDegrees(lon)) - LON_SUBTRACTION);
        double lat1 = EAST_NORTH_FACTOR * (HOUR_IN_SECONDS * (Math.toDegrees(lat)) - LAT_SUBTRACTION);
        return 2600072.37 +
                211455.93 * lon1 -
                10938.51 * lon1 * lat1 -
                0.36 * lon1 * Math.pow(lat1, 2) -
                44.54 * Math.pow(lon1, 3);
    }

    /**
     * This method allows is to switch between WGS84 coordinates to the Swiss coordinates by giving us the point's North coordinate.
     *
     * @param lon - double : Longitude of a point in the WGS84 system.
     * @param lat - double : Latitude of a point in the WGS84 system.
     * @return - double : The North coordinate of the point in the Swiss system.
     */
    public static double n(double lon, double lat) {
        double lon1 = EAST_NORTH_FACTOR * (HOUR_IN_SECONDS * (Math.toDegrees(lon)) - LON_SUBTRACTION);
        double lat1 = EAST_NORTH_FACTOR * (HOUR_IN_SECONDS * (Math.toDegrees(lat)) - LAT_SUBTRACTION);
        return 1200147.07 +
                308807.95 * lat1 +
                3745.25 * Math.pow(lon1, 2) +
                76.63 * Math.pow(lat1, 2) -
                194.56 * Math.pow(lon1, 2) * lat1 +
                119.79 * Math.pow(lat1, 3);
    }

    /**
     * This method allows is to switch between the Swiss coordinates to the WGS84 coordinates by giving us the point's Longitude.
     *
     * @param e - double : East coordinates of a point in the Swiss system.
     * @param n - double : North coordinates of a point in the Swiss system.
     * @return - double : The Longitude of the point in the WGS84 system.
     */
    public static double lon(double e, double n) {
        double x = LAT_LON_FACTOR * (e - EAST_SUBTRACTION);
        double y = LAT_LON_FACTOR * (n - NORTH_SUBTRACTION);
        double lon0 = 2.6779094 +
                4.728982 * x + 0.791484 * x * y +
                0.1306 * x * Math.pow(y, 2) -
                0.0436 * Math.pow(x, 3);
        return Math.toRadians(lon0 * (100 / 36.));
    }

    /**
     * This method allows is to switch between the Swiss coordinates to the WGS84 coordinates by giving us the point's Latitude.
     *
     * @param e - double : East coordinates of a point in the Swiss system.
     * @param n - double : North coordinates of a point in the Swiss system.
     * @return - double : The Latitude of the point in the WGS84 system.
     */
    public static double lat(double e, double n) {
        double x = LAT_LON_FACTOR * (e - EAST_SUBTRACTION);
        double y = LAT_LON_FACTOR * (n - NORTH_SUBTRACTION);
        double lat0 = 16.9023892 +
                3.238272 * y -
                0.270978 * Math.pow(x, 2) -
                0.002528 * Math.pow(y, 2) -
                0.0447 * Math.pow(x, 2) * y -
                0.0140 * Math.pow(y, 3);
        return Math.toRadians(lat0 * (100 / 36.));
    }
}


