package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
/*
private PointCh point1 = new PointCh(2485000, 1075000);
    private PointCh point2 = new PointCh(2486000, 1076000);
    private PointCh point3 = new PointCh(2487000, 1077000);
    private PointCh point4 = new PointCh(2488000, 1078000);
    private PointCh point5 = new PointCh(2489000, 1079000);
    private PointCh point6 = new PointCh(2490000, 1080000);
    private PointCh point7 = new PointCh(2491000, 1081000);
 */

public class MultiRouteTest2 {
    private PointCh point1 = new PointCh(2485000, 1075000);
    private PointCh point2 = new PointCh(2486000, 1075000);
    private PointCh point3 = new PointCh(2487000, 1075000);
    private PointCh point4 = new PointCh(2488000, 1075000);
    private PointCh point5 = new PointCh(2489000, 1075000);
    private PointCh point6 = new PointCh(2490000, 1075000);
    private PointCh point7 = new PointCh(2491000, 1075000);

    private Edge e1 = new Edge(0, 10, point1, point2, 1000, Functions.constant(5));
    private Edge e2 = new Edge(10, 20, point2, point3, 1000, Functions.constant(5));
    private Edge e3 = new Edge(20, 30, point3, point4, 1000, Functions.constant(5));
    private Edge e4 = new Edge(30, 40, point4, point5, 1000, Functions.constant(5));
    private Edge e5 = new Edge(40, 50, point5, point6, 1000, Functions.constant(5));
    private Edge e6 = new Edge(50, 60, point6, point7, 1000, Functions.constant(5));

    /*private Edge e2 = new Edge(10, 30, point2, point3, 1000, Functions.constant(5));
    private Edge e3 = new Edge(30, 50,  point3, point4, 1500, Functions.constant(5));


    private Edge e4 = new Edge(50, 55, point4, point5, 3500, Functions.constant(5));
    private Edge e5 =new Edge(55, 70, point5, point6, 1000, Functions.constant(5));


     */
    public List<Edge> edge1 = new ArrayList<>();
    public List<Edge> edge2 = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();
    public List<PointCh> points = new ArrayList<>();


    MultiRoute m1;

    @Test
    void initialize() {
        edge1.add(e1); edge1.add(e2); edge1.add(e3);
        edge2.add(e4); edge2.add(e5);
        edges.add(e1); edges.add(e2); edges.add(e3); edges.add(e4); edges.add(e5);edges.add(e6);


        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        points.add(point5);
        points.add(point6);
        points.add(point7);

        List<Route> segments = new ArrayList<>();
        segments.add(new MultiRoute(List.of(new SingleRoute(List.of(e1)),new SingleRoute(List.of(e2)),new SingleRoute(List.of(e3)))));
        segments.add(new MultiRoute(List.of(new SingleRoute(List.of(e4)),new SingleRoute(List.of(e5)),new SingleRoute(List.of(e6)))));

        m1 = new MultiRoute(segments);
    }

    @Test
    public void indexOfSegmentAtCorrect() {
        initialize();

        assertEquals(0, m1.indexOfSegmentAt(-5));
        assertEquals(1, m1.indexOfSegmentAt(1500));
        assertEquals(1, m1.indexOfSegmentAt(2000)); // 1 ou 2
        assertEquals(5, m1.indexOfSegmentAt(5500));

    }
    @Test
    public void lengthWorks(){
        initialize();
        assertEquals(6000, m1.length());
    }
    @Test
    public void pointsWorks(){
        initialize();
        assertEquals(points , m1.points());
    }

    @Test
    void egdesCorrect() {
        initialize();
        assertEquals(edges, m1.edges());
    }
    @Test
    void pointAtWorks(){
        initialize();
        assertEquals(point1, m1.pointAt(0));
        assertEquals(new PointCh(2487010, 1075000), m1.pointAt(2010));
        assertEquals(point7, m1.pointAt(6000));
        assertEquals(point6, m1.pointAt(5000));
    }
    @Test
    void pointClosestToTest(){
        initialize();
        assertEquals(new RoutePoint(point1,0,0), m1.pointClosestTo(point1));
        assertEquals( new RoutePoint(new PointCh(2485000, 1075000),0,10),m1.pointClosestTo( new PointCh(2485000, 1075010)));
        assertEquals( new RoutePoint( new PointCh(2488100, 1075000),3100,100),m1.pointClosestTo( new PointCh(2488100, 1075100)));
    }
    @Test
    void nodeClosestToTest(){
        initialize();
        assertEquals(20, m1.nodeClosestTo(1501));
        assertEquals(60, m1.nodeClosestTo(8000));
        assertEquals(40, m1.nodeClosestTo(4500));
        assertEquals(0, m1.nodeClosestTo(-1));
    }

