package ch.epfl.javelo.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

import static ch.epfl.javelo.Functions.sampled;
import static ch.epfl.javelo.Math2.interpolate;
import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.lang.Float.isNaN;

/**
 * A class intended to build an ElevationProfile
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class ElevationProfileComputer {

    /**
     * A private constructor.
     */
    private ElevationProfileComputer() {
    }

    /**
     * This method allows us to build an ElevationProfile while interpolating the missing data.
     *
     * @param route         The route of which we compute the ElevationProfile.
     * @param maxStepLength The gap between edges.
     * @return The ElevationProfile associated to the given route.
     * @throws IllegalArgumentException (checkArgument) Throws an exception if
     *                                  the gap between two edges is negative or null.
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        checkArgument(maxStepLength > 0);

        if (route == null) return null;
        int nbSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double length = route.length();
        double spaceBetween = length / (nbSamples - 1);
        float[] samples = new float[nbSamples];
        List<Integer> indexes = new ArrayList<>();

        IntStream.range(0, nbSamples).forEach(i -> samples[i] = ((float) route.elevationAt(i * spaceBetween)));

        int firstValidIndex = firstValid(samples);
        int lastValidIndex = lastValid(samples);
        if (firstValidIndex == -1) return new ElevationProfile(length, new float[nbSamples]);

        Arrays.fill(samples, 0, firstValidIndex, samples[firstValidIndex]);
        Arrays.fill(samples, lastValidIndex, samples.length, samples[lastValidIndex]);

        if (!isNaN(samples[0]) && isNaN(samples[1])) indexes.add(0);

        //Taking the indexes of values surrounding NaNs to facilitate interpolation.
        IntStream.range(1, samples.length - 1).forEach(i -> {
            if (!isNaN(samples[i]) && isNaN(samples[i - 1]) && isNaN(samples[i + 1])) {
                indexes.add(i);
                indexes.add(i);
            } else if (!isNaN(samples[i]) && (isNaN(samples[i - 1])
                    || isNaN(samples[i + 1]))) {
                indexes.add(i);
            }
        });
        if (!isNaN(samples[samples.length - 1]) && isNaN(samples[samples.length - 2])) {
            indexes.add(samples.length - 1);
        }

        //Interpolating
        for (int i = 0; i <= (indexes.size() / 2) - 1; i++) {
            int quantity = indexes.get(2 * i + 1) - indexes.get(2 * i);
            if (quantity == 1) {
                samples[indexes.get(2 * i) + 1] = (float) interpolate(samples[indexes.get(2 * i)],
                        samples[indexes.get(2 * i) + 1], 0.5);
            } else {
                DoubleUnaryOperator func = sampled(new float[]{samples[indexes.get(2 * i)],
                        samples[indexes.get(2 * i + 1)]}, quantity);
                for (int j = 1; j < quantity; j++) {
                    samples[indexes.get(2 * i) + j] = (float) func.applyAsDouble(j);
                }
            }
        }
        return new ElevationProfile(length, samples);
    }

    /**
     * This method allows us to compute the first non-NaN value of an array of float.
     *
     * @param floatArray The array we are looking through.
     * @return The first valid index or -1 if it does not exist.
     */
    private static int firstValid(float[] floatArray) {
        for (int i = 0; i < floatArray.length; i++) {
            if (!isNaN(floatArray[i])) return i;
        }
        return -1;
    }

    /**
     * This method allows us to compute the last non-NaN value of an array of float.
     *
     * @param floatArray The array we are looking through.
     * @return The last valid index or -1 if it does not exist.
     */
    private static int lastValid(float[] floatArray) {
        for (int i = floatArray.length - 1; i >= 0; i--) {
            if (!isNaN(floatArray[i])) return i;
        }
        return -1;
    }

}
