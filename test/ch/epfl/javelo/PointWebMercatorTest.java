package ch.epfl.javelo;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointWebMercatorTest {

    static final double DELTA = 1e0;
    PointWebMercator pointWMIncorrect = new PointWebMercator(0.918275214444, 0.953664894749);
    PointWebMercator pointWMCorrect = new PointWebMercator(0.518275214444, 0.353664894749);
    PointCh pointTest = new PointCh(2500000, 1100000);

    @Test
    void constructorExceptions() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-0.5, 0.3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.3, -0.3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(1.3, 0.3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.3, 1.3);
        });
    }

    @Test
    void constructorNoException() {
        new PointWebMercator(1, 1);
        new PointWebMercator(0, 0);
        new PointWebMercator(0.8, 0.3);
    }

    @Test
    void xAtZoomLevelCorrect() {

        assertEquals(69561722, pointWMCorrect.xAtZoomLevel(19), DELTA);
    }

    @Test
    void yAtZoomLevelCorrect() {
        assertEquals(47468099, pointWMCorrect.yAtZoomLevel(19), DELTA);
    }

    @Test
    void lonCorrect() {
        assertEquals(Math.toRadians(6.5790772), pointWMCorrect.lon(), DELTA);
    }

    @Test
    void latCorrect() {
        assertEquals(Math.toRadians(46.5218976), pointWMCorrect.lat(), DELTA);
    }

    @Test
    void toPointChIncorrect() {
        double e = Ch1903.e(pointWMIncorrect.lon(), pointWMIncorrect.lat());
        double n = Ch1903.n(pointWMIncorrect.lon(), pointWMIncorrect.lat());

        assertEquals(null, pointWMIncorrect.toPointCh());
    }

    @Test
    void toAndofPointChCorrect() {
        PointCh pointCh1 = new PointCh(2500000, 1100000);
        assertEquals(pointCh1.e(), PointWebMercator.ofPointCh(pointCh1).toPointCh().e(), 2);
        assertEquals(pointCh1.n(), PointWebMercator.ofPointCh(pointCh1).toPointCh().n(), 2);

        PointCh pointCh2 = new PointCh(2485200, 1075200);
        PointWebMercator pointWM = new PointWebMercator(WebMercator.x(pointCh2.lon()), WebMercator.y(pointCh2.lat()));
        assertEquals(pointCh2.e(), pointWM.toPointCh().e(), 3);
        assertEquals(pointCh2.n(), pointWM.toPointCh().n(), 3);
    }

    //private static final double DELTA = 1e-6;

    @Test
    void Testof() {
        var actual1 = PointWebMercator.of(19, 69561722, 47468099);
        assertEquals(actual1.x(), 0.518275214444, DELTA);
        assertEquals(actual1.y(), 0.353664894749, DELTA);
    }
    

    @Test
    void yAtZoomLevel() {

        var actual1 = new PointWebMercator(0.518275214444, 0.353664894749);
        var actual2 = actual1.yAtZoomLevel(19);
        var expected1 = 47468099;
        assertEquals(expected1, actual2, DELTA);
    }

    @Test
    void toPointCh() {
        var actual1 = new PointWebMercator(0.518275214444, 0.353664894749);
        var actual2 = actual1.toPointCh();
        var expected1 = new PointCh(Ch1903.e(WebMercator.lon(0.518275214444), WebMercator.lat(0.353664894749)), Ch1903.n(WebMercator.lon(0.518275214444), WebMercator.lat(0.353664894749)));
        assertEquals(expected1, actual2);
    }

}