        @Test
        void indexOfSegmentAt(){

            Edge e1 = new Edge(0, 0, null, null, 10, null);
            Edge e2 = new Edge(0, 0, null, null, 10, null);
            Edge e3 = new Edge(0, 0, null, null, 500, null);

            List<Edge> listEdge = new ArrayList<>();
            List<Edge> listEdge2 = new ArrayList<>();

            listEdge.add(e1);
            listEdge.add(e2);
            listEdge2.add(e3);
            listEdge2.add(e3);

            SingleRoute singleRoute1 = new SingleRoute(listEdge);
            SingleRoute singleRoute2 = new SingleRoute(listEdge);
            SingleRoute singleRoute3 = new SingleRoute(listEdge2);


            List<Route> routeListe = new ArrayList<>();
            List<Route> routeListeComposee = new ArrayList<>();
            List<Route> routeListeComposee2 = new ArrayList<>();
            List<Route> routeListeComposee3 = new ArrayList<>();

            routeListeComposee2.add(singleRoute3);
            routeListeComposee2.add(singleRoute3);
            routeListeComposee2.add(singleRoute3);

            routeListeComposee3.add(singleRoute3);
            routeListeComposee3.add(singleRoute3);
            routeListeComposee3.add(singleRoute3);


            routeListe.add(singleRoute1);
            routeListe.add(singleRoute2);

            MultiRoute multiRoute = new MultiRoute(routeListe);
            MultiRoute multiRoute2 = new MultiRoute(routeListeComposee2);
            MultiRoute multiRoute3 = new MultiRoute(routeListeComposee3);
            List<Route> routeComposeede23 = new ArrayList<>();

            routeComposeede23.add(multiRoute2);
            routeComposeede23.add(multiRoute3);



            routeListeComposee.add(multiRoute);
            routeListeComposee.add(singleRoute1);
            routeListeComposee.add(singleRoute2);
            routeListeComposee.add(singleRoute2);
            routeListeComposee.add(singleRoute2);


            MultiRoute multiRouteComposee = new MultiRoute(routeListeComposee);
            MultiRoute multiRouteComposee2 = new MultiRoute(routeComposeede23);

            assertEquals(1, multiRoute.indexOfSegmentAt(22));
            assertEquals(0, multiRoute.indexOfSegmentAt(18));
            assertEquals(3, multiRouteComposee.indexOfSegmentAt(62));
            assertEquals(5, multiRouteComposee.indexOfSegmentAt(118));

        }
        @Test
        void lengthTest(){

            Edge e1 = new Edge(0, 0, null, null, 10, null);
            Edge e2 = new Edge(0, 0, null, null, 10, null);
            Edge e3 = new Edge(0, 0, null, null, 500, null);

            List<Edge> listEdge = new ArrayList<>();
            List<Edge> listEdge2 = new ArrayList<>();

            listEdge.add(e1);
            listEdge.add(e2);
            listEdge2.add(e3);
            listEdge2.add(e3);

            SingleRoute singleRoute1 = new SingleRoute(listEdge);
            SingleRoute singleRoute2 = new SingleRoute(listEdge);
            SingleRoute singleRoute3 = new SingleRoute(listEdge2);


            List<Route> routeListe = new ArrayList<>();
            List<Route> routeListeComposee = new ArrayList<>();
            List<Route> routeListeComposee2 = new ArrayList<>();
            List<Route> routeListeComposee3 = new ArrayList<>();

            routeListeComposee2.add(singleRoute3);
            routeListeComposee2.add(singleRoute3);
            routeListeComposee2.add(singleRoute3);

            routeListeComposee3.add(singleRoute3);
            routeListeComposee3.add(singleRoute3);
            routeListeComposee3.add(singleRoute3);


            routeListe.add(singleRoute1);
            routeListe.add(singleRoute2);

            MultiRoute multiRoute = new MultiRoute(routeListe);
            MultiRoute multiRoute2 = new MultiRoute(routeListeComposee2);
            MultiRoute multiRoute3 = new MultiRoute(routeListeComposee3);
            List<Route> routeComposeede23 = new ArrayList<>();

            routeComposeede23.add(multiRoute2);
            routeComposeede23.add(multiRoute3);



            routeListeComposee.add(multiRoute);
            routeListeComposee.add(singleRoute1);
            routeListeComposee.add(singleRoute2);
            routeListeComposee.add(singleRoute2);
            routeListeComposee.add(singleRoute2);


            MultiRoute multiRouteComposee = new MultiRoute(routeListeComposee);
            MultiRoute multiRouteComposee2 = new MultiRoute(routeComposeede23);

            assertEquals(120,multiRouteComposee.length());
            assertEquals(6000,multiRouteComposee2.length());

        }

