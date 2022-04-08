package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTestBis {
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


    private MultiRoute MultiRoute5segments() {
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

        List<Route> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);
        routes.add(route4);
        routes.add(route5);

        return new MultiRoute(routes);
    }

    private MultiRoute MultiMultiRoute() {
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

        return new MultiRoute(routes);
    }

    private List<Edge> MultiRoute5segmentEdges() {
        int fromNodeId1 = 0;
        int toNodeId11 = 10;
        int toNodeId12 = 15;
        double length11 = 101;
        double length12 = 10;
        DoubleUnaryOperator a1 = Functions.sampled(samples11, 100);
        DoubleUnaryOperator b1 = Functions.sampled(samples12, 100);
        Edge edge11 = new Edge(fromNodeId1, toNodeId11, route1start, route1etape, length11, a1);
        Edge edge12 = new Edge(toNodeId11,toNodeId12, route1etape, route1to2,length12,b1);

        int fromNodeId2 = 15;
        int toNodeId21 = 20;
        int toNodeId22 = 25;
        double length21 = 102;
        double length22 = 20;
        DoubleUnaryOperator a2 = Functions.sampled(samples21, 100);
        DoubleUnaryOperator b2 = Functions.sampled(samples22, 100);
        Edge edge21 = new Edge(fromNodeId2, toNodeId21, route1to2, route2etape, length21, a2);
        Edge edge22 = new Edge(toNodeId21,toNodeId22, route2etape, route2to3,length22,b2);

        int fromNodeId3 = 25;
        int toNodeId31 = 30;
        int toNodeId32 =35;
        double length31 = 103;
        double length32 = 30;
        DoubleUnaryOperator a3 = Functions.sampled(samples31, 100);
        DoubleUnaryOperator b3 = Functions.sampled(samples32, 100);
        Edge edge31 = new Edge(fromNodeId3, toNodeId31, route2to3, route3etape, length31, a3);
        Edge edge32 = new Edge(toNodeId31,toNodeId32, route3etape, route3to4,length32,b3);

        int fromNodeId4 = 35;
        int toNodeId41 = 40;
        int toNodeId42 =45;
        double length41 = 104;
        double length42 = 40;
        DoubleUnaryOperator a4 = Functions.sampled(samples41, 100);
        DoubleUnaryOperator b4 = Functions.sampled(samples42, 100);
        Edge edge41 = new Edge(fromNodeId4, toNodeId41, route3to4, route4etape, length41, a4);
        Edge edge42 = new Edge(toNodeId41,toNodeId42, route4etape, route4to5,length42,b4);

        int fromNodeId5 = 45;
        int toNodeId51 = 50;
        int toNodeId52 =55;
        double length51 = 105;
        double length52 = 50;
        DoubleUnaryOperator a5 = Functions.sampled(samples51, 100);
        DoubleUnaryOperator b5 = Functions.sampled(samples52, 100);
        Edge edge51 = new Edge(fromNodeId5, toNodeId51, route4to5, route5etape, length51, a5);
        Edge edge52 = new Edge(toNodeId51,toNodeId52, route5etape, route5end,length52,b5);

        List<Edge> edges = new ArrayList<>();

        edges.add(edge11);
        edges.add(edge12);
        edges.add(edge21);
        edges.add(edge22);
        edges.add(edge31);
        edges.add(edge32);
        edges.add(edge41);
        edges.add(edge42);
        edges.add(edge51);
        edges.add(edge52);

        return edges;
    }

    private List<PointCh> MultiRoute5segmentsPoints() {
        List<PointCh> points = new ArrayList<>();

        points.add(route1start);
        points.add(route1etape);
        points.add(route1to2);

        points.add(route2etape);
        points.add(route2to3);

        points.add(route3etape);
        points.add(route3to4);

        points.add(route4etape);
        points.add(route4to5);

        points.add(route5etape);
        points.add(route5end);

        return points;
    }

    private MultiRoute MultiRouteLength0() {
        int fromNodeId1 = 0;
        int toNodeId11 = 0;
        PointCh fromPoint11 = new PointCh(2485000, 1075000);
        PointCh toPointCh11 = new PointCh(2485000, 1075000);

        double length11 = 0;
        DoubleUnaryOperator a1 = null;
        Edge edge11 = new Edge(fromNodeId1, toNodeId11, fromPoint11, toPointCh11, length11, a1);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge11);
        SingleRoute route1 = new SingleRoute(edges1);

        List<Route> routes = new ArrayList<>();
        routes.add(route1);

        return new MultiRoute(routes);
    }

    @Test
    void indexOfSegmentAt() {
        MultiRoute route = MultiRoute5segments();
        MultiRoute multi = MultiMultiRoute();

        // l       : 111 122 133 144 155
        //     ==> 0 111 233 366 510 665
        //       | 0- 1-- 2-- 3-- 4---- |
        //
        // total length : 665

        // Expected
        int expected0 = 0;
        int expected1 = 0;
        int expected2 = 4;
        int expected3 = 2;
        int expected4 = 4;
        int expected5 = 2;
        int expected6 = 4;

        // SingleRoutes
        int actual0 = route.indexOfSegmentAt(-100);
        int actual1 = route.indexOfSegmentAt(0);
        int actual2 = route.indexOfSegmentAt(1000);
        int actual3 = route.indexOfSegmentAt(332.5);
        int actual4 = route.indexOfSegmentAt(510);
        int actual5 = route.indexOfSegmentAt(300);
        int actual6 = route.indexOfSegmentAt(665);

        // l       :  111 122   133 144  155
        //     ==> 0 (111 233) (366 510) 665
        //         | 0- 1-- 2-- 3-- 4---- |
        //
        // total length : 665

        //MultiRoutes & SingleRoutes
        int actual0m = multi.indexOfSegmentAt(-100);
        int actual1m = multi.indexOfSegmentAt(0);
        int actual2m = multi.indexOfSegmentAt(1000);
        int actual3m = multi.indexOfSegmentAt(332.5);
        int actual4m = multi.indexOfSegmentAt(510);
        int actual5m = multi.indexOfSegmentAt(300);
        int actual6m = multi.indexOfSegmentAt(665);

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4-1, actual4);
        assertEquals(expected5, actual5);
        assertEquals(expected6, actual6);

        assertEquals(expected0, actual0m);
        assertEquals(expected1, actual1m);
        assertEquals(expected2, actual2m);
        assertEquals(expected3, actual3m);
        assertEquals(expected4-1, actual4m);
        assertEquals(expected5, actual5m);
        assertEquals(expected6, actual6m);
    }

    @Test
    void length() {
        MultiRoute route5 = MultiRoute5segments();
        MultiRoute route0 = MultiRouteLength0();
        MultiRoute multi = MultiMultiRoute();

        assertEquals(665, route5.length());
        assertEquals(0, route0.length());
        assertEquals(665, multi.length());
    }

    @Test
    void edges() {
        MultiRoute route = MultiRoute5segments();
        MultiRoute multi = MultiMultiRoute();

        List<Edge> expected = MultiRoute5segmentEdges();
        List<Edge> actual0 = route.edges();
        List<Edge> actual1 = multi.edges();

        //assertEquals(expected, actual0);
        //assertEquals(expected, actual1);
    }

    @Test
    void points() {
        MultiRoute route = MultiRoute5segments();
        MultiRoute multi = MultiMultiRoute();

        List<PointCh> expected = MultiRoute5segmentsPoints();
        List<PointCh> actual1 = route.points();
        List<PointCh> actual2 = multi.points();

        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }

    @Test
    void pointAt() {
        //TODO
        MultiRoute route = MultiRoute5segments();
        MultiRoute multi = MultiMultiRoute();

        PointCh expected0 = new PointCh(2485000, 1075000); // start
        PointCh actual0 = route.pointAt(0);

        PointCh expected1 = new PointCh(2485550, 1075550); // end
        PointCh actual1 = route.pointAt(route.length());

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
    }

    @Test
    void elevationAt() {
        //TODO
    }

    @Test
    void nodeClosestTo() {

        // nodeId :         0 10  15  20  25  30  35  40  45  50  55
        // position :       0 101 111 213 233 336 366 470 510 615 665

        MultiRoute route = MultiRoute5segments();
        MultiRoute multi = MultiMultiRoute();

        // Expected
        int expected0 = 0;
        int expected1 = 0;
        int expected2 = 55;
        int expected3 = 55;
        int expected4 = 30;
        int expected5 = 30;

        // SingleRoutes
        int actual0 = route.nodeClosestTo(0);
        int actual1 = route.nodeClosestTo(-100);
        int actual2 = route.nodeClosestTo(665);
        int actual3 = route.nodeClosestTo(1000);
        int actual4 = route.nodeClosestTo(336);
        int actual5 = route.nodeClosestTo(330);

        // MultiRoutes & SingleRoutes
        int actual0m = multi.nodeClosestTo(0);
        int actual1m = multi.nodeClosestTo(-100);
        int actual2m = multi.nodeClosestTo(665);
        int actual3m = multi.nodeClosestTo(1000);
        int actual4m = multi.nodeClosestTo(336);
        int actual5m = multi.nodeClosestTo(330);

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
        assertEquals(expected5, actual5);

        assertEquals(expected0, actual0m);
        assertEquals(expected1, actual1m);
        assertEquals(expected2, actual2m);
        assertEquals(expected3, actual3m);
        assertEquals(expected4, actual4m);
        assertEquals(expected5, actual5m);
    }

    @Test
    void pointClosestTo() {
        //TODO
        Route route = MultiRoute5segments();
        Route multi = MultiMultiRoute();

        RoutePoint expected0 = null; // start
        RoutePoint expected1 = null; // end
    }
}
