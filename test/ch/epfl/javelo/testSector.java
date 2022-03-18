package ch.epfl.javelo;


import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

    public class testSector {
        @Test
        void sectorsInAreaTest() {
            ByteBuffer sectorsBuffer = ByteBuffer.allocate(6 * 16384);
            for (int i = 0; i < 16384; i++) {
                sectorsBuffer.putInt(i*2);
                sectorsBuffer.putShort((short) 2);
            }
            GraphSectors graphSectors = new GraphSectors(sectorsBuffer);
            List<GraphSectors.Sector> expected = new ArrayList<GraphSectors.Sector>();
            expected.add(new GraphSectors.Sector(16383*2,16383*2+2));
            expected.add(new GraphSectors.Sector(16383*2-2, 16383*2));
            expected.add(new GraphSectors.Sector(16383*2-2-128*2, 16383*2-128*2));
            expected.add(new GraphSectors.Sector(16383*2-128*2, 16383*2+2-128*2));
            List<GraphSectors.Sector> actual = graphSectors.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2800);
            //still to test maxn et maxe !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            assertEquals(expected,actual );
            for (GraphSectors.Sector s : actual) {
                //System.out.println(s.startNodeId());
                //System.out.println(s.endNodeId());
                assertTrue(expected.contains(s));
            }


        }

        @Test
        void sectorsInAreaTest2() {
            ByteBuffer sectorsBuffer = ByteBuffer.allocate(6 * 16384);
            for (int i = 0; i < 16383; i++) {
                sectorsBuffer.putInt(i*2);
                sectorsBuffer.putShort((short) 2);
            }
            sectorsBuffer.putInt(16383*2);
            sectorsBuffer.putShort((short) 4);
            GraphSectors graphSectors = new GraphSectors(sectorsBuffer);
            List<GraphSectors.Sector> expected = new ArrayList<GraphSectors.Sector>();
            expected.add(new GraphSectors.Sector(16383*2,16383*2+4));
            expected.add(new GraphSectors.Sector(16383*2-2, 16383*2));
            expected.add(new GraphSectors.Sector(16383*2-2-128*2, 16383*2-128*2));
            expected.add(new GraphSectors.Sector(16383*2-128*2, 16383*2+2-128*2));
            List<GraphSectors.Sector> actual = graphSectors.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2800);
            assertEquals(expected,actual );
            for (GraphSectors.Sector s : actual) {
                //System.out.println(s.startNodeId());
                //System.out.println(s.endNodeId());
                assertTrue(expected.contains(s));
            }


        }

        @Test
        void sectorsInAreaTest3() {
            ByteBuffer sectorsBuffer = ByteBuffer.allocate(6 * 16384);
            for (int i = 0; i < 16384; i++) {
                sectorsBuffer.putInt(i*2);
                sectorsBuffer.putShort((short) 2);
            }

            GraphSectors graphSectors = new GraphSectors(sectorsBuffer);
            List<GraphSectors.Sector> expected = new ArrayList<GraphSectors.Sector>();
            expected.add(new GraphSectors.Sector(0,2));

            List<GraphSectors.Sector> actual = graphSectors.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 1726);
            assertEquals(expected,actual );
            for (GraphSectors.Sector s : actual) {
                assertTrue(expected.contains(s));
            }


        }
    }
