package ch.epfl.javelo.projection;

/**
 * This not instantiable class contains values and function to describe the geographic limit of the Swiss.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class SwissBounds {

    /**
     * Minimum east coordinate.
     */
    public static final double MIN_E = 2485000;

    /**
     * Maximum east coordinate.
     */
    public static final double MAX_E = 2834000;

    /**
     * Minimum north coordinate.
     */
    public static final double MIN_N = 1075000;

    /**
     * Maximum north coordinate.
     */
    public static final double MAX_N = 1296000;

    /**
     * Width of the Swiss.
     */
    public static final double WIDTH = MAX_E - MIN_E;

    /**
     * Height of the Swiss.
     */
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Private constructor.
     */
    private SwissBounds() {
    }

    /**
     * This method allows us to check if a given location is situated in Switzerland.
     *
     * @param e The point's East coordinate in the Swiss system.
     * @param n The point's North coordinate in the Swiss system.
     * @return Whether the given point is located in Switzerland.
     */
    public static boolean containsEN(double e, double n) {
        return (MAX_E >= e && MIN_E <= e && MAX_N >= n && MIN_N <= n);
    }
}
