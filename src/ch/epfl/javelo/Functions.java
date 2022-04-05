package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * This final not instantiable class allows us to get create mathematical function.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public class Functions {

    /**
     * private constructor which allows the class to be uninstantiable.
     */
    private Functions() {
    }

    /**
     * @param y the constant which has to be returned
     * @return a constant function.
     */
    public static DoubleUnaryOperator constant(double y) {
        return x -> y;
    }

    /**
     * @param samples table containing values spaced regularly.
     * @param xMax    the maximum value taken by the function.
     * @return return a function obtained by linear interplolation between
     * all values given samples from 0 to XMax.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(xMax > 0 && samples.length >= 2);
        return (x) -> {
            if (x < 0) return samples[0];
            if (x >= xMax) return samples[samples.length - 1];
            double ecart = xMax / (samples.length - 1);
            int borneInf = (int) (x / ecart);
            return Math2.interpolate(samples[borneInf], samples[borneInf + 1], ((x - borneInf * ecart) / ecart));
        };

    }
}