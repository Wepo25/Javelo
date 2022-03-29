package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest2 {

    private List<Edge> edges = new ArrayList<>();
    private List<PointCh> points = new ArrayList<>();
    SingleRoute route = route();

    private SingleRoute route() {
        float[] samples1 = new float[]{200, 100, 150, 250,300};
        DoubleUnaryOperator profile1 = Functions.sampled(samples1, 100 * sqrt(2));
        Edge edge1 = new Edge(0, 4,
                new PointCh(2500100, 1100100), new PointCh(2500200, 1100200),
                100 * sqrt(2), profile1);
        points.add(new PointCh(2500100, 1100100));

        float[] samples2 = new float[]{0, 200, 100, 400,0};
        PointCh from2 = new PointCh(2500200, 1100200);
        PointCh to2 = new PointCh(2500000, 1100000);
        DoubleUnaryOperator profile2 = Functions.sampled(samples2, from2.distanceTo(to2));
        Edge edge2 = new Edge(4, 7,
                from2, to2, from2.distanceTo(to2), profile2);
        points.add(from2);
        points.add(to2);

        float[] samples3 = new float[]{300, 100, 200, 100,200};
        PointCh from3 = new PointCh(2500000, 1100000);
        PointCh to3 = new PointCh(2500400, 1100400);
        DoubleUnaryOperator profile3 = Functions.sampled(samples3, from3.distanceTo(to3));
        Edge edge3 = new Edge(7, 5,
                from3, to3, from3.distanceTo(to3), profile3);
        points.add(to3);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        SingleRoute route1 = new SingleRoute(edges);
        return route1;
    }


    @Test
    void indexOfSegmentAtTest() {
        assertEquals(0, route.indexOfSegmentAt(100000));
    }

    @Test
    void lengthTest() {
        assertEquals((100 + 200 + 400) * sqrt(2), route.length());
    }

    @Test
    void edgesTest() {
        assertIterableEquals(edges, route.edges());
    }

    @Test
    void pointsTest() {
        assertEquals(points, route.points());
    }

    @Test
    void pointAtTest() {
        PointCh expected = new PointCh(2500175, 1100175);
        PointCh actual = route.pointAt(75 * sqrt(2));
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());
        for (int i = 100; i <= 300; i += 5) {
            int p = i - 100;
            expected = new PointCh(2500200 - p, 1100200 - p);
            actual = route.pointAt(i * sqrt(2));
            assertEquals(expected.e(), actual.e());
            assertEquals(expected.n(), actual.n());
        }
        for (int i = 300; i <= 700; i += 5) {
            int p = i - 300;
            expected = new PointCh(2500000 + p, 1100000 + p);
            actual = route.pointAt(i * sqrt(2));
            assertEquals(expected.e(), actual.e());
            assertEquals(expected.n(), actual.n());
        }

        // can the position ecceed the lenght? what to do in that case
        expected = new PointCh(2500500, 1100500);
        actual = route.pointAt(800 * sqrt(2));
        //assertEquals(expected.e(), actual.e());
        //assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500100, 1100100);
        actual = route.pointAt(0);
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500100, 1100100);
        actual = route.pointAt(-100 * sqrt(2));
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500700, 1100700);
        actual = route.pointAt(1000 * sqrt(2));
        //assertEquals(expected.e(), actual.e());
        //assertEquals(expected.n(), actual.n());

    }

    @Test
    void elevationAtTest() throws IOException {
        List<Edge> edges2=new ArrayList<>();
        DoubleUnaryOperator profile2= Functions.constant(Double.NaN);
        edges2.add(new Edge(0,2,
                new PointCh(2500700, 1100700),new PointCh(2500500, 1100500),
                200*sqrt(2),profile2));
        SingleRoute route2=new SingleRoute(edges2);
        assertEquals(Double.NaN,route2.elevationAt(100));
        assertEquals(Double.NaN,route2.elevationAt(0));
        assertEquals(Double.NaN,route2.elevationAt(200));


        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        List<Edge> edges3=new ArrayList<>();
        //Pont Charles Bessières Lausanne
        edges3.add(new Edge(129480,129415,graph.nodePoint(129480),graph.nodePoint(129415),83.3,graph.edgeProfile(276838)));
        SingleRoute route3=new SingleRoute(edges3);

        assertEquals(Double.NaN,route3.elevationAt(50));
        assertEquals(Double.NaN,route3.elevationAt(0));
        assertEquals(Double.NaN,route3.elevationAt(-10));
        assertEquals(Double.NaN,route3.elevationAt(100));


        double expected = 250;
        double actual = route.elevationAt(75 * sqrt(2));
        assertEquals(expected, actual);

        expected = 200;
        actual = route.elevationAt(-25 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt(-1);
        assertEquals(expected, actual,10e-6);

        expected = 150;
        actual = route.elevationAt(50 * sqrt(2));
        assertEquals(expected, actual);

        expected = 200.0;
        actual = route.elevationAt(62.5 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 275;
        actual = route.elevationAt(87.5 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 287.5;
        actual = route.elevationAt(93.75 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 300;
        actual = route.elevationAt(99.999999999 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 0;
        actual = route.elevationAt(100 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 100;
        actual = route.elevationAt(125 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 150;
        actual = route.elevationAt(137.5 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 150;
        actual = route.elevationAt(175 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 100;
        actual = route.elevationAt(200 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt(275 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 300;
        actual = route.elevationAt(300 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt( 350* sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt( 1000* sqrt(2));
        assertEquals(expected, actual,10e-6);
    }

    @Test
    void nodeClosestToTest() {

        int expected = 0;
        int actual = route.nodeClosestTo(25 * sqrt(2));
        assertEquals(expected, actual);

        expected = 0;
        actual = route.nodeClosestTo(-25 * sqrt(2));
        assertEquals(expected, actual);

        expected = 0;
        actual = route.nodeClosestTo(-1);
        assertEquals(expected, actual);

        //expected = 0;
        //actual = route.nodeClosestTo(50 * sqrt(2));
        //assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(62.5 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(87.5 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(93.75 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(99.999999999 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(100 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(125 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(137.5 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(175 * sqrt(2));
        assertEquals(expected, actual);

        //expected = 4;
        //actual = route.nodeClosestTo(200 * sqrt(2));
        //assertEquals(expected, actual);

        expected = 7;
        actual = route.nodeClosestTo(275 * sqrt(2));
        assertEquals(expected, actual);

        expected = 7;
        actual = route.nodeClosestTo(300 * sqrt(2));
        assertEquals(expected, actual);

        expected = 7;
        actual = route.nodeClosestTo( 350* sqrt(2));
        assertEquals(expected, actual);

        expected = 5;
        actual = route.nodeClosestTo( 1000* sqrt(2));
        assertEquals(expected, actual);

        expected = 5;
        actual = route.nodeClosestTo( 675* sqrt(2));
        assertEquals(expected, actual);
    }

    @Test
    void pointClosestToTest() {
        RoutePoint expected = new RoutePoint(new PointCh(2500100, 1100100), 0, 0);
        RoutePoint actual = route.pointClosestTo(new PointCh(2500100, 1100100));
        assertEquals(expected, actual);

        expected = new RoutePoint(new PointCh(2500150, 1100150), 50*sqrt(2), 50*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500200, 1100100));
        assertEquals(expected, actual);

        expected = new RoutePoint(new PointCh(2500050, 1100050), 100*sqrt(2)+150*sqrt(2), 0);
        actual = route.pointClosestTo(new PointCh(2500050, 1100050));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500175, 1100175), 75*sqrt(2), 25*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500150, 1100200));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500200, 1100200), 100*sqrt(2), 0);
        actual = route.pointClosestTo(new PointCh(2500200, 1100200));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500100, 1100100), 0, 100*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500200, 1100000));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500005, 1100005), 100*sqrt(2)+195*sqrt(2), 5*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500000, 1100010));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500010, 1100010), 100*sqrt(2)+190*sqrt(2), 10*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500020, 1100000));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500300, 1100300), 300*sqrt(2)+300*sqrt(2), 100*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500200, 1100400));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500400, 1100400), 700*sqrt(2), 200*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500600, 1100600));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500400, 1100400), 700*sqrt(2), 200);
        actual = route.pointClosestTo(new PointCh(2500600, 1100400));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500400, 1100400), 700*sqrt(2), new PointCh(2500400, 1100400).distanceTo(new PointCh(2500600, 1100500)));
        actual = route.pointClosestTo(new PointCh(2500600, 1100500));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());
    }

    //Mimi

    PointCh p0 = new PointCh(2600100, 1200400);
    PointCh p1 = new PointCh(2600123, 1200456);
    PointCh p2 = new PointCh(2600456, 1200789);
    PointCh p3 = new PointCh(2600789, 1200123);
    PointCh p4 = new PointCh(2601000, 1201000);

    Edge myEdge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
    Edge myEdge2 = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Math.sin(x));
    Edge edgeBeforeRoute  =  new Edge(3, 4, p0, p1, p0.distanceTo(p1), x -> Double.NaN);



    public SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(myEdge1);
        edges.add(myEdge2);

        return new SingleRoute(edges);
    }

    @Test
    void indexOfSegmentAtMimi() {
        int expected = 0;
        int actual1 = ourRoute().indexOfSegmentAt(23456);
        int actual2 = ourRoute().indexOfSegmentAt(2343456);
        assertEquals(expected, actual1, "ATTENTION LÀÀÀÀ OH");
        assertEquals(expected, actual2, "ATTENTION LÀÀÀÀ OH");

    }

    @Test
    void constructorThrowsRightExceptionMimi() {
        List<Edge> edgesVide = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute route1 = new SingleRoute(edgesVide);
        });
    }

    @Test
    void lengthMimi() {
        double expected = 1215.5437527776;
        double actual = ourRoute().length();
        assertEquals(expected,actual,Math.pow(10,-5));
    }

    /*@Test
    void edges() {
        PointCh p1 = new PointCh(2600123, 1200456);
        PointCh p2 = new PointCh(2600456, 1200789);
        PointCh p3 = new PointCh(2600789, 1200123);
        PointCh p4 = new PointCh(2601000, 1201000);

        Edge edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        Edge edge2 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        List<Edge> expected = new ArrayList<>();
        expected.add(edge1);
        expected.add(edge2);

        List<Edge> actual = ourRoute().edges();
        for (int i = 0; i < actual.size(); i++) {
            System.out.println(expected.get(i));
            System.out.println(actual.get(i));
            assertEquals(expected.get(i),actual.get(i));
        }
    }*/

    @Test
    void pointsMimi() {
        List<PointCh> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);

        assertEquals(expected, ourRoute().points(), "lolilol c faux");

    }

    @Test
    void pointAtMimi() {

        PointCh pInter = myEdge1.pointAt(myEdge1.length()/2d); // expected intermediate point :
        PointCh actualInter = ourRoute().pointAt(myEdge1.length()/2d); // actual
        PointCh actual1 = ourRoute().pointAt(0); // the point at the beginning of our SingleRoute
        PointCh actual2 = ourRoute().pointAt(myEdge1.length()); //  the point between the first and second edges on our SingleRoute
        PointCh actual3 = ourRoute().pointAt(myEdge1.length() + myEdge2.length()); // the point at the edge of our SingleRoute
        PointCh actual3bis = ourRoute().pointAt(ourRoute().length()); // just another way to compute the point at the end of the route

        assertEquals(p1,actual1, "oh no…our code…it's broken");
        assertEquals(p2,actual2, "oh no…our code…it's broken");
        assertEquals(p3,actual3, "oh no…our code…it's broken");
        assertEquals(p3, actual3bis, "oh no…our code…it's broken");
        assertEquals(pInter, actualInter, "oh no…our code…it's broken");

    }

    @Test
    void pointAtWorksWithPointsBeyondRouteMiMi() {
        PointCh pointBeyondRoute = ourRoute().pointAt(ourRoute().length()+45);
        //PointCh pointBeforeRoute = ourRoute().pointAt(edgeBeforeRoute.length()/2d);
        PointCh pointBeforeRoute = ourRoute().pointAt(-10);
        assertEquals(p3, pointBeyondRoute, "oh no…our code…it's broken"); // a point right after the route
        assertEquals(p1, pointBeforeRoute, "oh no…our code…it's broken"); // a point right before the route
    }

    @Test
    void elevationAtMimi() {
        SingleRoute r = ourRoute();

        double actual1 = r.elevationAt(myEdge1.length()/2d);
        double actual2 = r.elevationAt(myEdge1.length()+(myEdge2.length()/3d));
        //System.out.println(myEdge2.elevationAt(myEdge2.length()/3d));

        assertTrue(Double.isNaN(actual1));
        assertEquals(Math.sin(myEdge2.length()/3d), actual2, Math.pow(10, -5));
    }

    @Test
    void nodeClosestToMimi() {
        // limit points (at the edges' extremity)
        assertEquals(1, ourRoute().nodeClosestTo(0));
        assertEquals(2, ourRoute().nodeClosestTo(myEdge1.length()));
        assertEquals(3, ourRoute().nodeClosestTo(ourRoute().length()));

        // testing intermediate points :

    }





















    // LES EDGES DE CES TEST NE SONT PAS COLLE!! :/
    @Test
    public void indexOfSegmentAt(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges1 =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute singleRoute = new SingleRoute(edges);
        int expected = 0;
        int actual0 = singleRoute.indexOfSegmentAt(4.5);
        //TEST 1:
        assertEquals(expected, actual0);
        int actual1 = singleRoute.indexOfSegmentAt(16);
        //TEST 2:
        assertEquals(expected, actual1);
    }

    @Test
    public void lengthWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        double expected0 = 16.6875;
        double actual0 = singleRoute0.length();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        double expected1 = 21.6875;
        double actual1 = singleRoute1.length();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void edgesWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        List<Edge> expected0 = edges0;
        List<Edge> actual0 = singleRoute0.edges();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        List<Edge> expected1 = edges1;
        List<Edge> actual1 = singleRoute1.edges();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void pointsWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        List<PointCh> expected0 = new ArrayList<>();
        expected0.add(edge0.fromPoint());
        expected0.add(edge0.toPoint());
        List<PointCh> actual0 = singleRoute0.points();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        List<PointCh> expected1 = new ArrayList<>();
        expected1.add(edge0.fromPoint());
        expected1.add(edge1.fromPoint());
        expected1.add(edge1.toPoint());
        List<PointCh> actual1 = singleRoute1.points();
        //TEST 2:
        //assertEquals(expected1, actual1);
    }

    @Test
    public void pointAtWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        PointCh expected0 = edge0.pointAt(4);
        PointCh actual0 = singleRoute0.pointAt(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        PointCh expected1 = edge1.pointAt(4);
        PointCh actual1 = singleRoute1.pointAt(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        PointCh expected2 = edge1.pointAt(5);
        PointCh actual2 = singleRoute1.pointAt(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        PointCh expected3 = edge0.pointAt(0);
        PointCh actual3 = singleRoute1.pointAt(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void elevationAtWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        double expected0 = edge0.elevationAt(4);
        double actual0 = singleRoute0.elevationAt(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        double expected1 = edge1.elevationAt(4);
        double actual1 = singleRoute1.elevationAt(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        double expected2 = edge1.elevationAt(5);
        double actual2 = singleRoute1.elevationAt(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        double expected3 = edge0.elevationAt(0);
        double actual3 = singleRoute1.elevationAt(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void nodeClosestToWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        int expected0 = edge0.fromNodeId();
        int actual0 = singleRoute0.nodeClosestTo(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        int expected1 = edge1.toNodeId();
        int actual1 = singleRoute1.nodeClosestTo(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        int expected2 = edge1.toNodeId();
        int actual2 = singleRoute1.nodeClosestTo(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        int expected3 = edge0.fromNodeId();
        int actual3 = singleRoute1.nodeClosestTo(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void rightPositionWorks(){
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,12,0,10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute singleRoute = new SingleRoute(edges);
        double position = 100000000;
        //TEST 1: Should return 16.6875
        //singleRoute.rightPosition(position);
        //double position1 = -100000000;
        //TEST 2: Should return 0.0
        //singleRoute.rightPosition(position1);
    }




























    //aya




    @Test
    public void pointAtShit() {
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),100, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),100, null);
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        SingleRoute s = new SingleRoute(l);
        PointCh pointCh = e1.pointAt(200);
        PointCh p = pointCh;
        //assertEquals(p.e(), s.pointAt(200).e(), 3);
        //assertEquals(p.n(), s.pointAt(200).n(), 3);
    }

    @Test
    public void pointAtWorksAya() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300, null);
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100, null);
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200, null);
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700, null);
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        assertEquals(p.e(), s.pointAt(10_000).e(), 3);
        assertEquals(p.n(), s.pointAt(10_000).n(), 3);
    }

    @Test
    public void pointAtWorks2() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300, null);
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100, null);
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200, null);
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700, null);
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(1);
        assertEquals(p.e(), s.pointAt(1).e(), 4);
        assertEquals(p.n(), s.pointAt(1).n(), 4);
    }

    @Test
    public void elevationAtReturnsNaNIfNoProfile(){
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        assertTrue(Float.isNaN((float) s.elevationAt(10_000)));
    }

    @Test
    public void elevationAtReturnsAltitudeAtGivenPosition() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0, 0, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199), 5800,
                Functions.constant(4589));
        Edge e1 = new Edge(0, 0, e0.pointAt(100), e0.pointAt(200), 2300,
                Functions.constant(234));
        Edge e2 = new Edge(0, 0, e1.pointAt(200), e1.pointAt(300), 1100,
                Functions.constant(643));
        Edge e3 = new Edge(0, 0, e2.pointAt(300), e2.pointAt(400), 2200,
                Functions.constant(0));
        Edge e4 = new Edge(0, 0, e3.pointAt(400), e3.pointAt(500), 1700,
                Functions.constant(2.3));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        double expected = 234;
        assertEquals(expected, s.elevationAt(5_900));
    }

    @Test
    public void elevationAtWorksWithNonConstantAltitudesOnInterval() {

        float[] elevationSamples = new float[]{384.75f, 384.6875f, 384.5625f, 384.5f};
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.sampled(elevationSamples, 2300));
        Edge e2 = new Edge(2,3,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        float expected = 384.7418f;
        assertEquals(expected, s.elevationAt(5900), 1e-3);
    }

    @Test
    public void pointClosestToWorksOnTrivialPoint() {
        PointCh p = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        RoutePoint rp = new RoutePoint(p, 0, 0);
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }

    @Test
    public void pointClosestToWorksOnHorizontalItinerary() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300);

        PointCh pp = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N);

        RoutePoint rp = new RoutePoint(pp, 300 , p.distanceTo(pp));
        System.out.println("p_distance_to +"+p.distanceTo(pp));
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),100,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N),100,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N),100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N),100,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N),100,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }

    @Test
    public void pointClosestToWorksOnDiagonalItinerary1() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 250, SwissBounds.MIN_N + 350);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300);
        RoutePoint rp = new RoutePoint(pp, 424.26, p.distanceTo(pp));

        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),141.42,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),141.42,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),141.42,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),141.42,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500),141.42,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }

    /*@Test
    public void pointClosestToWorksOnDiagonalItinerary2() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 220, SwissBounds.MIN_N + 350);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 285, SwissBounds.MIN_N + 285);
        RoutePoint rp = new RoutePoint(pp, 403.05, p.distanceTo(pp));
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),141.42,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),141.42,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),141.42,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),141.42,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500),141.42,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }*/

}