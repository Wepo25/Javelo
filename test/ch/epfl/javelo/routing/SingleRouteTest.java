package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

    public class SingleRouteTest {

        private List<Edge> edgesEmpty = new ArrayList<>();

        private PointCh point0 = new PointCh(2500000, 1075000);
        private PointCh point1 = new PointCh(2550000, 1100000);
        private PointCh point2 = new PointCh(2600000, 1125000);
        private PointCh point3 = new PointCh(2650000, 1150000);
        private PointCh point4 = new PointCh(2700000, 1175000);

        private Edge edge0 = new Edge(0, 1, point0, point1, 1000, Functions.constant(5));
        private Edge edge1 = new Edge(1, 2, point1, point2, 2000, Functions.constant(5));
        private Edge edge2 = new Edge(2, 3, point2, point3, 2000, Functions.constant(5));
        private Edge edge3 = new Edge(3, 4, point3, point4, 1000, Functions.constant(5));

        private List<Edge> edges = new ArrayList<Edge>();
        private List<PointCh> points = new ArrayList<>();

        private SingleRoute test;

        public SingleRouteTest() {
            edges.add(edge0);
            edges.add(edge1);
            edges.add(edge2);
            edges.add(edge3);

            points.add(point0);
            points.add(point1);
            points.add(point2);
            points.add(point3);
            points.add(point4);

            test = new SingleRoute(edges);
        }

        @Test
        void constructorThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                new SingleRoute(edgesEmpty);
            });
        }

        @Test
        void edgesCorrect() {
            assertEquals(edges, test.edges());
        }

        @Test
        void summedUpTableAndLengthCorrect() {
            double[] data = new double[] {0, 1000, 3000, 5000, 6000};

            for (int i = 0; i < data.length; i++) {
               // assertEquals(data[i], test.summedUpTable()[i]);
            }

            assertEquals(6000, test.length());
        }

        @Test
        void pointsCorrect() {
            for (int i = 0; i < points.size(); i++) {
                assertEquals(points.get(i), test.points().get(i));
            }
        }

        @Test
        void pointAtCorrect() {
            assertEquals(edge0.pointAt(0), test.pointAt(-2));
            assertEquals(edge0.pointAt(0), test.pointAt(0));
            assertEquals(edge0.pointAt(500), test.pointAt(500));
            assertEquals(edge0.pointAt(999.2), test.pointAt(999.2));
            assertEquals(edge1.pointAt(0), test.pointAt(1000));
            assertEquals(edge2.pointAt(1500), test.pointAt(4500));
            assertEquals(edge2.pointAt(1999), test.pointAt(4999));
            assertEquals(edge3.pointAt(1000), test.pointAt(6000));
            assertEquals(edge3.pointAt(1000), test.pointAt(6999));
        }

        @Test
        void elevationAtCorrect() {
            assertEquals(edge0.elevationAt(0), test.elevationAt(-2));
            assertEquals(edge0.elevationAt(0), test.elevationAt(0));
            assertEquals(edge0.elevationAt(500), test.elevationAt(500));
            assertEquals(edge0.elevationAt(999.2), test.elevationAt(999.2));
            assertEquals(edge1.elevationAt(0), test.elevationAt(1000));
            assertEquals(edge2.elevationAt(1500), test.elevationAt(4500));
            assertEquals(edge2.elevationAt(1999), test.elevationAt(4999));
            assertEquals(edge3.elevationAt(1000), test.elevationAt(6000));
            assertEquals(edge3.elevationAt(1000), test.elevationAt(6999));
        }
        @Test
        public void nodeClosestToTest(){
            assertEquals(0, test.nodeClosestTo(499));
            assertEquals(0, test.nodeClosestTo(0));
            assertEquals(2, test.nodeClosestTo(3400));
            assertEquals(2, test.nodeClosestTo(3000));
            assertEquals(4, test.nodeClosestTo(7000));
            assertEquals(1,test.nodeClosestTo(900));
        }
        @Test
        public void pointClosestToTest(){
            assertEquals(point0, test.pointClosestTo(point0));

        }
    }

