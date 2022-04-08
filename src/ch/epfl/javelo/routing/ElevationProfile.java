package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * A Profile representing a sequence of elevation.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class ElevationProfile {

    private final DoubleSummaryStatistics s = new DoubleSummaryStatistics();
    private final double length;
    private final float[] samples;

    /**
     * This method is the constructor of the ElevationProfile class.
     *
     * @param length           - double : The length of our sequence of elevations.
     * @param elevationSamples - float[] : The sequence of elevations.
     * @throws IllegalArgumentException (checkArgument) : Throws an exception if the length is negative or the
     *                                  sequence of elevations contain less than two elements.
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.samples = elevationSamples;
        for (float sample : samples) {
            s.accept(sample);
        }
    }

    /**
     * This method allows us to get the length among which this elevation sequence spreads.
     *
     * @return - double : The length of the path that this elevation sequence represents.
     */
    public double length() {
        return this.length;
    }

    /**
     * This method allows us to compute the lowest elevation of the path corresponding to this sequence of elevations.
     *
     * @return - double : The minimal elevation.
     */
    public double minElevation() {
        return s.getMin();
    }

    /**
     * This method allows us to compute the highest elevation of the path corresponding to this sequence of elevations.
     *
     * @return - double : The maximal elevation.
     */
    public double maxElevation() {
        return s.getMax();
    }

    /**
     * This method allows us to compute the sum of each altitude gain we face being on this path.
     *
     * @return - double : The total ascent of the path corresponding to this sequence of elevations.
     */
    public double totalAscent() {
        double ascent = 0;
        for (int i = 0; i < samples.length - 1; i++) {
            if (samples[i + 1] - samples[i] > 0) {
                ascent += samples[i + 1] - samples[i];
            }
        }
        return Math.abs(ascent);
    }

    /**
     * This method allows us to compute the sum of each altitude loss we face being on this path.
     *
     * @return - double : The total descent of the path corresponding to this sequence of elevations.
     *
     */
    public double totalDescent() {
        double descent = 0;
        for (int i = 0; i < samples.length - 1; i++) {
            if (samples[i + 1] - samples[i] < 0) {
                descent += samples[i + 1] - samples[i];
            }
        }
        return Math.abs(descent);
    }

    /**
     * This method allows us to get the elevation at a given position.
     *
     * @param position - double : The position at which we are computing the elevation.
     * @return - double : The elevation at the given position.
     */
    public double elevationAt(double position) {
            return Functions.sampled(samples, length).applyAsDouble(position);
    }


}





