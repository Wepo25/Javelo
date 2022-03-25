package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    public static Graph loadFrom(Path basePath) throws IOException {
        IntBuffer nodes = tryAndOpen(basePath.resolve("nodes.bin")).asIntBuffer();
        ByteBuffer sectors = tryAndOpen(basePath.resolve("sectors.bin")).asReadOnlyBuffer();
        ByteBuffer edges = tryAndOpen(basePath.resolve("edges.bin")).asReadOnlyBuffer();
        IntBuffer profileIds = tryAndOpen(basePath.resolve("profile_ids.bin")).asIntBuffer();
        ShortBuffer elevations = tryAndOpen(basePath.resolve("elevations.bin")).asShortBuffer();
        LongBuffer attributes = tryAndOpen(basePath.resolve("attributes.bin")).asLongBuffer();
        List<AttributeSet> attributeSets = new ArrayList<>();
        for (int i = 0; i < attributes.capacity(); i++) {
            attributeSets.add(new AttributeSet(attributes.get(i)));
        }
        return new Graph(new GraphNodes(nodes), new GraphSectors(sectors), new GraphEdges(edges, profileIds, elevations), attributeSets);

    }

    private static MappedByteBuffer tryAndOpen(Path path) throws IOException {
        try(FileChannel channel = FileChannel.open(path)){
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    public int nodeCount() {
        return nodes.count();
    }

    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance) {
        int closestNodeId = -1;
        double NewSearchDistance = searchDistance*searchDistance ;
        List<GraphSectors.Sector> temp = sectors.sectorsInArea(point, searchDistance);
        for (GraphSectors.Sector sect : temp) {
            for (int i = sect.startNodeId(); i < sect.endNodeId(); ++i) {
                double tempDistance = point.squaredDistanceTo(nodePoint(i));
                if ((tempDistance <= NewSearchDistance)) {
                    NewSearchDistance = tempDistance;
                    closestNodeId = i;
                }
            }
        }
        return closestNodeId;
    }

    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (!edges.hasProfile(edgeId)) {
            return Functions.constant(Double.NaN);
        } else
            return Functions.sampled(edges.profileSamples(edgeId), this.edges.length(edgeId));
    }
}