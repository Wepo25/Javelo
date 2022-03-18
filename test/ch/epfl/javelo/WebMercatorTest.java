package ch.epfl.javelo;

import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
    static final double DELTA = 1e-7;

    @Test
    void xMethodCorrect() {
        assertEquals(0.518275214444, WebMercator.x(Math.toRadians(6.5790772)), DELTA);

    }

    @Test
    void yMethodCorrect() {
        assertEquals(0.353664894749, WebMercator.y(Math.toRadians(46.5218976)), DELTA);

    }

    @Test
    void lonMethodCorrect() {
        assertEquals(Math.toRadians(6.5790772), WebMercator.lon(0.518275214444), DELTA);

    }

    @Test
    void latMethodCorrect() {
        assertEquals(Math.toRadians(46.5218976), WebMercator.lat(0.353664894749), DELTA);

    }

    @Test
    void xWorksOnKnownValues1() {
        var actual1 = WebMercator.x(Math.toRadians(6.5790772));
        var expected1 = 0.518275214444;
        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void yWorksOnKnownValues1() {
        var actual1 = WebMercator.y(Math.toRadians(46.5218976));
        var expected1 = 0.353664894749; // pas la bonne expected, je sais pas comment trouver la expected
        assertEquals(expected1, actual1, DELTA);
    }
}
