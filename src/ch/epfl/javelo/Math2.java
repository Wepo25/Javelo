package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;

/**
 * This final not instantiable allows us to do calculation with statics methods.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Math2 {

    /**
     * Private constructor.
     */
    private Math2() {
    }

    /**
     * This method is used to compute the upper-bound of a division of two integers.
     *
     * @param x the numerator.
     * @param y the denominator.
     * @return The upper-bound of the division of x by y.
     * @throws IllegalArgumentException Throws an exception if at least one
     *                                  of the value is negative or equal to zero.
     */
    public static int ceilDiv(int x, int y) {
        checkArgument((y > 0 && x >= 0));
        return (x + y - 1) / y;
    }

    /**
     * This method allows us to find the function passing through y0 and y1 in order to find f(x).
     *
     * @param y0 Value on the y-axis at x = 0.
     * @param y1 Value on the y-axis at x = 1.
     * @param x  Value of which we want to calculate the image.
     * @return The image of the computed function at point x.
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * This method allows us to limit the value of a given integer to a given range of integers.
     *
     * @param min The minimum of a given range.
     * @param v   The value we want to check.
     * @param max The maximum of a given range.
     * @return min if v is smaller than min, max if v is greater than max and v otherwise.
     * @throws IllegalArgumentException (checkArgument) Throws an exception if the minimum value of the range
     *                                  is strictly greater than its given maximum.
     */
    public static int clamp(int min, int v, int max) {
        checkArgument((min <= max));
        return (v < min) ? min : Math.min(v, max);
    }

    /**
     * This method allows us to limit the value of a given double to a given range of doubles.
     *
     * @param min The minimum of a given range.
     * @param v   The value we want to check.
     * @param max The maximum of a given range.
     * @return min if v is smaller than min, max if v is greater than max and v otherwise.
     * @throws IllegalArgumentException (checkArgument) Throws an exception if the minimum value of the range is
     *                                  strictly greater than its given maximum.
     */
    public static double clamp(double min, double v, double max) {
        checkArgument((min <= max));
        return (v < min) ? min : Math.min(v, max);

    }

    /**
     * This method allows us to compute to asinh of a given value.
     *
     * @param x The value of which we want to compute the asinh.
     * @return The value which when applied to sinh(y) gives back x.
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }

    /**
     * This method allows us to compute the dotProduct of two points / vectors.
     *
     * @param uX X value of the first point / vector.
     * @param uY Y value of the first point / vector.
     * @param vX X value of the second point / vector.
     * @param vY Y value of the second point / vector.
     * @return The dotProduct of two points / vectors.
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, (Math.fma(uY, vY, 0)));
    }

    /**
     * This method allows us to compute the squared norm of a given vector.
     *
     * @param uX X value of the Vector.
     * @param uY Y value of the Vector.
     * @return The squared norm of the given vector.
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * This method allows us to compute the norm of a given vector.
     *
     * @param uX X value of the Vector.
     * @param uY Y value of the Vector.
     * @return The norm of the given vector.
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(dotProduct(uX, uY, uX, uY));
    }

    /**
     * This method allows us to compute the length of the projection of a given vector on another one.
     *
     * @param aX X value of A point .
     * @param aY Y value of A point .
     * @param bX X value of B point .
     * @param bY Y value of B point .
     * @param pX X value of P point .
     * @param pY Y value of P point .
     * @return The length of the projection of AP vector on the AB vector.
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double vX = pX - aX;
        double vY = pY - aY;
        double wX = bX - aX;
        double wY = bY - aY;

        return dotProduct(vX, vY, wX, wY) / norm(wX, wY);
    }
}

