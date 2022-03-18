package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTest {

    ByteBuffer edgesBuffer = ByteBuffer.allocate(10);


    IntBuffer profileIds = IntBuffer.wrap(new int[]{
            // Type : 3. Index du premier échantillon : 1.
            (1 << 30) | 1
    });

    ShortBuffer elevations = ShortBuffer.wrap(new short[]{
            (short) 0,
            (short) 0x180C, (short) 0xFEFF,
            (short) 0xFFFE, (short) 0xF000,
            (short) 0xFEFF, (short) 0xFFFE,
            (short) 0xF000, (short) 0xFEFF,
            (short) 0xFFFE, (short) 0xF000,
            (short) 0xFEFF, (short) 0xFFFE,
            (short) 0xF000,

    });


    @Test
    void isInverted() {
        edgesBuffer.putInt(0, 2147483635);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(!edges.isInverted(0));

    }

    @Test
    void targetNodeId() {
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(12, edges.targetNodeId(0));

    }

    @Test
    void length() {
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.6875, edges.length(0));
    }

    @Test
    void elevationGain() {
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.0, edges.elevationGain(0));
    }

    @Test
    void hasProfile() {
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);

        profileIds = IntBuffer.wrap(new int[]{(0 << 30) | 1});
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(!edges.hasProfile(0));

        profileIds = IntBuffer.wrap(new int[]{(1 << 30) | 1});
        edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(edges.hasProfile(0));

        profileIds = IntBuffer.wrap(new int[]{(2 << 30) | 1});
        edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(edges.hasProfile(0));

        profileIds = IntBuffer.wrap(new int[]{(3 << 30) | 1});
        edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(edges.hasProfile(0));

    }

    @Test
    void profileSamples() {
        float[] expectedSamples1 = new float[]{};
        float[] expectedSamples2 = new float[]{
                384.75f, 4079.9375f, 4095.875f, 3840.0f, 4079.9375f, 4095.875f,
                3840.0f, 4079.9375f, 4095.875f, 3840.0f,
        };
        float[] expectedSamples3 = new float[]{
                3840.0f, 4095.875f, 4079.9375f, 3840.0f, 4095.875f, 4079.9375f,
                3840.0f, 4095.875f, 4079.9375f, 384.75f,
        };
        float[] expectedSamples4 = new float[]{
                384.75f, 384.625f, 384.5625f, 384.5f, 384.375f, 383.375f, 383.375f,
                383.25f, 383.1875f, 383.125f

        };
        float[] expectedSamples5 = new float[]{
                383.125f, 383.1875f, 383.25f, 383.375f, 383.375f, 384.375f,
                384.5f, 384.5625f, 384.625f, 384.75f
        };
        float[] expectedSamples6 = new float[]{
                384.75f,384.6875f,384.5625f,384.5f,384.4375f,384.375f,
                384.3125f,384.25f,384.125f,384.0625f,
        };
        float[] expectedSamples7 = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        //2147483635
        //~12
        edgesBuffer.putInt(0, 2147483635);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(0 << 30) | 1});
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        //assertArrayEquals(expectedSamples1, edges.profileSamples(0));

        edgesBuffer.clear();
        edgesBuffer.putInt(0, 2147483635);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(1 << 30) | 1});
        edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(expectedSamples2, edges.profileSamples(0));

        edgesBuffer.clear();
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(1 << 30) | 1});
        edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(expectedSamples3, edges.profileSamples(0));

        edgesBuffer.clear();
        edgesBuffer.putInt(0, 2147483635);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(2 << 30) | 1});
        edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        //assertArrayEquals(expectedSamples4, edges.profileSamples(0));

        edgesBuffer.clear();
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(2 << 30) | 1});
        edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        //assertArrayEquals(expectedSamples5, edges.profileSamples(0));

        edgesBuffer.clear();
        edgesBuffer.putInt(0, 2147483635);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(3 << 30) | 1});
        edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        //assertArrayEquals(expectedSamples6, edges.profileSamples(0));

        edgesBuffer.clear();
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        profileIds = IntBuffer.wrap(new int[]{(3 << 30) | 1});
        edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        //assertArrayEquals(expectedSamples7, edges.profileSamples(0));

    }

    @Test
    void attributesIndex() {
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(2022, edges.attributesIndex(0));
    }

    @Test
        void isInverted2() {
            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

            assertTrue(edges.isInverted(0));
        }

        @Test
        void targetNodeId2(){
            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

            assertEquals(12, edges.targetNodeId(0));
        }

        @Test
        void length2() {
            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

            assertEquals(16.6875, edges.length(0));
        }

        @Test
        void elevationGain2() {
            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

            assertEquals(16.0, edges.elevationGain(0));
        }
        @Test
        void attributesIndex2() {
            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

            assertEquals(2022, edges.attributesIndex(0));
        }


        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };

        @Test
        void profileSamples2(){
            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

            assertArrayEquals(expectedSamples, edges.profileSamples(0));
        }


        void testAll() {


            ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
            edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
            edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
            edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
            edgesBuffer.putShort(8, (short) 2022);

            IntBuffer profileIds = IntBuffer.wrap(new int[]{
                    // Type : 3. Index du premier échantillon : 1.
                    (3 << 30) | 1
            });

            ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                    (short) 0,
                    (short) 0x180C, (short) 0xFEFF,
                    (short) 0xFFFE, (short) 0xF000
            });

            GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);


            assertTrue(edges.isInverted(0));
            assertEquals(12, edges.targetNodeId(0));
            assertEquals(16.6875, edges.length(0));
            assertEquals(16.0, edges.elevationGain(0));
            assertEquals(2022, edges.attributesIndex(0));
            float[] expectedSamples = new float[]{
                    384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                    384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
            };
            assertArrayEquals(expectedSamples, edges.profileSamples(0));

        }
    }