        private final PointCh point1_ = new PointCh(2520000, 1145000);
        private final PointCh point2_ = new PointCh(2525000, 1145000);
        private final PointCh point3_ = new PointCh(2530000, 1145000);
        private final PointCh point4_ = new PointCh(2535000, 1145000);
        private final PointCh point5_ = new PointCh(2540000, 1145000);
        private final PointCh point6_ = new PointCh(2540000, 1146000);
        private final PointCh point7_ = new PointCh(2540000, 1147000);

        private final float[] sample1 = new float[]{
                1f, 2f, 3f, 4f, 5f
        };
        private final float[] sample2 = new float[]{
                5f, 4f, 3f, 2f, 1f
        };
        private final float[] sample4 = new float[]{
                5f, 6f, 7f, 8f, 9f
        };
        private final float[] sample5 = new float[]{
                9f, 10f, 11f, 10f, 9f
        };
        private final float[] sample6 = new float[]{
                9f, 8f, 7f, 8f, 9f
        };
        private final DoubleUnaryOperator profile1 = Functions.sampled(sample1, 5000);
        private final DoubleUnaryOperator profile2 = Functions.sampled(sample2, 5000);
        private final DoubleUnaryOperator profile4 = Functions.sampled(sample4, 5000);
        private final DoubleUnaryOperator profile5 = Functions.sampled(sample5, 1000);
        private final DoubleUnaryOperator profile6 = Functions.sampled(sample6, 1000);



        private MultiRoute createSimpleMultiRoute(){
            List<Edge> edgeList1 = new ArrayList<>();
            List<Edge> edgeList2 = new ArrayList<>();


            Edge e1 = new Edge(0, 1, point1_, point2_, 5000, profile1);
            Edge e2 = new Edge(1, 2, point2_, point3_, 5000, profile2);
            Edge e3 = new Edge(1, 2, point3_, point4_, 5000, profile1);
            Edge e4 = new Edge(2, 3, point4_, point5_, 5000, profile4);
            edgeList1.add(e1);
            edgeList1.add(e2);
            edgeList2.add(e3);
            edgeList2.add(e4);

            SingleRoute s1 = new SingleRoute(edgeList1);
            SingleRoute s2 = new SingleRoute(edgeList2);
            List<Route> sList = new ArrayList<>();
            sList.add(s1);
            sList.add(s2);

            MultiRoute m = new MultiRoute(sList);
            return m;
        }



        private MultiRoute createMultiMultiRoute(){
            List<Edge> edgeList3 = new ArrayList<>();

            Edge e5 = new Edge(3,4, point5_, point6_, 1000, profile5);
            Edge e6 = new Edge(4,5, point6_, point7_, 1000, profile6);

            edgeList3.add(e5);
            edgeList3.add(e6);

            List<Route> sList = new ArrayList<>();
            SingleRoute s3 = new SingleRoute(edgeList3);
            sList.add(s3);
            MultiRoute m1 = createSimpleMultiRoute();
            MultiRoute m2 = new MultiRoute(sList);
            List<Route> mList = new ArrayList<>();
            mList.add(m1);
            mList.add(m2);
            return new MultiRoute(mList);
        }

