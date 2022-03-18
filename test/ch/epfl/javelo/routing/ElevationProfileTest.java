package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import java.util.DoubleSummaryStatistics;

import static org.junit.jupiter.api.Assertions.*;

public class ElevationProfileTest {
    private float[] rightTable = new float[] {2, 3, 4, 15};
    private float[] descentTable = new float[] {35, 30, 37, 30};
    private float[] constantTable = new float[] {1, 1, 1, 1};
    private float[] wrongTable2 = new float[]{1};
    private ElevationProfile e = new ElevationProfile(6, rightTable);
    private ElevationProfile f = new ElevationProfile(6, constantTable);
    private ElevationProfile a = new ElevationProfile(6, descentTable);


    @Test
    void falseConstructorCorrect() {
        float[] wrongTable1 = new float[0];
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, rightTable);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, constantTable);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(3, wrongTable1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(3, wrongTable2);
        });
    }

    @Test
    void minAndmaxCorrect(){
        assertEquals(2, e.minElevation());
        assertEquals(15, e.maxElevation());
        assertEquals(30, a.minElevation());
        assertEquals(37, a.maxElevation());
        assertEquals(1, f.minElevation());
        assertEquals(1, f.maxElevation());
    }

    @Test
    void AscentDescentCorrect(){
        assertEquals(13, e.totalAscent());
        assertEquals(0, e.totalDescent());
        assertEquals(0, f.totalAscent());
        assertEquals(0, f.totalDescent());
        assertEquals(12, a.totalDescent());
        assertEquals(7, a.totalAscent());
    }

    @Test
    void ElevationAt() {
        assertEquals(9.5, e.elevationAt(5));
        assertEquals(2, e.elevationAt(-3));
        assertEquals(2, e.elevationAt(0));
        assertEquals(15, e.elevationAt(1455));
        assertEquals(33.5, a.elevationAt(3));
        assertEquals(1, f.elevationAt(3));
        assertEquals(1, f.elevationAt(-12));

    }
}