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
        //assertEquals(2, e.minElevation());
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

    }@Test
    void lengthWorksWithNormalValues(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 2f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        assertEquals(5, elevProf.length());
    }
    @Test
    void minElevationWorksWithNormalValues(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 2f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
        assertEquals(2, elevProf.minElevation());
    }
    @Test
    void maxElevationWorksWithNormalValues(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 2f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
        assertEquals(102, elevProf.maxElevation());
    }
    @Test
    void totalAscentWorksWithNormalValues(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 2f};
        float [] samples2 = {0f, 5f, 7.5f, 10.2f, -2.5f, 2.3f, 12f, 4f, 15f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        ElevationProfile elev = new ElevationProfile(100, samples2);
        assertEquals(121.5f, elevProf.totalAscent());
        //assertEquals(35.7f, elev.totalAscent());
    }
    @Test
    void totalAscentWorksWithNonTrivialValues(){
        float[] elevationSamples = {0f, 0f, 10000f, 0f, 1f, 1f, 2f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        assertEquals(10002, elevProf.totalAscent());
    }
    @Test
    void totalAscentWorksWithTrivialArray(){
        float[] elevationSamples = {0f, 0f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        assertEquals(0, elevProf.totalAscent());
    }
    @Test
    void totalDescentWorksWithNormalValues(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 2f};
        ElevationProfile elevProf = new ElevationProfile(5, elevationSamples);
        assertEquals(121.5, elevProf.totalDescent());
    }
    @Test
    void elevationAtWorksWithExtremeCase(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 12.1f};
        ElevationProfile elevProf = new ElevationProfile(10, elevationSamples);

        assertEquals(2, elevProf.elevationAt(-1));
        assertEquals(12.1f, elevProf.elevationAt(11));
    }
    @Test
    void elevationAtWorksWithNormalCase(){
        float[] elevationSamples = {2f, 5.5f, 3f, 57f, 38f, 102f, 12f};
        ElevationProfile elevProf = new ElevationProfile(10, elevationSamples);
        ElevationProfile firstProfile = new ElevationProfile(10, new float[]{384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f});
        //assertEquals(384.6875f, firstProfile.elevationAt(1));
//        assertEquals(4.1f, elevProf.elevationAt(1));
    }


}
