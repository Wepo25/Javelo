package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * @return the total number of nodes contained in an object GraphNodes
     */
    public int count() {
        return buffer.capacity() / 3;
    }

    /**
     * @param nodeId the identity of the node.
     * @return the coordinate E of the node.
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * @param nodeId the identity of the node.
     * @return the coordinate N of the node.
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * @param nodeId the identity of the node.
     * @return the number of edges leaving this node.
     */
    public int outDegree(int nodeId) {
        int idEdge = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(idEdge, 28, 4);
    }

    /**
     * @param nodeId    the identity of the node
     * @param edgeIndex the index of the edge we want to know the identity.
     * @return the identity of the edge of index edgeIndex leaving the node.
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int idEdge = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(idEdge, 0, 28) + edgeIndex;
    }

}
