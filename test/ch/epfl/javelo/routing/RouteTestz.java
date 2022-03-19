package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;



    public class RouteTestz {
        @Test
        void withPositionShiftedByTest(){
            RoutePoint routePoint = new RoutePoint(null,1,0.0);
            assertEquals(11,routePoint.withPositionShiftedBy(10).position());
            assertEquals(0,routePoint.withPositionShiftedBy(-1).position());
            assertEquals(1000,routePoint.withPositionShiftedBy(999).position());
        }

        @Test
        void minTest(){
            RoutePoint thisRoutePoint = new RoutePoint(null,1,10);
            RoutePoint thatRoutePoint = new RoutePoint(null,1,10);
            RoutePoint thatRoutePoint2 = new RoutePoint(null,1,1);
            RoutePoint thatRoutePoint3 = new RoutePoint(null,1,11);

            assertEquals(thisRoutePoint,thisRoutePoint.min(thatRoutePoint));
            assertEquals(thatRoutePoint2,thisRoutePoint.min(thatRoutePoint2));
            assertEquals(thisRoutePoint,thisRoutePoint.min(thatRoutePoint3));

        }
        @Test
        void minTest2() {
            RoutePoint thisRoutePoint = new RoutePoint(null, 1, 10);
            RoutePoint expectedRoutePoint = new RoutePoint(null, 5, 1);

            assertEquals(thisRoutePoint, thisRoutePoint.min(null, 4, 23));
            assertEquals(expectedRoutePoint, thisRoutePoint.min(null, 5, 1));
        }

    }

