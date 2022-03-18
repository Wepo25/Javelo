package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {
    private static final double DISTANCE_ERROR = 0.1; //10cm d'erreur autorisée

    @Test
    void loadFromTest(){
        Path basePath = Path.of("lausanne");
        try{ Graph graph = Graph.loadFrom(basePath); }
        catch (IOException e){
            fail(); }

        Path falseBasePath = Path.of("bonjour");
        assertThrows(IOException.class, ()->{
            Graph graph = Graph.loadFrom(falseBasePath);
        });
    }

    @Test
    void nodePointTest(){
        Graph graph = generateLausanneGraph();

        int nodeId1 = 0; // 1684019323
        int nodeId2 = 123567; // 3761311896
        //attention il faut faire -1 pour pas de "out of bounds" exception
        int nodeId3 = graph.nodeCount() -1; // 5475839472
        int nodeOut = graph.nodeCount();

        PointCh node1 = new PointCh(Ch1903.e(Math.toRadians(6.7761194), Math.toRadians(46.6455770)), Ch1903.n(Math.toRadians(6.7761194), Math.toRadians(46.6455770)));
        PointCh node2 = new PointCh(Ch1903.e(Math.toRadians(6.6291292), Math.toRadians(46.5235985)), Ch1903.n(Math.toRadians(6.6291292), Math.toRadians(46.5235985)));
        PointCh node3 = new PointCh(Ch1903.e(Math.toRadians(6.4789731), Math.toRadians(46.6422279)), Ch1903.n(Math.toRadians(6.4789731), Math.toRadians(46.6422279)));

        assertEquals(node1.e(), graph.nodePoint(nodeId1).e(), DISTANCE_ERROR);
        assertEquals(node1.n(), graph.nodePoint(nodeId1).n(), DISTANCE_ERROR);
        assertEquals(node2.e(), graph.nodePoint(nodeId2).e(), DISTANCE_ERROR);
        assertEquals(node2.n(), graph.nodePoint(nodeId2).n(), DISTANCE_ERROR);
        assertEquals(node3.e(), graph.nodePoint(nodeId3).e(), DISTANCE_ERROR);
        assertEquals(node3.n(), graph.nodePoint(nodeId3).n(), DISTANCE_ERROR);

        assertThrows(IndexOutOfBoundsException.class, ()->{graph.nodePoint(nodeOut);});
    }

    @Test
    void nodeOutDegreeTest(){
        Graph graph = generateLausanneGraph();

        int nodeId1 = 200000; // 4364284737
        int nodeId2 = 123567; // 3761311896
        int nodeId3 = 189954; // 3107538389

        assertEquals(2, graph.nodeOutDegree(nodeId1));
        assertEquals(3, graph.nodeOutDegree(nodeId2));
        assertEquals(2, graph.nodeOutDegree(nodeId3));
    }

    @Test
    void nodeClosestToTest(){
        Graph graph = generateLausanneGraph();

        int nodeId1 = 0; // 1684019323
        int nodeId2 = 123_567; // 3761311896
        int nodeId3 = graph.nodeCount() -1; // 5475839472

        PointCh node1 = new PointCh(Ch1903.e(Math.toRadians(6.7761194), Math.toRadians(46.6455770)), Ch1903.n(Math.toRadians(6.7761194), Math.toRadians(46.6455770)));
        PointCh node2 = new PointCh(Ch1903.e(Math.toRadians(6.6291292), Math.toRadians(46.5235985)), Ch1903.n(Math.toRadians(6.6291292), Math.toRadians(46.5235985)));
        PointCh node3 = new PointCh(Ch1903.e(Math.toRadians(6.4789731), Math.toRadians(46.6422279)), Ch1903.n(Math.toRadians(6.4789731), Math.toRadians(46.6422279)));

        PointCh pointNearNode1 = new PointCh(node1.e() - 20, node1.n());

        assertEquals(nodeId1, graph.nodeClosestTo(node1, 100));
        assertEquals(nodeId2, graph.nodeClosestTo(node2, 100));
        assertEquals(nodeId3, graph.nodeClosestTo(node3, 100));
        assertEquals(nodeId1, graph.nodeClosestTo(pointNearNode1, 20));
        assertEquals(-1, graph.nodeClosestTo(pointNearNode1, 19));
    }

    @Test
    void generalEdgesTest(){
        Graph graph = generateLausanneGraph();

        int nodeId1 = 123_567; // 3761311896
        int edgeId1 = graph.nodeOutEdgeId(nodeId1, 0);
        int edgeId2 = graph.nodeOutEdgeId(nodeId1, 1);
        int edgeId3 = graph.nodeOutEdgeId(nodeId1, 2);

        assertEquals(123_566, graph.edgeTargetNodeId(edgeId1)); //c'est un test artificiel...
        assertEquals(123_565, graph.edgeTargetNodeId(edgeId2));
        assertEquals(123_542, graph.edgeTargetNodeId(edgeId3));

        //attributes
        AttributeSet a2 = AttributeSet.of(Attribute.BICYCLE_YES, Attribute.HIGHWAY_PEDESTRIAN, Attribute.SURFACE_ASPHALT);
        AttributeSet A2 = graph.edgeAttributes(edgeId2);
        assertEquals(a2.bits(), A2.bits());

        AttributeSet a3 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        AttributeSet A3 = graph.edgeAttributes(edgeId3);
        assertEquals(a3.bits(), A3.bits());

        //edgeLength
        double distance2 = graph.nodePoint(123_565).distanceTo(graph.nodePoint(nodeId1));
        double distance3 = graph.nodePoint(123_542).distanceTo(graph.nodePoint(nodeId1));
        assertEquals(distance2, graph.edgeLength(edgeId2), DISTANCE_ERROR);
        assertEquals(distance3, graph.edgeLength(edgeId3), DISTANCE_ERROR);

        //edgeIsInverted... je vois pas comment faire mieux
        int edgeId2Inv = graph.nodeOutEdgeId(123_565, 0);
        int edgeId3Inv = graph.nodeOutEdgeId(123_542, 0);

        assertEquals(graph.edgeIsInverted(edgeId2), !graph.edgeIsInverted(edgeId2Inv));
        assertEquals(graph.edgeIsInverted(edgeId3), !graph.edgeIsInverted(edgeId3Inv));

        //on ne peut pas tester graph.edgeElevationGain(...) et graph.edgeProfile(...)
    }

    private Graph generateLausanneGraph(){
        Path basePath = Path.of("lausanne");
        Graph graph = null;
        try{ graph = Graph.loadFrom(basePath);}
        catch (IOException e){
            fail();}
        return graph;
    }

    private void getMapNodeId(int nodeId){
        Graph graph = generateLausanneGraph();

        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer = null;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
        } catch (Exception e){ fail(); }

        System.out.println("nodeID: " + osmIdBuffer.get(nodeId));
    }
}
