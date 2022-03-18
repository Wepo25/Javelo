package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;


public final class ElevationProfile {

    private static final DoubleSummaryStatistics s = new DoubleSummaryStatistics();
    private final double length;
    private final float[] samples;

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        samples = new float[(1 + (int) Math.ceil(length / 2))];
        System.arraycopy(elevationSamples, 0, samples, 0, 1 + (int) Math.ceil(length / 2));
        for (float sample : samples) {
            s.accept(sample);
        }
    }

    public double length() {
        return this.length;
    }

    public double minElevation() {
        return s.getMin();
    }

    public double maxElevation() {
        return s.getMax();
    }

    public double totalAscent() {
        double ascent = 0;
        for (int i = 1; i < samples.length; i++) {
            if (samples[i - 1] < samples[i]) {
                ascent += Math.abs(samples[i] - samples[i - 1]);
            }
        }
        return ascent;
    }

    public double totalDescent() {
        double descent = 0;
        for (int i = 1; i < samples.length; i++) {
            if (samples[i - 1] > samples[i]) {
                descent += Math.abs(samples[i] - samples[i - 1]);
            }
        }
        return descent;
    }

    public double elevationAt(double position) {
        if (length < position) return samples[samples.length-1];
        if (position < 0) return samples[0];
        return Functions.sampled(samples, length).applyAsDouble(position);
    }


}





