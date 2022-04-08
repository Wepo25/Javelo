package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static ch.epfl.javelo.Bits.extractSigned;
import static ch.epfl.javelo.Bits.extractUnsigned;
import static java.lang.Short.toUnsignedInt;

/**
 * A Buffer containing specific data of a Graph. Here edges.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 *
 * @param edgesBuffer - ByteBuffer : Buffer containing the orientations of the edges,
 *                    the identity of the destination node, the length,
 *                    the positive height difference and the identity of OSM attributes.
 * @param profileIds - IntBuffer : Buffer containing the type of profile of the edge and
 *                   the identity of the first profile's sample
 * @param elevations - ShortBuffer : Buffer containing different profile's type :not existent,
 *                  none compressed, compressed Q4.4, compressed Q0.4.
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     * The capacity taken to represent an edge inside the buffer.
     */
    private static final int EDGES_INTS = 10;

    /**
     * The OFFSET needed to reach the length of an edge.
     */
    private static final int OFFSET_LENGTH = 4;

    /**
     * The OFFSET needed to reach the elevation of an edge.
     */
    private static final int OFFSET_ELEVATION = 6;

    /**
     * The OFFSET needed to reach the AttributeSet of an edge.
     */
    private static final int OFFSET_ATTRIBUTE = 8;


    /**
     * This method allows us to know if an edge goes in the same direction as the OMS path it comes from.
     *
     * @param edgeId - int : The ID (or position) of the edge inside edgesBuffer.
     * @return - boolean : True if the edge goes in the opposite way of the OMS path it comes from.
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(edgeId * EDGES_INTS) < 0;
    }

    /**
     * This method allows us to know which node a given edge is targeting.
     *
     * @param edgeId - int : The ID (or position) of the edge inside edgesBuffer.
     * @return - int : The ID of the destination node of the given edge.
     */

    public int targetNodeId(int edgeId) {
        return isInverted(edgeId) ? ~edgesBuffer.getInt((edgeId * EDGES_INTS)) : edgesBuffer.getInt((edgeId * EDGES_INTS));
    }

    /**
     * This method allows us to know the length of a given edge.
     *
     * @param edgeId - int : The ID (or position) of the edge inside edgesBuffer.
     * @return - double : The length in meter of the given edge.
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_LENGTH)));
    }

    /**
     * This method allows us to know the elevation gain of a given edge.
     *
     * @param edgeId - int : The ID (or position) of the edge inside edgesBuffer.
     * @return - double : The positive difference in altitude of the given edge.
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort((edgeId * EDGES_INTS + OFFSET_ELEVATION))));
    }

    /**
     * This method allows us to know whether a given edge has a profile.
     *
     * @param edgeId - int : The ID (or position) of the edge inside profileIds.
     * @return - boolean : True iff the given edge possesses a profile (different from 0).
     */
    public boolean hasProfile(int edgeId) {
        return extractUnsigned(profileIds.get(edgeId), 30, 2) != 0;
    }

    /**
     * This method allows us to access a given edge's height samples no matter its profile type.
     *
     * @param edgeId - int : The ID (or position) of the edge inside profileIds.
     * @return - float[] : An array of floats consisting of the different heights of a given edge.
     */
    public float[] profileSamples(int edgeId) {
        int pf = extractUnsigned(profileIds.get(edgeId), 30, 2);
        int sampleId = extractUnsigned(profileIds.get(edgeId), 0, 29);
        int quantity = 1 + (int) Math.ceil(length(edgeId) / 2);
        float[] samples = new float[quantity];
        if (pf == 0) {
            return new float[0];
        } else if (pf == 1) {
            for (int i = sampleId, index = 0; i < sampleId + quantity; i++, index++) {
                samples[index] = Math.scalb(toUnsignedInt(elevations.get(i)), -4);
            }
        } else {
            samples[0] = Math.scalb(toUnsignedInt(elevations.get(sampleId)), -4);
            for (int i = sampleId + 1, j = 0, index = 1; i <= (sampleId + Math2.ceilDiv(quantity - 1, ((pf == 2) ? 2 : 4)));
                 i = ((((pf == 2) ? 1 : 3) - j % ((pf == 2) ? 2 : 4)) == 0) ? i + 1 : i, j++, index++) {
                samples[index] = samples[index - 1] + Math.scalb(extractSigned(elevations.get(i),
                        ((pf == 2) ? 8 : 4) * (((pf == 2) ? 1 : 3) - j % ((pf == 2) ? 2 : 4)), (pf == 2) ? 8 : 4), -4);
                if (index == samples.length - 1) {
                    break;
                }
            }
        }
        return (isInverted(edgeId)) ? reverse(samples) : samples;
    }

    /**
     * This method allows us to find the ID of a set of attribute given an edge.
     *
     * @param edgeId - int : The ID (or position) of the edge inside edgesBuffer.
     * @return - int : The ID of the set of attributes of the given edge.
     */
    public int attributesIndex(int edgeId) {
        return toUnsignedInt(edgesBuffer.getShort((edgeId * EDGES_INTS + OFFSET_ATTRIBUTE)));
    }

    /**
     * This method allows us to easily reverse a float array which is useful when using the profileSamples method.
     *
     * @param a - float[] : An array of floats.
     * @return - float[] : The given float array but reversed.
     */
    private float[] reverse(float[] a) {
        float[] temp = new float[a.length];
        for (int i = a.length - 1; i >= 0; --i) {
            temp[(a.length - 1) - i] = a[i];
        }
        return temp;
    }
}
