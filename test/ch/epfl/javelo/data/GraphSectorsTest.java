package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;
import static org.junit.jupiter.api.Assertions.*;


class GraphSectorsTest {

    // faire les 4 points aux extrémités
    // faire des points qui avec la distance tombe pile sur un autre secteur pour vérifier qu'il ne l'inclue pas
    //

    @Test
    void sectorsInAreaTest() {

        ByteBuffer buffer = ByteBuffer.allocate(6 * 16684);

        for (int i = 0; i <= 16683; i += 1) {
            buffer.putInt(i * 6, i);
        }

        for (int j = 0; j <= 16683; j += 1) {
            buffer.putShort(j * 6 + 4, (short) 5);
        }

        GraphSectors graphSectors = new GraphSectors(buffer);

        int expectedLength = 1;
        int actualLength = graphSectors.sectorsInArea(new PointCh(2485000, 1075000), 0).size();
        assertEquals(expectedLength, actualLength);

        int expectedLength2 = 4;
        int actualLength2 = graphSectors.sectorsInArea(new PointCh(2485000 + (SwissBounds.WIDTH / 128), 1075000 + (SwissBounds.HEIGHT / 128)), 0).size();
        assertEquals(expectedLength2, actualLength2);

        int expectedStartNode1 = 0;
        int actualStartNode1 = graphSectors.sectorsInArea(new PointCh(2485000 + (SwissBounds.WIDTH / 128), 1075000 + (SwissBounds.HEIGHT / 128)), 0).get(0).startNodeId();
        int expectedEndNode1 = 4;
        int actualEndNode1 = graphSectors.sectorsInArea(new PointCh(2485000 + (SwissBounds.WIDTH / 128), 1075000 + (SwissBounds.HEIGHT / 128)), 0).get(0).endNodeId();
        assertEquals(expectedStartNode1, actualStartNode1);
        assertEquals(expectedEndNode1, actualEndNode1);

    }
    @Test
    void sectorInAreaTest2() {
        ByteBuffer buffer = ByteBuffer.allocate(6 * 16684);

        for (int i = 0; i <= 16683; i += 1) {
            buffer.putInt(i * 6, i);
        }

        for (int j = 0; j <= 16683; j += 1) {
            buffer.putShort(j * 6 + 4, (short) 5);
        }

        GraphSectors graphSectors = new GraphSectors(buffer);
        int expectedLength3 = 15;
        int expectedStartNode3 = 128 * 46 + 36;
        int expectedEndNode3 = expectedStartNode3 + 5;

        int actualLength3 = graphSectors.sectorsInArea(new PointCh(2485000 + (37.5 * (SwissBounds.WIDTH / 128)), 1075000 + 48.5 * (SwissBounds.HEIGHT / 128)), (SwissBounds.WIDTH / 128)).size();
        int actualStartNode3 = graphSectors.sectorsInArea(new PointCh(2485000 + (37.5 * (SwissBounds.WIDTH / 128)), 1075000 + 48.5 * (SwissBounds.HEIGHT / 128)), (SwissBounds.WIDTH / 128)).get(0).startNodeId();
        int actualEndNode3 = graphSectors.sectorsInArea(new PointCh(2485000 + (37.5 * (SwissBounds.WIDTH / 128)), 1075000 + 48.5 * (SwissBounds.HEIGHT / 128)), (SwissBounds.WIDTH / 128)).get(0).endNodeId();

        assertEquals(expectedLength3, actualLength3);
        assertEquals(expectedStartNode3, actualStartNode3);
        assertEquals(expectedEndNode3, actualEndNode3);
    }

    @Test
    void GraphsSectorsWorksTrivial(){
        byte[] tab = new byte[48];
        for (byte i = 0; i<48; i++){
            tab[i] =  i;
        }
        ByteBuffer b = ByteBuffer.wrap(tab) ;
        List<GraphSectors.Sector> output = new ArrayList<GraphSectors.Sector>();

    }

    @Test
    void GraphSectorsWorksWith00() {

        byte[] tab = new byte[98304];

        for (int i = 0; i < 98304; i += 6) {

            tab[i] = (byte) Bits.extractUnsigned(i*4, 24, 8);
            tab[i + 1] = (byte) Bits.extractUnsigned(i*4, 16, 8);
            tab[i + 2] = (byte) Bits.extractUnsigned(i*4, 8, 8);
            tab[i + 3] = (byte) Bits.extractUnsigned(i*4, 0, 8);

            tab[i + 4] = (byte) 0;
            tab[i + 5] = (byte) 1;
        }
    }
    @Test
    void GraphSectorsWorksWithEntireMap(){

        byte[] tab = new byte[98304];

        for (int i = 0; i< 16384; i++){

            tab[i*6] = (byte) Bits.extractUnsigned(i*4, 24, 8);
            tab[6*i+1] = (byte) Bits.extractUnsigned(i*4, 16, 8);
            tab[6*i+2] = (byte) Bits.extractUnsigned(i*4, 8, 8);
            tab[6*i+3] = (byte) Bits.extractUnsigned(i*4, 0, 8);

            tab[6*i+4]= (byte) 0 ;
            tab[6*i+5] = (byte) 4;

        }

        ByteBuffer buffer = ByteBuffer.wrap(tab);

        GraphSectors graph = new GraphSectors(buffer);

        ArrayList<GraphSectors.Sector> output = new ArrayList<>();

        for (int j =0; j< 128; j++ ){
            for (int i = 0; i<128; i++){
                output.add(new GraphSectors.Sector((j*128+i)*4, (j*128+i+1)*4));
            }
        }

        List<GraphSectors.Sector> actual = graph.sectorsInArea(
                new PointCh(SwissBounds.MIN_E+SwissBounds.WIDTH/2 +10, SwissBounds.MIN_N+SwissBounds.HEIGHT/2 + 10 ), SwissBounds.WIDTH);

        assertArrayEquals(output.toArray(), actual.toArray());



    }}


