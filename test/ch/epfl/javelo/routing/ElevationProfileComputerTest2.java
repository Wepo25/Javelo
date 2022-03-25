package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.TestRandomizer;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.TestRandomizer.newRandom;
import static ch.epfl.javelo.routing.ElevationProfileComputer.*;

import static org.junit.jupiter.api.Assertions.*;


public class ElevationProfileComputerTest2 {

    public class RouteTest1 implements Route {

        private List<Edge> edges;
        private PointCh point1 = new PointCh(2485010, 1075010); //NotARealAttribute

        public RouteTest1(List<Edge> edges) {
            if (edges.isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                this.edges = edges;
            }
        }

        @Override
        public double length() {
            double length = 0;
            for (int i = 0; i < edges.size(); ++i) {
                length += edges.get(i).length();
            }
            return length;
        }

        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public List<Edge> edges() {
            return edges;
        }

        @Override
        public List<PointCh> points() {
            List<PointCh> points = new ArrayList<>();
            for (int i = 0; i < edges.size(); ++i) {
                points.add(edges.get(i).fromPoint());
            }
            points.add(edges.get(edges.size() - 1).toPoint());
            return points;
        }

        public RoutePoint pointClosestTo(PointCh point) {
            RoutePoint route1 = new RoutePoint(point1, 10, 20);
            return route1;
        }

        @Override
        public PointCh pointAt(double position) {
            return point1;
        }


        //J'AI INVENTE UNE SORTE D'ALGO BUT NOT SURE.
        @Override
        public double elevationAt(double position) {
            double oldPosition = position;
            double newPosition = position;
            for (int i = 0; i < edges.size(); ++i) {
                newPosition = oldPosition - edges.get(i).length();
                if (newPosition <= 0) {
                    return edges.get(i).elevationAt(oldPosition);
                } else {
                    oldPosition = newPosition;
                }
            }
            return 444;
        }

        @Override
        public int nodeClosestTo(double position) {
            return 444;
        }
    }

    @Test
    void elevationProfileWorksForEmptyProfiles() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, nan);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, nan);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(0, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileOneEdgeNotNaN() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5.5);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, nan);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, cst);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, nan);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        assertEquals(5.5, elevationProfileTesting.elevationAt(25));
        assertEquals(5.5, elevationProfileTesting.elevationAt(35));
        assertEquals(5.5, elevationProfileTesting.elevationAt(45));
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(5.5, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileWith1NaNInTheMiddleSimple() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5.5);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, cst);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, cst);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, cst);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(5.5, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileWith1NaNInTheMiddleDouble() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5);
        DoubleUnaryOperator cst2 = Functions.constant(6);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 1, cst);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 1, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 1, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 1, cst2);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 1, cst2);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 1);
        assertEquals(5, elevationProfileTesting.elevationAt(0));
        assertEquals(5, elevationProfileTesting.elevationAt(2));
        assertEquals(6, elevationProfileTesting.elevationAt(4));
    }

    @Test
    void elevationProfileCompleteTest() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5);
        float[] squaredSamples = {0, 4, 16, 36, 64, 100};
        DoubleUnaryOperator squared = Functions.sampled(squaredSamples, 10);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, squared);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 2.5);
        float[] expected = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5.4f, 5.8f, 6.2f, 6.6f, 7, 26, 57, 100, 100, 100, 100, 100};
        assertEquals(5, elevationProfileTesting.elevationAt(5));
        assertEquals(5, elevationProfileTesting.elevationAt(15));
        assertEquals(5.4f, elevationProfileTesting.elevationAt(22.5));
        assertEquals(5.8f, elevationProfileTesting.elevationAt(25));
        assertEquals(6.6f, elevationProfileTesting.elevationAt(30));
        assertEquals(7, elevationProfileTesting.elevationAt(32.5));
        assertEquals(100, elevationProfileTesting.elevationAt(50));
    }

}