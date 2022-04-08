package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiRouteTestArthur {

    @Test
    void testInitializeSingleRoute(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 1000);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 1000, profile);

        l.add(edge1);

        SingleRoute s1 = new SingleRoute(l);
        List<Edge> l2 = new ArrayList<Edge>();

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 1000);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 1000, profile2);

        l2.add(edge2);

        SingleRoute s2 = new SingleRoute(l2);
        List<Edge> l3 = new ArrayList<Edge>();

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 1000);
        Edge edge3 = new Edge(2, 3, fromPoint3, toPoint3, 1000, profile3);

        l3.add(edge3);

        SingleRoute s3 = new SingleRoute(l3);

        List<Route> lRoute1 = new ArrayList<Route>();
        lRoute1.add(s1);
        lRoute1.add(s2);
        lRoute1.add(s3);

        MultiRoute m1 = new MultiRoute(lRoute1);

        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(new ArrayList<Route>());
        });
    }

    @Test
    void testMultiRouteFunctions(){
        List<Edge> allEdges = new ArrayList<>();
        List<PointCh> allPoints = new ArrayList<>();
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 1000);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 1000, profile);

        l.add(edge1);
        allEdges.add(edge1);
        allPoints.add(fromPoint);

        SingleRoute s1 = new SingleRoute(l);
        List<Edge> l2 = new ArrayList<Edge>();

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        float[] profileTab2 = {520f, 525f, 530f, 535f, 540f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 1000);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 1000, profile2);

        l2.add(edge2);
        allEdges.add(edge2);
        allPoints.add(fromPoint2);

        SingleRoute s2 = new SingleRoute(l2);
        List<Edge> l3 = new ArrayList<Edge>();

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N);
        float[] profileTab3 = {540f, 545f, 550f, 555f, 560f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 1000);
        Edge edge3 = new Edge(2, 3, fromPoint3, toPoint3, 1000, profile3);

        l3.add(edge3);
        allEdges.add(edge3);
        allPoints.add(fromPoint3);

        SingleRoute s3 = new SingleRoute(l3);

        List<Route> lRoute1 = new ArrayList<Route>();
        lRoute1.add(s1);
        lRoute1.add(s2);
        lRoute1.add(s3);

        MultiRoute m1 = new MultiRoute(lRoute1);

        List<Edge> l4 = new ArrayList<Edge>();

        PointCh fromPoint4 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N);
        PointCh toPoint4 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N);
        float[] profileTab4 = {560f, 565f, 570f, 575f, 580f};
        DoubleUnaryOperator profile4 = Functions.sampled(profileTab4, 1000);
        Edge edge4 = new Edge(3, 4, fromPoint4, toPoint4, 1000, profile4);

        l4.add(edge4);
        allEdges.add(edge4);
        allPoints.add(fromPoint4);

        SingleRoute s4 = new SingleRoute(l4);
        List<Edge> l5 = new ArrayList<Edge>();

        PointCh fromPoint5 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N);
        PointCh toPoint5 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N);
        float[] profileTab5 = {580f, 585f, 590f, 595f, 600f};
        DoubleUnaryOperator profile5 = Functions.sampled(profileTab5, 1000);
        Edge edge5 = new Edge(4, 5, fromPoint5, toPoint5, 1000, profile5);

        l5.add(edge5);
        allEdges.add(edge5);
        allPoints.add(fromPoint5);

        SingleRoute s5 = new SingleRoute(l5);
        List<Edge> l6 = new ArrayList<Edge>();

        PointCh fromPoint6 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N);
        PointCh toPoint6 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N);
        float[] profileTab6 = {600f, 605f, 610f, 615f, 620f};
        DoubleUnaryOperator profile6 = Functions.sampled(profileTab6, 1000);
        Edge edge6 = new Edge(5, 6, fromPoint6, toPoint6, 1000, profile6);

        l6.add(edge6);
        allEdges.add(edge6);
        allPoints.add(fromPoint6);
        allPoints.add(toPoint6);

        SingleRoute s6 = new SingleRoute(l6);

        List<Route> lRoute2 = new ArrayList<Route>();
        lRoute2.add(s4);
        lRoute2.add(s5);
        lRoute2.add(s6);

        MultiRoute m2 = new MultiRoute(lRoute2);

        List<Route> lRouteF = new ArrayList<Route>();
        lRouteF.add(m1);
        lRouteF.add(m2);

        MultiRoute m = new MultiRoute(lRouteF);

        assertEquals(6000, m.length());


        //Test pointAt()

        for(int i = 0; i <= 6000; i++){
            assertEquals(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), m.pointAt(i));
        }

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), m.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), m.pointAt(-3));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), m.pointAt(-2000));

        assertEquals(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), m.pointAt(6003));
        assertEquals(new PointCh(SwissBounds.MIN_E + 5001, SwissBounds.MIN_N), m.pointAt(5001));
        assertEquals(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), m.pointAt(7002));


        //Test elevationAt()

        for(int i = 0; i <= 6000; i++){
            assertEquals(500 + (0.02 * i), m.elevationAt(i), 1e-6);
        }

        assertEquals(500, m.elevationAt(-1), 1e-6);
        assertEquals(500, m.elevationAt(0), 1e-6);
        assertEquals(500, m.elevationAt(-1001), 1e-6);

        assertEquals(620, m.elevationAt(6001), 1e-6);
        assertEquals(620, m.elevationAt(7001), 1e-6);
        assertEquals(620, m.elevationAt(8001), 1e-6);

        //Test nodeClosestTo()

        for(int i = 0; i <= 6000; i++){
            if(i <= 500){
                assertEquals(0, m.nodeClosestTo(i));
            }else if(i <= 1500){
                assertEquals(1, m.nodeClosestTo(i));
            }else if(i <= 2500) {
                assertEquals(2, m.nodeClosestTo(i));
            }else if(i <= 3500) {
                assertEquals(3, m.nodeClosestTo(i));
            }else if(i <= 4500) {
                assertEquals(4, m.nodeClosestTo(i));
            }else if(i <= 5500) {
                assertEquals(5, m.nodeClosestTo(i));
            }else{
                assertEquals(6, m.nodeClosestTo(i));
            }
        }

        assertEquals(0, m.nodeClosestTo(-1));
        assertEquals(0, m.nodeClosestTo(-2));
        assertEquals(0, m.nodeClosestTo(-1001));


        assertEquals(6, m.nodeClosestTo(6001));
        assertEquals(6, m.nodeClosestTo(60012));


        //Test edges()
        assertEquals(allEdges, m.edges());

        //Test points()
        assertEquals(allPoints, m.points());

        //Test pointClosestTo()
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), 1, 1), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N), 1000, 20), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 20)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), 6000, Math.sqrt(20000)), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6100, SwissBounds.MIN_N + 100)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), 6000, 100), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 100)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N), 3000, 100), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 100)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 0, 10000), m.pointClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 10000)));


        //Test indexOfSegment()
        //One multi route with two routes inside (real length is 3000)
        for(int i = -1500; i < 4500; i++){
            if (i <= 1000) {
                assertEquals(0, m1.indexOfSegmentAt(i));
            }else if (i <= 2000) {
                assertEquals(1, m1.indexOfSegmentAt(i));
            }else if (i <= 3000) {
                assertEquals(2, m1.indexOfSegmentAt(i));
            }else{
                assertEquals(2, m1.indexOfSegmentAt(i));
            }
        }


        //One multi route with two sub multiroutes (real length is 6000)
        for(int i = -1500; i < 7500; i++){
            if (i <= 1000) {
                assertEquals(0, m.indexOfSegmentAt(i));
            }else if (i <= 2000) {
                assertEquals(1, m.indexOfSegmentAt(i));
            }else if (i <= 3000) {
                assertEquals(2, m.indexOfSegmentAt(i));
            }else if (i <= 4000) {
                assertEquals(3, m.indexOfSegmentAt(i));
            }else if (i <= 5000) {
                assertEquals(4, m.indexOfSegmentAt(i));
            } else if (i <= 6000) {
                assertEquals(5, m.indexOfSegmentAt(i));
            }else{
                assertEquals(5, m.indexOfSegmentAt(i));
            }
        }
    }
}