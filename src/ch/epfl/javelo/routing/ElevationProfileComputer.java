package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class ElevationProfileComputer {

    private ElevationProfileComputer() {
    }

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int nbSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double length = route.length();
        double spaceBetween = length / (nbSamples - 1);
        float[] samples = new float[nbSamples];
        List<Integer> indexes = new ArrayList<>();


        for (int i = 0; i < nbSamples; i++) {
            samples[i] = ((float) route.elevationAt(i * spaceBetween));
        }
        if (firstValid(samples) == -1 || lastValid(samples) == -1) {
            return new ElevationProfile(length, new float[nbSamples]);
        }
        Arrays.fill(samples, 0, firstValid(samples), samples[firstValid(samples)]);
        Arrays.fill(samples, lastValid(samples), samples.length, samples[lastValid(samples)]);
        if (!Float.isNaN(samples[0]) && Float.isNaN(samples[1])) {
            indexes.add(0);
        }
        for (int i = 1; i < samples.length - 1; i++) {
            if (!Float.isNaN(samples[i]) && Float.isNaN(samples[i - 1]) && Float.isNaN(samples[i + 1])) {
                indexes.add(i);
                indexes.add(i);
            } else if (!Float.isNaN(samples[i]) && (Float.isNaN(samples[i - 1]) || Float.isNaN(samples[i + 1]))) {
                indexes.add(i);
            }
        }
        if (!Float.isNaN(samples[samples.length - 1]) && Float.isNaN(samples[samples.length - 2])) {
            indexes.add(samples.length - 1);
        }

        for (int i = 0; i <= (indexes.size() / 2) - 1; i++) {
            int quantity = indexes.get(2 * i + 1) - indexes.get(2 * i);
            DoubleUnaryOperator func = Functions.sampled(new float[]{samples[indexes.get(2 * i)], samples[indexes.get(2 * i + 1)]}, quantity);
            for (int j = 1; j < quantity; j++) {
                samples[indexes.get(2 * i) + j] = (float) func.applyAsDouble(j);
            }

        }
        return new ElevationProfile(length, samples);
    }

    private static int firstValid(float[] s) {
        for (int i = 0; i < s.length; i++) {
            if (!Float.isNaN(s[i])) {
                return i;
            }
        }
        return -1;
    }

    private static int lastValid(float[] s) {
        for (int i = s.length - 1; i >= 0; i--) {
            if (!Float.isNaN(s[i])) {
                return i;
            }
        }
        return -1;
    }

}
