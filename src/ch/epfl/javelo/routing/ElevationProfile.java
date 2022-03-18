package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;


public final class ElevationProfile {

    private final DoubleSummaryStatistics s = new DoubleSummaryStatistics();
    private final double length;
    private final float[] samples;

    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.samples = elevationSamples;
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
        double ascent= 0;
        for (int i = 0; i < samples.length-1; i++) {
            if (samples[i+1]- samples[i] > 0) {
                ascent += samples[i+1]- samples[i];
            }
        }
        return Math.abs(ascent);
    }

    public double totalDescent() {
        double descent = 0;
        for (int i = 0; i < samples.length-1; i++) {
            if (samples[i+1]- samples[i] <0) {
                descent += samples[i+1]- samples[i];
            }
        }
        return Math.abs(descent);
    }

    public double elevationAt(double position) {
        if (length < position) return samples[samples.length-1];
        if (position < 0) return samples[0];
        return Functions.sampled(samples, length).applyAsDouble(position);
    }


}