        @Test
        void testPointsOnSimpleMultiRoute(){
            MultiRoute m = createSimpleMultiRoute();
            List<PointCh> expectedList = new ArrayList<>();
            expectedList.add(point1_);
            expectedList.add(point2_);
            expectedList.add(point3_);
            expectedList.add(point4_);
            expectedList.add(point5_);
            assertEquals(expectedList, m.points());
        }

        @Test
        void testPointsOnMultipleMultiRoute(){
            MultiRoute m = createMultiMultiRoute();
            List<PointCh> expectedList = new ArrayList<>();
            expectedList.add(point1_);
            expectedList.add(point2_);
            expectedList.add(point3_);
            expectedList.add(point4_);
            expectedList.add(point5_);
            expectedList.add(point6_);
            expectedList.add(point7_);
            assertEquals(expectedList, m.points());
        }
        //----------
        @Test
        void pointAtSimpleMultiRouteTest(){
            MultiRoute m = createSimpleMultiRoute();
            assertEquals(new PointCh(2520000, 1145000), m.pointAt(0));
            assertEquals(new PointCh(2521000, 1145000), m.pointAt(1000));
            assertEquals(new PointCh(2522000, 1145000), m.pointAt(2000));
            assertEquals(new PointCh(2525000, 1145000), m.pointAt(5000));
            assertEquals(new PointCh(2540000, 1145000), m.pointAt(21000));
            assertEquals(new PointCh(2520000, 1145000), m.pointAt(-100));
        }


        @Test
        void pointAtMultipleMultiRouteTest(){
            MultiRoute m = createMultiMultiRoute();
            assertEquals(new PointCh(2520000, 1145000), m.pointAt(0));
            assertEquals(new PointCh(2521000, 1145000), m.pointAt(1000));
            assertEquals(new PointCh(2522000, 1145000), m.pointAt(2000));
            assertEquals(new PointCh(2525000, 1145000), m.pointAt(5000));
            assertEquals(new PointCh(2540000, 1146000), m.pointAt(21000));
            assertEquals(new PointCh(2520000, 1145000), m.pointAt(-100));
            assertEquals(point7_, m.pointAt(30000));
        }

        @Test
        void elevationAtSimpleMultiRouteTest(){
            MultiRoute m = createSimpleMultiRoute();
            assertEquals(1, m.elevationAt(0));
            assertEquals(profile1.applyAsDouble(500), m.elevationAt(500));
            assertEquals(profile1.applyAsDouble(1000), m.elevationAt(1000));
            assertEquals(5, m.elevationAt(5000));
            assertEquals(1, m.elevationAt(10000));
            assertEquals(profile2.applyAsDouble(1000), m.elevationAt(6000));
            assertEquals(profile1.applyAsDouble(2000), m.elevationAt(12000));
            assertEquals(profile4.applyAsDouble(4000), m.elevationAt(19000));
            assertEquals(9, m.elevationAt(3000000));
            assertEquals(1, m.elevationAt(-10));

        }


        @Test
        void elevationAtMultipleMultiRouteTest(){
            MultiRoute m = createMultiMultiRoute();
            assertEquals(1, m.elevationAt(0));
            assertEquals(profile1.applyAsDouble(500), m.elevationAt(500));
            assertEquals(profile1.applyAsDouble(1000), m.elevationAt(1000));
            assertEquals(5, m.elevationAt(5000));
            assertEquals(1, m.elevationAt(10000));
            assertEquals(profile2.applyAsDouble(1000), m.elevationAt(6000));
            assertEquals(profile1.applyAsDouble(2000), m.elevationAt(12000));
            assertEquals(profile4.applyAsDouble(4000), m.elevationAt(19000));
            assertEquals(9, m.elevationAt(3000000));
            assertEquals(1, m.elevationAt(-10));

            assertEquals(profile5.applyAsDouble(100), m.elevationAt(20100));
            assertEquals(9, m.elevationAt(20000));
            assertEquals(profile6.applyAsDouble(500), m.elevationAt(21500));
            assertEquals(9, m.elevationAt(22000));


        }


        @Test
        void nodeClosestSimpleMultiRouteTest(){
            MultiRoute m = createSimpleMultiRoute();
            assertEquals(0, m.nodeClosestTo(0));
            assertEquals(0, m.nodeClosestTo(1000));
            assertEquals(1, m.nodeClosestTo(3000));
            assertEquals(1, m.nodeClosestTo(5000));
            assertEquals(1, m.nodeClosestTo(6000));
            assertEquals(2, m.nodeClosestTo(8000));
            assertEquals(1, m.nodeClosestTo(7500));
            assertEquals(3, m.nodeClosestTo(100000));
            assertEquals(0, m.nodeClosestTo(-10));
        }



