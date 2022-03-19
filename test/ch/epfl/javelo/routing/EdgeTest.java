package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {
    private static final double DELTA = 1e-6;


    @Test
    void positionClosestTo() {
        Edge edge1 = edgeBuilder();
        var expected1 = Math2.projectionLength(2635000, 1115000, 2640000, 1118000, 2637000, 1116000);
        PointCh point1 = new PointCh(2637000,1116000);
        assertEquals(expected1, edge1.positionClosestTo(new PointCh(2637000, 1116000)));
        var expected2 = Math2.projectionLength(2635000, 1115000, 2640000, 1118000, 2635700, 1117800);
        assertEquals(expected2, edge1.positionClosestTo(new PointCh(2635700, 1117800)));
    }

    @Test
    void pointAt() {
        Edge edge1 = edgeBuilder();
        var expected1 = new PointCh(2635000, 1115000);
        var actual1 = edge1.pointAt(0);
        assertEquals(expected1.e(), actual1.e(), DELTA);
        assertEquals(expected1.n(), actual1.n(), DELTA);
        var expected2 = new PointCh(2640000, 1118000);
        var actual2 = edge1.pointAt(edge1.length());
        assertEquals(expected2.e(), actual2.e(), DELTA);
        assertEquals(expected2.n(), actual2.n(), DELTA);
        var expected3 = new PointCh(2637500, 1116500);
        var actual3 = edge1.pointAt(2915.475948);
        assertEquals(expected3.e(), actual3.e(), DELTA);
        assertEquals(expected3.n(), actual3.n(), DELTA);
    }

    @Test
    void elevationAt() {
        Edge edge1 = edgeBuilder();

    }

    private Edge edgeBuilder() {
        double length = 5830.951895;
        DoubleUnaryOperator profile = Functions.sampled(new float[] {350.0625f, 325.125f, 287.25f, 310.0625f, 360.125f, 412.25f, 369f}, length);
        return new Edge(4360, 4361, new PointCh(2635000, 1115000), new PointCh(2640000, 1118000), length, profile);
    }
    private final static double E_MIN_LAUSANNE = 2520000;
    private final static double N_MIN_LAUSANNE = 1145000;



    @Test
    void positionClosestToDestinationPointGraphBasedTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        Edge edge = Edge.of(graph,263615,123567,123566);

        assertEquals(edge.length(),edge.positionClosestTo(graph.nodePoint(123566)),1);
    }


    @Test
    void positionClosestToOwnPointGraphBasedTest() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        Edge edge = Edge.of(graph,263615,123567,123566);
        assertEquals(0.0,edge.positionClosestTo(graph.nodePoint(123567)));
    }
    @Test
    void positionClosestTo2() {
        PointCh from = new PointCh(E_MIN_LAUSANNE, N_MIN_LAUSANNE);
        PointCh to = new PointCh(E_MIN_LAUSANNE + 1000, N_MIN_LAUSANNE);
        PointCh inBetweenPoint = new PointCh(E_MIN_LAUSANNE + 99, N_MIN_LAUSANNE);
        PointCh logicalPoint = new PointCh(E_MIN_LAUSANNE + 99, N_MIN_LAUSANNE + 566);

        Edge edge = new Edge(0, 0, from, to, 1000, null);

        assertEquals(99,edge.positionClosestTo(inBetweenPoint));
        assertEquals(99,edge.positionClosestTo(logicalPoint));
    }


    @Test
    void pointAtGraphBasedTest() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        Edge edge = Edge.of(graph,263615,123567,123566);
        PointCh point  = edge.pointAt(Math.sqrt(graph.nodePoint(123566).squaredDistanceTo(graph.nodePoint(123567))));
        assertEquals(graph.nodePoint(123566).e(),point.e(),0.2);
        assertEquals(graph.nodePoint(123566).n(),point.n(),0.2);
    }

    @Test
    void elevationAtTest() throws IOException {
        float[] expectedSamples = new float[]{1f, 2f, 3f, 2f, 2.333333f, 5f, 6f, 10f, 9f, 2f,2.3334f};
        DoubleUnaryOperator doubleUnaryOperator = Functions.sampled(expectedSamples, 10);


        Edge edge = new Edge(0, 0, null, null, 10, doubleUnaryOperator);

        assertEquals(1f, edge.elevationAt(0));
        assertEquals(2.3334f, edge.elevationAt(11));
        assertEquals(2f, edge.elevationAt(1));
        assertEquals(3f, edge.elevationAt(2));
        assertEquals(2f, edge.elevationAt(3));
        assertEquals(2.333333f, edge.elevationAt(4));
        assertEquals(5.5f, edge.elevationAt(5.5));


    }}