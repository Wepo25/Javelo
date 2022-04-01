
    package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

    public class MultiRouteTest {
        private PointCh point1 = new PointCh(2485000, 1075000);
        private PointCh point2 = new PointCh(2486000, 1076000);
        private PointCh point3 = new PointCh(2487000, 1077000);
        private PointCh point4 = new PointCh(2488000, 1078000);
        private PointCh point5 = new PointCh(2489000, 1079000);
        private PointCh point6 = new PointCh(2490000, 1080000);

        private Edge e1 = new Edge(0, 10, point1, point2, 3000, Functions.constant(5));
        private Edge e2 = new Edge(10, 30, point2, point3, 1000, Functions.constant(5));
        private Edge e3 = new Edge(30, 50,  point3, point4, 1500, Functions.constant(5));


        private Edge e4 = new Edge(50, 55, point4, point5, 3500, Functions.constant(5));
        private Edge e5 =new Edge(55, 70, point5, point6, 1000, Functions.constant(5));

        public List<Edge> edge1 = new ArrayList<>();
        public List<Edge> edge2 = new ArrayList<>();
        public List<Edge> edges = new ArrayList<>();
        List<PointCh> points = new ArrayList<>();

        MultiRoute m1;

        @Test
        void initialize() {
            edge1.add(e1); edge1.add(e2); edge1.add(e3);
            edge2.add(e4); edge2.add(e5);
            edges.add(e1); edges.add(e2); edges.add(e3); edges.add(e4); edges.add(e5);


            points.add(point1);
            points.add(point2);
            points.add(point3);
            points.add(point4);
            points.add(point5);
            points.add(point6);

            List<Route> segments = new ArrayList<>();
            segments.add(new MultiRoute(List.of(new SingleRoute(edge1))));
            segments.add(new MultiRoute(List.of(new SingleRoute(edge2))));

            m1 = new MultiRoute(segments);
        }

        private static final int ORIGIN_N = 1_200_000;
        private static final int ORIGIN_E = 2_600_000;
        private static final double EDGE_LENGTH = 100.25;

        private static final double TOOTH_EW = 1023;
        private static final double TOOTH_NS = 64;
        private static final double TOOTH_LENGTH = 1025;
        private static final double TOOTH_ELEVATION_GAIN = 100d;
        private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;

        @Test
        void indexOfSegmentAtCorrect() {
            initialize();

            assertEquals(0, m1.indexOfSegmentAt(-5));
            //assertEquals(2, m1.indexOfSegmentAt(5000));
            //assertEquals(2, m1.indexOfSegmentAt(5499));
            assertEquals(2, m1.indexOfSegmentAt(5500));
            assertEquals(4, m1.indexOfSegmentAt(9500));
            assertEquals(4, m1.indexOfSegmentAt(11000));
        }
        @Test
        void constructorThrowsOnEmptyList(){
            assertThrows(IllegalArgumentException.class, ()-> new MultiRoute(List.of()));
        }

        @Test
        void indexOfSegmentWorks(){
            ArrayList<Route> segments = new ArrayList<>();
            var singleRoute1 = new SingleRoute(verticalEdges(10));
            ArrayList<Route> soussegments = new ArrayList<>();
            soussegments.add(singleRoute1);
            soussegments.add(singleRoute1);
            soussegments.add(singleRoute1);
            segments.add(new MultiRoute(soussegments));
            segments.add(new MultiRoute(soussegments));
            MultiRoute route = new MultiRoute(segments);
            assertEquals(4, route.indexOfSegmentAt(4013));
            assertEquals(0, route.indexOfSegmentAt(0));
            assertEquals(0, route.indexOfSegmentAt(-3));
            assertEquals(5, route.indexOfSegmentAt(5500));
            assertEquals(5, route.indexOfSegmentAt(61100));
        }

        @Test
        void lengthWorks(){
            ArrayList<Route> segments = new ArrayList<>();
            var singleRoute1 = new SingleRoute(verticalEdges(10));
            ArrayList<Route> soussegments = new ArrayList<>();
            soussegments.add(singleRoute1);
            soussegments.add(singleRoute1);
            soussegments.add(singleRoute1);
            segments.add(new MultiRoute(soussegments));
            segments.add(new MultiRoute(soussegments));
            MultiRoute route = new MultiRoute(segments);

            assertEquals(6015, route.length());
        }


        @Test
        void pointAtWorks(){
            ArrayList<Route> segments = new ArrayList<>();
            var edgesCount = 4;
            var singleRoute = new SingleRoute(sawToothEdges(edgesCount));
            ArrayList<Route> soussegments = new ArrayList<>();
            soussegments.add(singleRoute);
            soussegments.add(singleRoute);
            segments.add(new MultiRoute(soussegments));
            segments.add(new MultiRoute(soussegments));

            MultiRoute route = new MultiRoute(segments);

            System.out.println(singleRoute.length());

        }


        private static List<Edge> verticalEdges(int edgesCount) {
            var edges = new ArrayList<Edge>(edgesCount);
            for (int i = 0; i < edgesCount; i += 1) {
                var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
                var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
                System.out.println("p1: " + p1);
                System.out.println("p2: " + p2);
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
        void lengthCorrect() {
            initialize();
            assertEquals(10000, m1.length());
        }

        @Test
        void egdesCorrect() {
            initialize();
            assertEquals(edges, m1.edges());
        }

        @Test
        void pointsCorrect() {
            initialize();
            assertEquals(points, m1.points());
        }

        @Test
        void pointAtCorrect() {

        }
    }