        @Test
        void nodeClosestMultipleMultiRouteTest(){
            MultiRoute m = createMultiMultiRoute();
            assertEquals(0, m.nodeClosestTo(0));
            assertEquals(0, m.nodeClosestTo(1000));
            assertEquals(1, m.nodeClosestTo(3000));
            assertEquals(1, m.nodeClosestTo(5000));
            assertEquals(1, m.nodeClosestTo(6000));
            assertEquals(2, m.nodeClosestTo(8000));
            assertEquals(1, m.nodeClosestTo(7500));
            assertEquals(5, m.nodeClosestTo(100000));
            assertEquals(0, m.nodeClosestTo(-10));
            assertEquals(4, m.nodeClosestTo(20600));
            assertEquals(5, m.nodeClosestTo(21600));
        }

        @Test
        void pointClosestToSimpleMultiRouteTest(){
            MultiRoute m = createSimpleMultiRoute();
            //assertEquals(new RoutePoint(point1, 0, 0), m.pointClosestTo(point1));

            PointCh pointTest1 = new PointCh(2523000, 1145000);
            assertEquals(new RoutePoint(pointTest1, 3000, 0), m.pointClosestTo(pointTest1));

            PointCh pointTarget1 = new PointCh(2523000, 1144000);
            assertEquals(new RoutePoint(pointTest1, 3000, 1000), m.pointClosestTo(pointTarget1));

            PointCh pointTarget2 = new PointCh(2534000, 1144000);
            PointCh pointTest2 = new PointCh(2534000, 1145000);
            assertEquals(new RoutePoint(pointTest2, 14000, 1000), m.pointClosestTo(pointTarget2));

            PointCh pointTarget3 = new PointCh(2540000, 1143000);
            PointCh pointTest3 = new PointCh(2540000, 1145000);
            assertEquals(new RoutePoint(pointTest3, 20000, 2000), m.pointClosestTo(pointTarget3));

            PointCh pointTarget4 = new PointCh(2535000, 1143000);
            PointCh pointTest4 = new PointCh(2535000, 1145000);
            assertEquals(new RoutePoint(pointTest4, 15000, 2000), m.pointClosestTo(pointTarget4));


        }


        @Test
        void pointClosestToMultipleMultiRoute(){
            MultiRoute m = createMultiMultiRoute();

            PointCh pointTest1 = new PointCh(2523000, 1145000);
            assertEquals(new RoutePoint(pointTest1, 3000, 0), m.pointClosestTo(pointTest1));

            PointCh pointTarget1 = new PointCh(2523000, 1144000);
            assertEquals(new RoutePoint(pointTest1, 3000, 1000), m.pointClosestTo(pointTarget1));

            PointCh pointTarget2 = new PointCh(2534000, 1144000);
            PointCh pointTest2 = new PointCh(2534000, 1145000);
            assertEquals(new RoutePoint(pointTest2, 14000, 1000), m.pointClosestTo(pointTarget2));

            PointCh pointTarget3 = new PointCh(2540000, 1143000);
            PointCh pointTest3 = new PointCh(2540000, 1145000);
            assertEquals(new RoutePoint(pointTest3, 20000, 2000), m.pointClosestTo(pointTarget3));

            PointCh pointTarget4 = new PointCh(2535000, 1143000);
            PointCh pointTest4 = new PointCh(2535000, 1145000);
            assertEquals(new RoutePoint(pointTest4, 15000, 2000), m.pointClosestTo(pointTarget4));

            assertEquals(new RoutePoint(point6_, 21000, 0), m.pointClosestTo(point6_));

            assertEquals(new RoutePoint(point7_, 22000, 0), m.pointClosestTo(point7_));

            PointCh pointTarget5 = new PointCh(2540500, 1146500);
            PointCh pointTest5 = new PointCh(2540000, 1146500);
            assertEquals(new RoutePoint(pointTest5, 21500, 500), m.pointClosestTo(pointTarget5));

        }
    }


