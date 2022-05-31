package ch.epfl.javelo.data;

import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asDouble;
import java.nio.IntBuffer;

/**
 * A Buffer containing specific data of a Graph. Here nodes.
 *
 * @param buffer - IntBuffer : Buffer containing nodes attributes.
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public record GraphNodes(IntBuffer buffer) {

    /**
     * The Offset to access the East coordinate.
     */
    private static final int OFFSET_E = 0;

    /**
     * The Offset to access the North coordinate.
     */
    private static final int OFFSET_N = OFFSET_E + 1;

    /**
     * The Offset to access the number of leaving edges from a node.
     */
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;

    /**
     * The capacity taken to represent a node inside the buffer.
     */
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int OUT_DEGREE_INDEX = 28;

    private static final int OUT_DEGREE_LENGTH = 4;

    private static final int EDGE_ID_INDEX = 0;

    private static final int EDGE_ID_LENGTH = 28;

    /**
     * This method allows us to compute the number of nodes contained inside the buffer.
     *
     * @return - int : The total number of nodes contained in an object GraphNodes.
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * This method allows us to determine the east coordinate of a given node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - double : The coordinate E of the node.
     */
    public double nodeE(int nodeId) {
        return asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * This method allows us to determine the north coordinate of a given node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - double : The coordinate N of the node.
     */
    public double nodeN(int nodeId) {
        return asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * This method allows us to determine the number of leaving edge of a specific node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - int : The number of edges leaving this node.
     */
    public int outDegree(int nodeId) {
        int idEdge = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return extractUnsigned(idEdge, OUT_DEGREE_INDEX, OUT_DEGREE_LENGTH);
    }

    /**
     * This method allows us to get the global index of a leaving edges with the id of its node and
     * its index in the list of edges leaving this specific node.
     *
     * @param nodeId    - int : The identity of the node.
     * @param edgeIndex - int : The index of the edges in a list consisting only of edges leaving this specific node.
     * @return - int : The global index of the given edge.
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int idEdge = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return extractUnsigned(idEdge, EDGE_ID_INDEX, EDGE_ID_LENGTH) + edgeIndex;
    }

}
