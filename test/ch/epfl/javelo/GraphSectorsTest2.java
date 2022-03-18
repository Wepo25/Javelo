package ch.epfl.javelo;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GraphSectorsTest2 {

    @Test
    void sectorsInAreaTest(){

        ByteBuffer buffer = ByteBuffer.allocate(6*16684) ;

        for (int i=0; i<=16683;i+=1){
            buffer.putInt(i*6,i);
        }

        for (int j=0; j<= 16683; j+=1){
            buffer.putShort(j*6+4, (short) 5);
        }

        GraphSectors graphSectors = new GraphSectors(buffer);

        int expectedLength = 1 ;
        int actualLength = graphSectors.sectorsInArea(new PointCh( 2485000,1075000),0).size();
        assertEquals(expectedLength,actualLength);

        int expectedLength2 = 4 ;
        int actualLength2 = graphSectors.sectorsInArea(new PointCh(2485000+(SwissBounds.WIDTH/128),1075000+(SwissBounds.HEIGHT/128)),0).size();
        assertEquals(expectedLength2,actualLength2);

        int expectedStartNode1 = 0 ;
        int actualStartNode1 = graphSectors.sectorsInArea(new PointCh(2485000+(SwissBounds.WIDTH/128),1075000+(SwissBounds.HEIGHT/128)),0).get(0).startNodeId();
        int expectedEndNode1 = 4 ;
        int actualEndNode1 = graphSectors.sectorsInArea(new PointCh(2485000+(SwissBounds.WIDTH/128),1075000+(SwissBounds.HEIGHT/128)),0).get(0).endNodeId();
        assertEquals(expectedStartNode1,actualStartNode1);
        assertEquals(expectedEndNode1,actualEndNode1);

    }

    @Test
    void sectorInAreaTest2(){
        ByteBuffer buffer = ByteBuffer.allocate(6*16684) ;

        for (int i=0; i<=16683;i+=1){
            buffer.putInt(i*6,i);
        }

        for (int j=0; j<= 16683; j+=1){
            buffer.putShort(j*6+4, (short) 5);
        }

        GraphSectors graphSectors = new GraphSectors(buffer);
        int expectedLength3 = 15 ;
        int expectedStartNode3 = 128*46 + 36;
        int expectedEndNode3 = expectedStartNode3 + 5;

        int actualLength3 = graphSectors.sectorsInArea(new PointCh(2485000+(37.5*(SwissBounds.WIDTH /128)),1075000+48.5*(SwissBounds.HEIGHT/128)),(SwissBounds.WIDTH/128)).size() ;
        int actualStartNode3 = graphSectors.sectorsInArea(new PointCh(2485000+(37.5*(SwissBounds.WIDTH/128)),1075000+48.5*(SwissBounds.HEIGHT/128)),(SwissBounds.WIDTH/128)).get(0).startNodeId() ;
        int actualEndNode3 = graphSectors.sectorsInArea(new PointCh(2485000+(37.5*(SwissBounds.WIDTH/128)),1075000+48.5*(SwissBounds.HEIGHT/128)),(SwissBounds.WIDTH/128)).get(0).endNodeId() ;

        assertEquals(expectedLength3,actualLength3);
        assertEquals(expectedStartNode3,actualStartNode3);
        assertEquals(expectedEndNode3,actualEndNode3);
    }
    private static final double SECTOR_WIDTH = SwissBounds.WIDTH / 128;
    private static final double SECTOR_HEIGHT = SwissBounds.HEIGHT / 128;

    @Test
    void findingGoodSectorsTest(){

        byte[] data = generateSwissSectors();

        ByteBuffer b = ByteBuffer.wrap(data);
        GraphSectors gs = new GraphSectors(b);
        double x = 38.5 * SECTOR_WIDTH + SwissBounds.MIN_E;
        double y = 44.5 * SECTOR_HEIGHT + SwissBounds.MIN_N;
        PointCh pointCh = new PointCh(x, y);

        List<GraphSectors.Sector> list1 = gs.sectorsInArea(pointCh, 1);
        assertEquals(1, list1.size());
        assertEquals(38 + 44 * 128, list1.get(0).startNodeId());
        assertEquals(38 + 44 * 128 + 10, list1.get(0).endNodeId()); //secteurs en x pair contiennent 10 nodes

        List<GraphSectors.Sector> list2 = gs.sectorsInArea(pointCh, SECTOR_HEIGHT);
        assertEquals(9, list2.size());
        assertEquals(37 + 43 * 128, list2.get(0).startNodeId()); //en bas à droite du carré
        assertEquals(list2.get(0), list2.get(0)); //secteur en x impair contiennent 0 nodes (une random)
    }

    @Test
    void sectorOutOfSwiss(){
        byte[] data = generateSwissSectors();

        ByteBuffer b = ByteBuffer.wrap(data);

        GraphSectors gs = new GraphSectors(b);
        PointCh pointCh = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);

        //test que pas de out-of-bounds exception a été lancée
        List<GraphSectors.Sector> list = gs.sectorsInArea(pointCh, 1);
        assertEquals(1, list.size());
        assertEquals(0, list.get(0).startNodeId());
    }

    @Test
    void sectorLimitTest(){
        byte[] data = generateSwissSectors();

        ByteBuffer b = ByteBuffer.wrap(data);
        GraphSectors gs = new GraphSectors(b);
        double x = 12 * SECTOR_WIDTH + SwissBounds.MIN_E;
        double y = 12 * SECTOR_HEIGHT + SwissBounds.MIN_N;
        PointCh pointCh = new PointCh(x, y);

        //si on tombe sur la limite d'un sector, on le compte comme compris
        List<GraphSectors.Sector> list1 = gs.sectorsInArea(pointCh, SECTOR_HEIGHT);
        assertEquals(6, list1.size());

        List<GraphSectors.Sector> list2 = gs.sectorsInArea(pointCh, SECTOR_WIDTH);
        assertEquals(12, list2.size());
    }


    private byte[] generateSwissSectors(){
        byte[] data = new byte[128 * 128 * (4 + 2)];
        for(int y = 0; y < 128; ++y){
            for(int x = 0; x < 128; ++x){

                int sector = 128 * y + x;
                int p = sector * (4 + 2);

                data[p] = 0b00000000;
                data[p+1] = 0b00000000;
                data[p+2] = (byte) Bits.extractUnsigned(sector, 8, 8);
                data[p+3] = (byte) Bits.extractUnsigned(sector, 0, 8); //l'id du premier node d'un secteur est la position du secteur

                data[p+4] = 0b00000000;
                if(x % 2 == 0){ data[p+5] = 0b00001010; } //les secteurs en x pair contiennent 10 nodes
                else          { data[p+5] = 0b00000000; } //les secteurs en x impair contiennent 0 nodes
            }
        }
        return data;
    }

        // faire les 4 points aux extrémités
        // faire des points qui avec la distance tombe pile sur un autre secteur pour vérifier qu'il ne l'inclue pas
        //

        @Test
        void sectorsInAreaTest2() {

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
            //assertEquals(expectedLength2, actualLength2);

            int expectedStartNode1 = 0;
            int actualStartNode1 = graphSectors.sectorsInArea(new PointCh(2485000 + (SwissBounds.WIDTH / 128) -1, 1075000 + (SwissBounds.HEIGHT / 128)-1), 0).get(0).startNodeId();
            int expectedEndNode1 = 4;
            int actualEndNode1 = graphSectors.sectorsInArea(new PointCh(2485000 + (SwissBounds.WIDTH / 128)-1, 1075000 + (SwissBounds.HEIGHT / 128)-1), 0).get(0).endNodeId();
            assertEquals(expectedStartNode1, actualStartNode1);
            assertEquals(expectedEndNode1, actualEndNode1);

        }
        @Test
        void sectorInAreaTest3() {
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

    }
