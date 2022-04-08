package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * A Buffer containing specific data of a Graph. Here nodes.
 *
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

    /**
     * This method allows us to compute the number of nodes contained inside the buffer.
     *
     * @return - int : The total number of nodes contained in an object GraphNodes.
     */
    public int count() {
        return buffer.capacity() / 3;
    }

    /**
     * This method allows us to determine the east coordinate of a given node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - double : The coordinate E of the node.
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * This method allows us to determine the north coordinate of a given node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - double : The coordinate N of the node.
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * This method allows us to determine the number of leaving edge of a specific node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - int : The number of edges leaving this node.
     */
    public int outDegree(int nodeId) {
        int idEdge = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(idEdge, 28, 4);
    }

    /**
     * This method allows us to get the global index of a leaving edges with the id of its node and
     * its index in the list of edges leaving this specific node.
     *
     * @param nodeId - int : The identity of the node.
     * @param edgeIndex - int : The index of the edges in a list consisting only of edges leaving this specific node.
     * @return - int : The global index of the given edge.
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int idEdge = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(idEdge, 0, 28) + edgeIndex;
    }

}
