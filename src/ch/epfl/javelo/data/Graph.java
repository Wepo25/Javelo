package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * A Buffer containing multiple data of a map.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Graph {

    /**
     * The path to the file containing the nodes' data.
     */
    private static final String NODES_PATH = "nodes.bin";
    /**
     * The path to the file containing the sectors' data.
     */
    private static final String SECTORS_PATH = "sectors.bin";
    /**
     * The path to the file containing the edges' data.
     */
    private static final String EDGES_PATH = "edges.bin";
    /**
     * The path to the file containing the profiles' data.
     */
    private static final String PROFILES_PATH = "profile_ids.bin";
    /**
     * The path to the file containing the elevations' data.
     */
    private static final String ELEVATIONS_PATH = "elevations.bin";
    /**
     * The path to the file containing the attributes' data.
     */
    private static final String ATTRIBUTES_PATH = "attributes.bin";

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * This method is the constructor of the class Graph.
     *
     * @param nodes         - GraphNodes : A buffer of nodes contained in an area.
     * @param sectors       - GraphSectors : A buffer of sectors contained in an area.
     * @param edges         - GraphEdges : A buffer of edges contained in an area.
     * @param attributeSets - List<AttributeSet> : A list of AttributeSets.
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * This method allows us to easily read data from a file and to extract, if they exist, a buffer of nodes,
     * a buffer of sectors, a buffer of edges, a buffer of profileIds, a buffer of elevations and a buffer of attributes.
     *
     * @param basePath - Path : The file's path.
     * @return - Graph : A graph composed of the extracted data.
     * @throws IOException : Throws an exception if it was unable to open the given file.
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        IntBuffer nodes = tryAndOpen(basePath.resolve(NODES_PATH)).asIntBuffer();
        ByteBuffer sectors = tryAndOpen(basePath.resolve(SECTORS_PATH));
        ByteBuffer edges = tryAndOpen(basePath.resolve(EDGES_PATH));
        IntBuffer profileIds = tryAndOpen(basePath.resolve(PROFILES_PATH)).asIntBuffer();
        ShortBuffer elevations = tryAndOpen(basePath.resolve(ELEVATIONS_PATH)).asShortBuffer();
        LongBuffer attributes = tryAndOpen(basePath.resolve(ATTRIBUTES_PATH)).asLongBuffer();
        List<AttributeSet> attributeSets = new ArrayList<>(attributes.capacity());
        for (int i = 0; i < attributes.capacity(); i++) {
            attributeSets.add(new AttributeSet(attributes.get(i)));
        }
        return new Graph(new GraphNodes(nodes),
                new GraphSectors(sectors),
                new GraphEdges(edges, profileIds, elevations), attributeSets);
    }

    /**
     * This private method allows us to open a given file while checking if it can access it.
     *
     * @param path - Path : The file's path.
     * @return - MappedByteBuffer : A mapped ByteBuffer containing the file's data.
     * @throws IOException . Throws an exception if it was unable to open the given file.
     */
    private static ByteBuffer tryAndOpen(Path path) throws IOException {
        try (FileChannel channel = FileChannel.open(path)) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    /**
     * This method allows us to know the number of nodes in this graph.
     *
     * @return - int : The number of nodes in this graph.
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * This method allows us to get the position of the given node.
     *
     * @param nodeId - int : The identity of the node.
     * @return - PointCh : The position of the given node.
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * This method allows us to get the number of leaving edges of the given node.
     *
     * @param nodeId - int : The identity of the node.
     * @return int : The number of leaving edges of this node.
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * This method allows us to get the global index of a leaving edges with the id of its node and
     * its index in the list of edges leaving this specific node.
     *
     * @param nodeId    - int : The identity of the node.
     * @param edgeIndex - int : The index of the edges in a list consisting only of edges leaving this specific node.
     * @return - int : The global index of the given edge.
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * This method allows us to determine the closest node to a given point at a given distance.
     *
     * @param point          - PointCh : The point from which we're trying to determine the closest node.
     * @param searchDistance - double : The search distance.
     * @return - int : The identity of the closest node to the given point, or -1 if no nodes satisfy criteria.
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        int closestNodeId = -1;
        double newSearchDistance = searchDistance * searchDistance;
        List<GraphSectors.Sector> temp = sectors.sectorsInArea(point, searchDistance);
        for (GraphSectors.Sector sect : temp) {
            for (int i = sect.startNodeId(); i < sect.endNodeId(); ++i) {
                double tempDistance = point.squaredDistanceTo(nodePoint(i));
                if ((tempDistance <= newSearchDistance)) {
                    newSearchDistance = tempDistance;
                    closestNodeId = i;
                }
            }
        }
        return closestNodeId;
    }

    /**
     * This method allows us to get the index of the node targeted by the given edge.
     *
     * @param edgeId - int : The id of the edge.
     * @return - int : The index of the node targeted by the given edge.
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * This method allows us to determine whether this edge is inverted or not.
     *
     * @param edgeId - int : The identity of the edge.
     * @return - boolean : True iff the edge is facing the opposite direction as the node it comes from.
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * This method allows us to get the set of attributes corresponding to the given edge.
     *
     * @param edgeId - int : The identity of the edge.
     * @return - AttributeSet : A set of the attributes corresponding to the given edge.
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return new AttributeSet(attributeSets.get(edges.attributesIndex(edgeId)).bits());
    }

    /**
     * This method allows us to get an edge's length.
     *
     * @param edgeId : The identity of the edge.
     * @return - double : The length of the given edge.
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * This method allows us to get the elevation of a given edge.
     *
     * @param edgeId - int : The identity of the edge.
     * @return - double : The elevation of the given edge.
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * This method allows us to compute the profile of a given edge.
     *
     * @param edgeId - int : The identity of the edge.
     * @return - DoubleUnaryOperator : The profile of an edge represented as a function.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return (!edges.hasProfile(edgeId)) ?
                Functions.constant(Double.NaN) :
                Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
    }
}