package ch.epfl.javelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Preconditions.checkArgument;


/**
 * This final not instantiable class allows us to get create mathematical function.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Functions {

    /**
     * Private constructor which allows the class to be not instantiable.
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
     * @return returns a function obtained by linear interpolation between
     * all values given samples from 0 to XMax.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        checkArgument(xMax > 0 && samples.length >= 2);
        List<Float> temp = new ArrayList<>();

        for (Float f : samples) temp.add(f);

        List<Float> immutableSamples = List.copyOf(temp);
        return (x) -> {
            if (x < 0) return immutableSamples.get(0);
            if (x >= xMax) return immutableSamples.get(immutableSamples.size() - 1);
            double gap = xMax / (immutableSamples.size() - 1);
            int borneInf = (int) (x / gap);
            return Math2.interpolate(immutableSamples.get(borneInf),
                    immutableSamples.get(Math2.clamp(0, borneInf + 1, immutableSamples.size() - 1)),
                    ((x - borneInf * gap) / gap));
        };
    }
}