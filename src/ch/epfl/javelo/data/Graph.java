package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
        this.attributeSets = attributeSets;
    }

    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path profilePath = basePath.resolve("profile_ids.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        Path attributesPath = basePath.resolve("attributes.bin");
        IntBuffer nodes;
        ByteBuffer sectors;
        ByteBuffer edges;
        IntBuffer profileIds;
        ShortBuffer elevations;
        LongBuffer attributes;
        List<AttributeSet> attributeSets = new ArrayList<>();
        try (FileChannel channel = FileChannel.open(nodesPath)) {
            nodes = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(sectorsPath)) {
            sectors = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }
        try (FileChannel channel = FileChannel.open(edgesPath)) {
            edges = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }
        try (FileChannel channel = FileChannel.open(profilePath)) {
            profileIds = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(elevationsPath)) {
            elevations = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asShortBuffer();
        }
        try (FileChannel channel = FileChannel.open(attributesPath)) {
            attributes = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        for (int i = 0; i < attributes.capacity(); i++) {
            attributeSets.add(new AttributeSet(attributes.get(i)));
        }
        return new Graph(new GraphNodes(nodes), new GraphSectors(sectors), new GraphEdges(edges, profileIds, elevations), attributeSets);

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
        double distance = Math2.squaredNorm(SwissBounds.MAX_E - SwissBounds.MIN_E, SwissBounds.MAX_N - SwissBounds.MIN_N);
        List<GraphSectors.Sector> temp = sectors.sectorsInArea(point, searchDistance);
        for (GraphSectors.Sector sect : temp) {
            for (int i = sect.startNodeId(); i < sect.endNodeId(); ++i) {
                double tempDistance = point.squaredDistanceTo(new PointCh(nodes.nodeE(i), nodes.nodeN(i)));
                if ((tempDistance < distance)) {
                    distance = tempDistance;
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
        return new AttributeSet(attributeSets.get(edges.attributesIndex(edgeId)).bits());
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

