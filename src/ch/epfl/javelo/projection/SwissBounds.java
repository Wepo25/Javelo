package ch.epfl.javelo.projection;

public final class SwissBounds {

    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Private constructor.
     */
    private SwissBounds() {
    }

    /**
     * This method allows us to check if a given location is situated in Switzerland.
     *
     * @param e - double : The point's East coordinate in the Swiss system.
     * @param n - double : The point's North coordinate in the Swiss system.
     * @return - boolean : Whether the given point is located in Switzerland.
     */
    public static boolean containsEN(double e, double n) {
        return (MAX_E >= e && MIN_E <= e && MAX_N >= n && MIN_N <= n);
    }
}
