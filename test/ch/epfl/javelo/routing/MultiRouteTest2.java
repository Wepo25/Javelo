package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    }
}
