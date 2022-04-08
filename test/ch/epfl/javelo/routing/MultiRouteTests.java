package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.javelo.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiRouteTests {

    float[] samples11 = {300, 310, 305, 320, 300, 290, 305, 300, 310, 300};
    float[] samples12 = {300, 310, 320, 340, 350, 365, 385, 400, 410, 405};
    PointCh route1start = new PointCh(2485000, 1075000);
    PointCh route1etape = new PointCh(2485100, 1075100);
    PointCh route1to2 = new PointCh(2485150, 1075150);


    float[] samples21 = {405, 410, 405, 420, 400, 390, 405, 400, 410, 400};
    float[] samples22 = {400, 410, 390, 370, 360, 365, 385, 400, 410, 400};
    PointCh route2etape = new PointCh(2485200, 1075200);
    PointCh route2to3 = new PointCh(2485250, 1075250);

    float[] samples31 = {400, 410, 405, 420, 400, 390, 405, 400, 410, 400};
    float[] samples32 = {400, 410, 390, 380, 360, 365, 355, 340, 325, 320};
    PointCh route3etape = new PointCh(2485300, 1075300);
    PointCh route3to4 = new PointCh(2485350, 1075350);

    float[] samples41 = {320, 310, 305, 320, 300, 290, 305, 300, 310, 300};
    float[] samples42 = {300, 310, 320, 340, 350, 365, 385, 400, 405, 400};
    PointCh route4etape = new PointCh(2485400, 1075400);
    PointCh route4to5 = new PointCh(2485450, 1075450);

    float[] samples51 = {400, 390, 385, 390, 400, 390, 380, 370, 375, 380};
    float[] samples52 = {380, 370, 355, 345, 340, 330, 325, 325, 320, 305};
    PointCh route5etape = new PointCh(2485500, 1075500);
    PointCh route5end = new PointCh(2485550, 1075550);

    @Test
    void simpleTestEverything(){

        ArrayList<Edge> allEdges = new ArrayList<>();
        ArrayList<PointCh> allPoints = new ArrayList<>();

        int fromNodeId1 = 0;
        int toNodeId11 = 10;
        int toNodeId12 = 15;
        double length11 = 101;
        double length12 = 10;
        DoubleUnaryOperator a1 = Functions.sampled(samples11, 100);
        DoubleUnaryOperator b1 = Functions.sampled(samples12, 100);
        Edge edge11 = new Edge(fromNodeId1, toNodeId11, route1start, route1etape, length11, a1);
        Edge edge12 = new Edge(toNodeId11,toNodeId12, route1etape, route1to2,length12,b1);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge11);
        edges1.add(edge12);
        SingleRoute route1 = new SingleRoute(edges1);
        allPoints.add(route1start);
        allPoints.add(route1etape);
        allPoints.add(route1to2);

        int fromNodeId2 = 15;
        int toNodeId21 = 20;
        int toNodeId22 = 25;
        double length21 = 102;
        double length22 = 20;
        DoubleUnaryOperator a2 = Functions.sampled(samples21, 100);
        DoubleUnaryOperator b2 = Functions.sampled(samples22, 100);
        Edge edge21 = new Edge(fromNodeId2, toNodeId21, route1to2, route2etape, length21, a2);
        Edge edge22 = new Edge(toNodeId21,toNodeId22, route2etape, route2to3,length22,b2);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(edge21);
        edges2.add(edge22);
        SingleRoute route2 = new SingleRoute(edges2);
        allPoints.add(route2etape);
        allPoints.add(route2to3);

        int fromNodeId3 = 25;
        int toNodeId31 = 30;
        int toNodeId32 =35;
        double length31 = 103;
        double length32 = 30;
        DoubleUnaryOperator a3 = Functions.sampled(samples31, 100);
        DoubleUnaryOperator b3 = Functions.sampled(samples32, 100);
        Edge edge31 = new Edge(fromNodeId3, toNodeId31, route2to3, route3etape, length31, a3);
        Edge edge32 = new Edge(toNodeId31,toNodeId32, route3etape, route3to4,length32,b3);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(edge31);
        edges3.add(edge32);
        SingleRoute route3 = new SingleRoute(edges3);
        allPoints.add(route3etape);
        allPoints.add(route3to4);

        int fromNodeId4 = 35;
        int toNodeId41 = 40;
        int toNodeId42 =45;
        double length41 = 104;
        double length42 = 40;
        DoubleUnaryOperator a4 = Functions.sampled(samples41, 100);
        DoubleUnaryOperator b4 = Functions.sampled(samples42, 100);
        Edge edge41 = new Edge(fromNodeId4, toNodeId41, route3to4, route4etape, length41, a4);
        Edge edge42 = new Edge(toNodeId41,toNodeId42, route4etape, route4to5,length42,b4);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(edge41);
        edges4.add(edge42);
        SingleRoute route4 = new SingleRoute(edges4);
        allPoints.add(route4etape);
        allPoints.add(route4to5);

        int fromNodeId5 = 45;
        int toNodeId51 = 50;
        int toNodeId52 =55;
        double length51 = 105;
        double length52 = 50;
        DoubleUnaryOperator a5 = Functions.sampled(samples51, 100);
        DoubleUnaryOperator b5 = Functions.sampled(samples52, 100);
        Edge edge51 = new Edge(fromNodeId5, toNodeId51, route4to5, route5etape, length51, a5);
        Edge edge52 = new Edge(toNodeId51,toNodeId52, route5etape, route5end,length52,b5);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(edge51);
        edges5.add(edge52);
        SingleRoute route5 = new SingleRoute(edges5);
        allPoints.add(route5etape);
        allPoints.add(route5end);

        allEdges.addAll(edges1);
        allEdges.addAll(edges2);
        allEdges.addAll(edges3);
        allEdges.addAll(edges4);
        allEdges.addAll(edges5);

        List<Route> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);
        routes.add(route4);
        routes.add(route5);

        MultiRoute route = new MultiRoute(routes);

        //only SingleRoutes
        int expected0 = 0;
        int actual0 = route.indexOfSegmentAt(-100);
        assertEquals(expected0, actual0);


        int expected1 = 0;
        int actual1 = route.indexOfSegmentAt(0);
        assertEquals(expected1, actual1);

        int expected2 = 4;
        int actual2 = route.indexOfSegmentAt(1000);
        assertEquals(expected2, actual2);

        int expected22 = 4;
        int actual22 = route.indexOfSegmentAt(665);
        assertEquals(expected22, actual22);

        int expected3 = 2;
        int actual3 = route.indexOfSegmentAt(332.5);
        assertEquals(expected3, actual3);

        int expected4 = 3;
        int actual4 = route.indexOfSegmentAt(510);
        assertEquals(expected4, actual4);
        //Ok pour 3 et 4

        int expected5 = 2;
        int actual5 = route.indexOfSegmentAt(300);
        assertEquals(expected5, actual5);

        assertEquals(allEdges, route.edges());

        assertEquals(allPoints, route.points());

        assertEquals(route1start, route.pointAt(0));
        assertEquals(route1etape, route.pointAt(101));
        assertEquals(route1to2, route.pointAt(111));
        assertEquals(route2etape, route.pointAt(213));
        assertEquals(route2to3, route.pointAt(233));
        assertEquals(route3etape, route.pointAt(336));
        assertEquals(route3to4, route.pointAt(366));
        assertEquals(route4etape, route.pointAt(470));
        assertEquals(route4to5, route.pointAt(510));
        assertEquals(route5etape, route.pointAt(615));
        assertEquals(route5end, route.pointAt(665));

        //TODO Tester les élévations
        assertEquals(300, route.elevationAt(0));
        assertEquals(300, route.elevationAt(101));
        //assertEquals(405, route.elevationAt(111));
        assertEquals(400, route.elevationAt(213));
        //assertEquals(400, route.elevationAt(233));

        assertEquals(fromNodeId1, route.nodeClosestTo(0));
        assertEquals(fromNodeId1, route.nodeClosestTo(50));
        assertEquals(toNodeId52, route.nodeClosestTo(664));
        assertEquals(toNodeId22, route.nodeClosestTo(234));
        assertEquals(toNodeId41, route.nodeClosestTo(438));
        assertEquals(toNodeId52, route.nodeClosestTo(1000));

        RoutePoint point1 = new RoutePoint(route1start, 0.0, 0.0);
        assertEquals(point1, route.pointClosestTo(route1start));

        RoutePoint point2 = new RoutePoint(route2etape, 213, 0);
        assertEquals(point2, route.pointClosestTo(route2etape));

        RoutePoint point3 = new RoutePoint(route5end, 665, 10);
        assertEquals(point3, route.pointClosestTo(new PointCh(2485550, 1075560)));

    }

    @Test
    void multiMultiRoutesTestEverything(){

        ArrayList<Edge> allEdges = new ArrayList<>();
        ArrayList<PointCh> allPoints = new ArrayList<>();

        int fromNodeId1 = 0;
        int toNodeId11 = 10;
        int toNodeId12 = 15;
        double length11 = 101;
        double length12 = 10;
        DoubleUnaryOperator a1 = Functions.sampled(samples11, 100);
        DoubleUnaryOperator b1 = Functions.sampled(samples12, 100);
        Edge edge11 = new Edge(fromNodeId1, toNodeId11, route1start, route1etape, length11, a1);
        Edge edge12 = new Edge(toNodeId11,toNodeId12, route1etape, route1to2,length12,b1);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge11);
        edges1.add(edge12);
        SingleRoute route1 = new SingleRoute(edges1);
        allPoints.add(route1start);
        allPoints.add(route1etape);
        allPoints.add(route1to2);

        int fromNodeId2 = 15;
        int toNodeId21 = 20;
        int toNodeId22 = 25;
        double length21 = 102;
        double length22 = 20;
        DoubleUnaryOperator a2 = Functions.sampled(samples21, 100);
        DoubleUnaryOperator b2 = Functions.sampled(samples22, 100);
        Edge edge21 = new Edge(fromNodeId2, toNodeId21, route1to2, route2etape, length21, a2);
        Edge edge22 = new Edge(toNodeId21,toNodeId22, route2etape, route2to3,length22,b2);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(edge21);
        edges2.add(edge22);
        SingleRoute route2 = new SingleRoute(edges2);
        allPoints.add(route2etape);
        allPoints.add(route2to3);

        int fromNodeId3 = 25;
        int toNodeId31 = 30;
        int toNodeId32 =35;
        double length31 = 103;
        double length32 = 30;
        DoubleUnaryOperator a3 = Functions.sampled(samples31, 100);
        DoubleUnaryOperator b3 = Functions.sampled(samples32, 100);
        Edge edge31 = new Edge(fromNodeId3, toNodeId31, route2to3, route3etape, length31, a3);
        Edge edge32 = new Edge(toNodeId31,toNodeId32, route3etape, route3to4,length32,b3);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(edge31);
        edges3.add(edge32);
        SingleRoute route3 = new SingleRoute(edges3);
        allPoints.add(route3etape);
        allPoints.add(route3to4);

        int fromNodeId4 = 35;
        int toNodeId41 = 40;
        int toNodeId42 =45;
        double length41 = 104;
        double length42 = 40;
        DoubleUnaryOperator a4 = Functions.sampled(samples41, 100);
        DoubleUnaryOperator b4 = Functions.sampled(samples42, 100);
        Edge edge41 = new Edge(fromNodeId4, toNodeId41, route3to4, route4etape, length41, a4);
        Edge edge42 = new Edge(toNodeId41,toNodeId42, route4etape, route4to5,length42,b4);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(edge41);
        edges4.add(edge42);
        SingleRoute route4 = new SingleRoute(edges4);
        allPoints.add(route4etape);
        allPoints.add(route4to5);

        int fromNodeId5 = 45;
        int toNodeId51 = 50;
        int toNodeId52 =55;
        double length51 = 105;
        double length52 = 50;
        DoubleUnaryOperator a5 = Functions.sampled(samples51, 100);
        DoubleUnaryOperator b5 = Functions.sampled(samples52, 100);
        Edge edge51 = new Edge(fromNodeId5, toNodeId51, route4to5, route5etape, length51, a5);
        Edge edge52 = new Edge(toNodeId51,toNodeId52, route5etape, route5end,length52,b5);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(edge51);
        edges5.add(edge52);
        SingleRoute route5 = new SingleRoute(edges5);
        allPoints.add(route5etape);
        allPoints.add(route5end);

        List<Route> routes1 = new ArrayList<>();
        List<Route> routes2 = new ArrayList<>();
        List<Route> routes3 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        routes2.add(route3);
        routes2.add(route4);
        routes3.add(route5);

        MultiRoute r1 = new MultiRoute(routes1);
        MultiRoute r2 = new MultiRoute(routes2);
        MultiRoute r3 = new MultiRoute(routes3);

        List<Route> routes = new ArrayList();
        routes.add(r1);
        routes.add(r2);
        routes.add(r3);

        allEdges.addAll(edges1);
        allEdges.addAll(edges2);
        allEdges.addAll(edges3);
        allEdges.addAll(edges4);
        allEdges.addAll(edges5);

        MultiRoute multi = new MultiRoute(routes);

        //multiple MultiRoutes
        int expected0m = 0;
        int actual0m = multi.indexOfSegmentAt(-100);
        assertEquals(expected0m, actual0m);
        int expected1m = 0;
        int actual1m = multi.indexOfSegmentAt(0);
        assertEquals(expected1m, actual1m);
        int expected2m = 4;
        int actual2m = multi.indexOfSegmentAt(1000);
        assertEquals(expected2m, actual2m);
        int expected3m = 2;
        int actual3m = multi.indexOfSegmentAt(332.5);
        assertEquals(expected3m, actual3m);
        int expected4m = 3;
        int actual4m = multi.indexOfSegmentAt(510);
        assertEquals(expected4m, actual4m);
        int expected5m = 2;
        int actual5m = multi.indexOfSegmentAt(300);
        assertEquals(expected5m, actual5m);

        int expected222 = 4;
        int actual222 = multi.indexOfSegmentAt(665);
        assertEquals(expected222, actual222);

        assertEquals(allEdges, multi.edges());

        assertEquals(allPoints, multi.points());

        assertEquals(route1start, multi.pointAt(0));
        assertEquals(route1etape, multi.pointAt(101));
        assertEquals(route1to2, multi.pointAt(111));
        assertEquals(route2etape, multi.pointAt(213));
        assertEquals(route2to3, multi.pointAt(233));
        assertEquals(route3etape, multi.pointAt(336));
        assertEquals(route3to4, multi.pointAt(366));
        assertEquals(route4etape, multi.pointAt(470));
        assertEquals(route4to5, multi.pointAt(510));
        assertEquals(route5etape, multi.pointAt(615));
        assertEquals(route5end, multi.pointAt(665));

        //TODO Tester les élévations

        assertEquals(fromNodeId1, multi.nodeClosestTo(0));
        assertEquals(fromNodeId1, multi.nodeClosestTo(50));
        assertEquals(toNodeId52, multi.nodeClosestTo(664));
        assertEquals(toNodeId22, multi.nodeClosestTo(234));
        assertEquals(toNodeId41, multi.nodeClosestTo(438));
        assertEquals(toNodeId52, multi.nodeClosestTo(1000));

        RoutePoint point1 = new RoutePoint(route1start, 0.0, 0.0);
        assertEquals(point1, multi.pointClosestTo(route1start));

        RoutePoint point2 = new RoutePoint(route2etape, 213, 0);
        assertEquals(point2, multi.pointClosestTo(route2etape));

        RoutePoint point3 = new RoutePoint(route5end, 665, 10);
        assertEquals(point3, multi.pointClosestTo(new PointCh(2485550, 1075560)));
    }


    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    // Sides of triangle used for "sawtooth" edges (shape: /\/\/\…)
    private static final double TOOTH_EW = 1023;
    private static final double TOOTH_NS = 64;
    private static final double TOOTH_LENGTH = 1025;
    private static final double TOOTH_ELEVATION_GAIN = 100d;
    private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;

    @Test
    void multiRouteConstructorThrowsOnEmptyEdgeList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(List.of());
        });
    }

    @Test
    void multiRouteIndexOfSegmentAtAlwaysReturns0() {
        //On le passe pour une multiroute formé d'une singleRoute
        var route = new SingleRoute(verticalEdges(10));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var p = rng.nextDouble(-100, multiRoute.length() + 100);
            assertEquals(0, multiRoute.indexOfSegmentAt(p));
        }
    }

    @Test
        //On le passe pour une multiroute formé d'une singleRoute
    void multiRouteLengthReturnsTotalLength() {
        for (int i = 1; i < 10; i += 1) {
            var route = new SingleRoute(verticalEdges(i));
            List<Route> routeList = new ArrayList<>();
            routeList.add(route);
            var multiRoute = new MultiRoute(routeList);
            assertEquals(i * EDGE_LENGTH, multiRoute.length());
        }
    }

    @Test
        //On le passe pour une multiroute formé d'une singleRoute
    void multiRouteEdgesAreCopiedToEnsureImmutability() {
        var immutableEdges = verticalEdges(10);
        var mutableEdges = new ArrayList<>(immutableEdges);
        var route = new SingleRoute(mutableEdges);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);
        mutableEdges.clear();
        assertEquals(immutableEdges, multiRoute.edges());
    }

    @Test
        //On le passe pour une multiroute formé d'une singleRoute
    void multiRoutePointsAreNotModifiableFromOutside() {
        var edgesCount = 5;
        var route = new SingleRoute(verticalEdges(edgesCount));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);
        try {
            multiRoute.points().clear();
        } catch (UnsupportedOperationException e) {
            // Nothing to do (the list of points is not modifiable, which is fine).
        }
        assertEquals(edgesCount + 1, multiRoute.points().size());
    }
    @Test
        //On le passe pour une multiroute formé d'une singleRoute
    void multiRoutePointsAreCorrect() {
        for (int edgesCount = 1; edgesCount < 10; edgesCount += 1) {
            var edges = verticalEdges(edgesCount);
            var route = new SingleRoute(edges);
            List<Route> routeList = new ArrayList<>();
            routeList.add(route);
            var multiRoute = new MultiRoute(routeList);
            var points = multiRoute.points();
            assertEquals(edgesCount + 1, points.size());
            Assertions.assertEquals(edges.get(0).fromPoint(), points.get(0));
            for (int i = 1; i < points.size(); i += 1)
                Assertions.assertEquals(edges.get(i - 1).toPoint(), points.get(i));
        }
    }

    @Test
    void multiRoutePointAtWorks() {
        var edgesCount = 4;
        var route = new SingleRoute(sawToothEdges(edgesCount));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);

        // Outside the range of the route
        assertEquals(sawToothPoint(0), multiRoute.pointAt(-1e6));
        assertEquals(sawToothPoint(edgesCount), multiRoute.pointAt(+1e6));

        // Edge endpoints
        for (int i = 0; i < edgesCount + 1; i += 1)
            assertEquals(sawToothPoint(i), multiRoute.pointAt(i * TOOTH_LENGTH));

        // Points at 1/4, 2/4 and 3/4 of the edges
        for (int i = 0; i < edgesCount; i += 1) {
            for (double p = 0.25; p <= 0.75; p += 0.25) {
                var expectedE = ORIGIN_E + (i + p) * TOOTH_EW;
                var expectedN = (i & 1) == 0
                        ? ORIGIN_N + TOOTH_NS * p
                        : ORIGIN_N + TOOTH_NS * (1 - p);
                assertEquals(
                        new PointCh(expectedE, expectedN),
                        multiRoute.pointAt((i + p) * TOOTH_LENGTH));
            }
        }
    }

    @Test
    void multiRouteElevationAtWorks() {
        var edgesCount = 4;
        var route = new SingleRoute(sawToothEdges(edgesCount));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);
        for (int i = 0; i < edgesCount; i += 1) {
            for (double p = 0; p < 1; p += 0.125) {
                var pos = (i + p) * TOOTH_LENGTH;
                var expectedElevation = (i + p) * TOOTH_ELEVATION_GAIN;
                assertEquals(expectedElevation, multiRoute.elevationAt(pos));
            }
        }
        assertEquals(0, multiRoute.elevationAt(-1e6));
        assertEquals(edgesCount * TOOTH_ELEVATION_GAIN, multiRoute.elevationAt(+1e6));
    }

    @Test
    void multiRouteNodeClosestToWorks() {
        var edgesCount = 4;
        var route = new SingleRoute(sawToothEdges(edgesCount));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);
        for (int i = 0; i <= edgesCount; i += 1) {
            for (double p = -0.25; p <= 0.25; p += 0.25) {
                var pos = (i + p) * TOOTH_LENGTH;
                assertEquals(i, multiRoute.nodeClosestTo(pos));
            }
        }
    }

    @Test
    void multiRoutePointClosestToWorksWithFarAwayPoints() {
        var rng = newRandom();
        var route = new SingleRoute(verticalEdges(1));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);

        // Points below the route
        var origin = new PointCh(ORIGIN_E, ORIGIN_N);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var dN = rng.nextDouble(-10_000, -1);
            var dE = rng.nextDouble(-1000, 1000);
            var p = new PointCh(ORIGIN_E + dE, ORIGIN_N + dN);
            var pct = multiRoute.pointClosestTo(p);
            assertEquals(origin, pct.point());
            assertEquals(0, pct.position());
            assertEquals(Math.hypot(dE, dN), pct.distanceToReference(), 1e-4);
        }
    // Points above the route
    var end = new PointCh(ORIGIN_E, ORIGIN_N + EDGE_LENGTH);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
        var dN = rng.nextDouble(1, 10_000);
        var dE = rng.nextDouble(-1000, 1000);
        var p = new PointCh(ORIGIN_E + dE, ORIGIN_N + EDGE_LENGTH + dN);
        var pct = multiRoute.pointClosestTo(p);
        assertEquals(end, pct.point());
        assertEquals(EDGE_LENGTH, pct.position());
        assertEquals(Math.hypot(dE, dN), pct.distanceToReference(), 1e-4);
    }
}

    @Test
    void multiRoutePointClosestToWorksWithPointsOnRoute() {
        var rng = newRandom();
        var route = new SingleRoute(verticalEdges(20));
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);

        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var pos = rng.nextDouble(0, route.length());
            var pt = multiRoute.pointAt(pos);
            var pct = multiRoute.pointClosestTo(pt);
            assertEquals(pt.e(), pct.point().e(), 1e-4);
            assertEquals(pt.n(), pct.point().n(), 1e-4);
            assertEquals(pos, pct.position(), 1e-4);
            assertEquals(0, pct.distanceToReference(), 1e-4);
        }
    }

    @Test
    void multiRoutePointClosestToWorksWithSawtoothPoints() {
        var edgesCount = 4;
        var edges = sawToothEdges(edgesCount);
        var route = new SingleRoute(edges);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        var multiRoute = new MultiRoute(routeList);

        // Points above the sawtooth
        for (int i = 1; i <= edgesCount; i += 2) {
            var p = sawToothPoint(i);
            var dN = i * 500;
            var pAbove = new PointCh(p.e(), p.n() + dN);
            var pct = multiRoute.pointClosestTo(pAbove);
            assertEquals(p, pct.point());
            assertEquals(i * TOOTH_LENGTH, pct.position());
            assertEquals(dN, pct.distanceToReference());
        }

        // Points below the sawtooth
        for (int i = 0; i <= edgesCount; i += 2) {
            var p = sawToothPoint(i);
            var dN = i * 500;
            var pBelow = new PointCh(p.e(), p.n() - dN);
            var pct = multiRoute.pointClosestTo(pBelow);
            assertEquals(p, pct.point());
            assertEquals(i * TOOTH_LENGTH, pct.position());
            assertEquals(dN, pct.distanceToReference());
        }

        // Points close to the n/8
        var dE = TOOTH_NS / 16d;
        var dN = TOOTH_EW / 16d;
        for (int i = 0; i < edgesCount; i += 1) {
            var upwardEdge = (i & 1) == 0;
            for (double p = 0.125; p <= 0.875; p += 0.125) {
                var pointE = ORIGIN_E + (i + p) * TOOTH_EW;
                var pointN = ORIGIN_N + TOOTH_NS * (upwardEdge ? p : (1 - p));
                var point = new PointCh(pointE, pointN);
                var position = (i + p) * TOOTH_LENGTH;
                var reference = new PointCh(
                        pointE + dE,
                        pointN + (upwardEdge ? -dN : dN));
                var pct = multiRoute.pointClosestTo(reference);
                assertEquals(point, pct.point());
                assertEquals(position, pct.position());
                assertEquals(Math.hypot(dE, dN), pct.distanceToReference());
            }
        }
    }

    private static List<Edge> verticalEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> Double.NaN));
        }
        return Collections.unmodifiableList(edges);
    }

    private static List<Edge> sawToothEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = sawToothPoint(i);
            var p2 = sawToothPoint(i + 1);
            var startingElevation = i * TOOTH_ELEVATION_GAIN;
            edges.add(new Edge(i, i + 1, p1, p2, TOOTH_LENGTH, x -> startingElevation + x * TOOTH_SLOPE));
        }
        return Collections.unmodifiableList(edges);
    }

    private static PointCh sawToothPoint(int i) {
        return new PointCh(
                ORIGIN_E + TOOTH_EW * i,
                ORIGIN_N + ((i & 1) == 0 ? 0 : TOOTH_NS));
    }





    @Test
    void indexOfSegmentAt() {
        java.util.function.DoubleUnaryOperator s1 = Functions.sampled(new float[]{1,2,3,4,5}, 2.7);
        java.util.function.DoubleUnaryOperator s2 = Functions.sampled(new float[]{5,4.5f,4,3,2}, 1);
        java.util.function.DoubleUnaryOperator s3 = Functions.sampled(new float[]{Float.NaN, Float.NaN}, 3);
        java.util.function.DoubleUnaryOperator s4 = Functions.sampled(new float[]{12,11,10,9,8}, 1);
        java.util.function.DoubleUnaryOperator s5 = Functions.sampled(new float[]{8,35,40,45,50}, 0.36);

        PointCh p1f = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1t = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2f = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2t = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3f = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3t = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4f = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4t = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5f = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5t = new PointCh(SwissBounds.MIN_E+5, SwissBounds.MIN_N);

        Edge e1 = new Edge(0,1,p1f,p1t,2.7,s1);
        Edge e2 = new Edge(1,2,p2f,p2t,1,s2);
        Edge e3 = new Edge(2,3,p3f,p3t,3,s3);
        Edge e4 = new Edge(3,4,p4f,p4t,1,s4);
        Edge e5 = new Edge(4,5,p5f,p5t,0.36,s5);

        List<Edge> edges1 = new ArrayList<>();
        edges1.add(e1);
        edges1.add(e2);
        edges1.add(e3);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(e2);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(e3);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(e4);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(e5);

        Route route1 = new SingleRoute(edges1);
        Route route2 = new SingleRoute(edges2);
        Route route3 = new SingleRoute(edges3);
        Route route4 = new SingleRoute(edges4);
        Route route5 = new SingleRoute(edges5);

        List<Route> route12 = new ArrayList<>(List.of(route1,route2));
        List<Route> route345 = new ArrayList<>(List.of(route3,route4,route5));
        Route multiRoute1 = new MultiRoute(route12);
        Route multiRoute2 = new MultiRoute(route345);

        List<Route> route12345 = new ArrayList<>(List.of(multiRoute1, multiRoute2));
        Route multiRoute3 = new MultiRoute(route12345);

        List<Route> finalRoute = new ArrayList<>(List.of(multiRoute1, multiRoute2, multiRoute3));
        Route finalMultiRoute = new MultiRoute(finalRoute);

        //Normal psk on prend l'arrête d'avant
        //0 au lieu du 1 ok
        assertEquals(0, finalMultiRoute.indexOfSegmentAt(6.7));
        assertEquals(0, finalMultiRoute.indexOfSegmentAt(-1));
        assertEquals(1, finalMultiRoute.indexOfSegmentAt(7.69));
        //1 au lieu du 2 ok
        assertEquals(1, finalMultiRoute.indexOfSegmentAt(7.7));
        assertEquals(3, finalMultiRoute.indexOfSegmentAt(11));
//        assertEquals(7, finalMultiRoute.indexOfSegmentAt(20));

    }

    @Test
    void edges() {
        java.util.function.DoubleUnaryOperator s1 = Functions.sampled(new float[]{1,2,3,4,5}, 2.7);
        java.util.function.DoubleUnaryOperator s2 = Functions.sampled(new float[]{5,4.5f,4,3,2}, 1);
        java.util.function.DoubleUnaryOperator s3 = Functions.sampled(new float[]{Float.NaN, Float.NaN}, 3);
        java.util.function.DoubleUnaryOperator s4 = Functions.sampled(new float[]{12,11,10,9,8}, 1);
        java.util.function.DoubleUnaryOperator s5 = Functions.sampled(new float[]{8,35,40,45,50}, 0.36);

        PointCh p1f = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1t = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2f = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2t = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3f = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3t = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4f = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4t = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5f = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5t = new PointCh(SwissBounds.MIN_E+5, SwissBounds.MIN_N);

        Edge e1 = new Edge(0,1,p1f,p1t,2.7,s1);
        Edge e2 = new Edge(1,2,p2f,p2t,1,s2);
        Edge e3 = new Edge(2,3,p3f,p3t,3,s3);
        Edge e4 = new Edge(3,4,p4f,p4t,1,s4);
        Edge e5 = new Edge(4,5,p5f,p5t,0.36,s5);

        List<Edge> edges1 = new ArrayList<>();
        edges1.add(e1);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(e2);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(e3);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(e4);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(e5);

        Route route1 = new SingleRoute(edges1);
        Route route2 = new SingleRoute(edges2);
        Route route3 = new SingleRoute(edges3);
        Route route4 = new SingleRoute(edges4);
        Route route5 = new SingleRoute(edges5);

        List<Route> route12 = new ArrayList<>(List.of(route1,route2));
        List<Route> route345 = new ArrayList<>(List.of(route3,route4,route5));
        Route multiRoute1 = new MultiRoute(route12);
        Route multiRoute2 = new MultiRoute(route345);

        List<Route> route12345 = new ArrayList<>(List.of(multiRoute1, multiRoute2));
        Route multiRoute3 = new MultiRoute(route12345);

        List<Route> finalRoute = new ArrayList<>(List.of(multiRoute1, multiRoute2, multiRoute3));
        Route finalMultiRoute = new MultiRoute(finalRoute);

        List<Edge> expected = new ArrayList<>(List.of(e1,e2,e3,e4,e5,e1,e2,e3,e4,e5));
        assertEquals(expected, finalMultiRoute.edges());
    }

    @Test
    void points() {
        java.util.function.DoubleUnaryOperator s1 = Functions.sampled(new float[]{1,2,3,4,5}, 2.7);
        java.util.function.DoubleUnaryOperator s2 = Functions.sampled(new float[]{5,4.5f,4,3,2}, 1);
        java.util.function.DoubleUnaryOperator s3 = Functions.sampled(new float[]{Float.NaN, Float.NaN}, 3);
        java.util.function.DoubleUnaryOperator s4 = Functions.sampled(new float[]{12,11,10,9,8}, 1);
        java.util.function.DoubleUnaryOperator s5 = Functions.sampled(new float[]{8,35,40,45,50}, 0.36);

        PointCh p1f = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1t = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2f = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2t = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3f = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3t = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4f = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4t = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5f = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5t = new PointCh(SwissBounds.MIN_E+5, SwissBounds.MIN_N);

        Edge e1 = new Edge(0,1,p1f,p1t,2.7,s1);
        Edge e2 = new Edge(1,2,p2f,p2t,1,s2);
        Edge e3 = new Edge(2,3,p3f,p3t,3,s3);
        Edge e4 = new Edge(3,4,p4f,p4t,1,s4);
        Edge e5 = new Edge(4,5,p5f,p5t,0.36,s5);

        List<Edge> edges1 = new ArrayList<>();
        edges1.add(e1);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(e2);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(e3);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(e4);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(e5);

        Route route1 = new SingleRoute(edges1);
        Route route2 = new SingleRoute(edges2);
        Route route3 = new SingleRoute(edges3);
        Route route4 = new SingleRoute(edges4);
        Route route5 = new SingleRoute(edges5);

        List<Route> route12 = new ArrayList<>(List.of(route1,route2));
        List<Route> route345 = new ArrayList<>(List.of(route3,route4,route5));
        Route multiRoute1 = new MultiRoute(route12);
        Route multiRoute2 = new MultiRoute(route345);

        List<Route> finalRoute = new ArrayList<>(List.of(multiRoute1, multiRoute2));
        Route finalMultiRoute = new MultiRoute(finalRoute);

        List<PointCh> expected = new ArrayList<>(List.of(p1f,p2f,p3f,p4f,p5f,p5t));
        assertEquals(expected, finalMultiRoute.points());
    }

    @Test
    void pointAt() {
        java.util.function.DoubleUnaryOperator s1 = Functions.sampled(new float[]{1,2,3,4,5}, 2.7);
        java.util.function.DoubleUnaryOperator s2 = Functions.sampled(new float[]{5,4.5f,4,3,2}, 1);
        java.util.function.DoubleUnaryOperator s3 = Functions.sampled(new float[]{Float.NaN, Float.NaN}, 3);
        java.util.function.DoubleUnaryOperator s4 = Functions.sampled(new float[]{12,11,10,9,8}, 1);
        java.util.function.DoubleUnaryOperator s5 = Functions.sampled(new float[]{8,35,40,45,50}, 0.36);

        PointCh p1f = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1t = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2f = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2t = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3f = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3t = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4f = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4t = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5f = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5t = new PointCh(SwissBounds.MIN_E+5, SwissBounds.MIN_N);

        Edge e1 = new Edge(0,1,p1f,p1t,2.7,s1);
        Edge e2 = new Edge(1,2,p2f,p2t,1,s2);
        Edge e3 = new Edge(2,3,p3f,p3t,3,s3);
        Edge e4 = new Edge(3,4,p4f,p4t,1,s4);
        Edge e5 = new Edge(4,5,p5f,p5t,0.36,s5);

        List<Edge> edges1 = new ArrayList<>();
        edges1.add(e1);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(e2);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(e3);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(e4);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(e5);

        Route route1 = new SingleRoute(edges1);
        Route route2 = new SingleRoute(edges2);
        Route route3 = new SingleRoute(edges3);
        Route route4 = new SingleRoute(edges4);
        Route route5 = new SingleRoute(edges5);

        List<Route> route12 = new ArrayList<>(List.of(route1,route2));
        List<Route> route345 = new ArrayList<>(List.of(route3,route4,route5));
        Route multiRoute1 = new MultiRoute(route12);
        Route multiRoute2 = new MultiRoute(route345);

        List<Route> finalRoute = new ArrayList<>(List.of(multiRoute1, multiRoute2));
        Route finalMultiRoute = new MultiRoute(finalRoute);

        assertEquals(p2t,finalMultiRoute.pointAt(3.7));
    }

    @Test
    void elevationAt() {
        java.util.function.DoubleUnaryOperator s1 = Functions.sampled(new float[]{1,2,3,4,5}, 2.7);
        java.util.function.DoubleUnaryOperator s2 = Functions.sampled(new float[]{5,4.5f,4,3,2}, 1);
        java.util.function.DoubleUnaryOperator s3 = Functions.sampled(new float[]{Float.NaN, Float.NaN}, 3);
        java.util.function.DoubleUnaryOperator s4 = Functions.sampled(new float[]{12,11,10,9,8}, 1);
        java.util.function.DoubleUnaryOperator s5 = Functions.sampled(new float[]{8,35,40,45,50}, 0.36);

        PointCh p1f = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1t = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2f = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2t = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3f = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3t = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4f = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4t = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5f = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5t = new PointCh(SwissBounds.MIN_E+5, SwissBounds.MIN_N);

        Edge e1 = new Edge(0,1,p1f,p1t,2.7,s1);
        Edge e2 = new Edge(1,2,p2f,p2t,1,s2);
        Edge e3 = new Edge(2,3,p3f,p3t,3,s3);
        Edge e4 = new Edge(3,4,p4f,p4t,1,s4);
        Edge e5 = new Edge(4,5,p5f,p5t,0.36,s5);

        List<Edge> edges1 = new ArrayList<>();
        edges1.add(e1);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(e2);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(e3);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(e4);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(e5);

        Route route1 = new SingleRoute(edges1);
        Route route2 = new SingleRoute(edges2);
        Route route3 = new SingleRoute(edges3);
        Route route4 = new SingleRoute(edges4);
        Route route5 = new SingleRoute(edges5);

        List<Route> route12 = new ArrayList<>(List.of(route1,route2));
        List<Route> route345 = new ArrayList<>(List.of(route3,route4,route5));
        Route multiRoute1 = new MultiRoute(route12);
        Route multiRoute2 = new MultiRoute(route345);

        List<Route> finalRoute = new ArrayList<>(List.of(multiRoute1, multiRoute2));
        Route finalMultiRoute = new MultiRoute(finalRoute);

        //length = 8.06m
        assertEquals(50, finalMultiRoute.elevationAt(8.06));
        assertEquals(1, finalMultiRoute.elevationAt(-1));
    }

    @Test
    void nodeClosestTo() {
        java.util.function.DoubleUnaryOperator s1 = Functions.sampled(new float[]{1,2,3,4,5}, 2.7);
        java.util.function.DoubleUnaryOperator s2 = Functions.sampled(new float[]{5,4.5f,4,3,2}, 1);
        java.util.function.DoubleUnaryOperator s3 = Functions.sampled(new float[]{Float.NaN, Float.NaN}, 3);
        java.util.function.DoubleUnaryOperator s4 = Functions.sampled(new float[]{12,11,10,9,8}, 1);
        java.util.function.DoubleUnaryOperator s5 = Functions.sampled(new float[]{8,35,40,45,50}, 0.36);

        PointCh p1f = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1t = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2f = new PointCh(SwissBounds.MIN_E+1, SwissBounds.MIN_N);
        PointCh p2t = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3f = new PointCh(SwissBounds.MIN_E+2, SwissBounds.MIN_N);
        PointCh p3t = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4f = new PointCh(SwissBounds.MIN_E+3, SwissBounds.MIN_N);
        PointCh p4t = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5f = new PointCh(SwissBounds.MIN_E+4, SwissBounds.MIN_N);
        PointCh p5t = new PointCh(SwissBounds.MIN_E+5, SwissBounds.MIN_N);

        Edge e1 = new Edge(0,1,p1f,p1t,2.7,s1);
        Edge e2 = new Edge(1,2,p2f,p2t,1,s2);
        Edge e3 = new Edge(2,3,p3f,p3t,3,s3);
        Edge e4 = new Edge(3,4,p4f,p4t,1,s4);
        Edge e5 = new Edge(4,5,p5f,p5t,0.36,s5);

        List<Edge> edges1 = new ArrayList<>();
        edges1.add(e1);
        List<Edge> edges2 = new ArrayList<>();
        edges2.add(e2);
        List<Edge> edges3 = new ArrayList<>();
        edges3.add(e3);
        List<Edge> edges4 = new ArrayList<>();
        edges4.add(e4);
        List<Edge> edges5 = new ArrayList<>();
        edges5.add(e5);

        Route route1 = new SingleRoute(edges1);
        Route route2 = new SingleRoute(edges2);
        Route route3 = new SingleRoute(edges3);
        Route route4 = new SingleRoute(edges4);
        Route route5 = new SingleRoute(edges5);

        List<Route> route12 = new ArrayList<>(List.of(route1,route2));
        List<Route> route345 = new ArrayList<>(List.of(route3,route4,route5));
        Route multiRoute1 = new MultiRoute(route12);
        Route multiRoute2 = new MultiRoute(route345);

        List<Route> finalRoute = new ArrayList<>(List.of(multiRoute1, multiRoute2));
        Route finalMultiRoute = new MultiRoute(finalRoute);

        assertEquals(5, finalMultiRoute.nodeClosestTo(8));
        assertEquals(0, finalMultiRoute.nodeClosestTo(-1));
        assertEquals(2, finalMultiRoute.nodeClosestTo(3.7));
        assertEquals(1, finalMultiRoute.nodeClosestTo(3.199999));
        assertEquals(2, finalMultiRoute.nodeClosestTo(3.200001));

    }

}
