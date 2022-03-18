package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

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
}