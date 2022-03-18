package ch.epfl.javelo;

import ch.epfl.javelo.data.Attribute;
import ch.epfl.javelo.data.AttributeSet;
import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestAttributeSet {
    @Test
    void containsTest1() {
        AttributeSet s = new AttributeSet(1L);
        assertEquals(true, s.contains(Attribute.HIGHWAY_SERVICE));
    }

    @Test
    void attributeSetConstruction() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b10000000000000000000000000000000);
        });
    }

    @Test
    void intersectsTest() {
        AttributeSet s = new AttributeSet(0b00000000000010001000000000010100);
        AttributeSet b = new AttributeSet(0b00000000000010000000000010000000);
        assertEquals(true, s.intersects(b));
    }

    @Test
    void toStringTest() {
        AttributeSet s = new AttributeSet(0b10100);
        assertEquals("{highway=residential,highway=path}", s.toString());
    }


    @Test
    void of() {

        AttributeSet set =
                AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        AttributeSet set1 = new AttributeSet(131074L);
        assertEquals(set, set1);
    }

    @Test
    void contains() {
        AttributeSet set = new AttributeSet(131072L);
        assertEquals(true, set.contains(TRACKTYPE_GRADE1));
    }

    @Test
    void intersects() {
        AttributeSet set = new AttributeSet(131072L);
        assertEquals(true, set.intersects(new AttributeSet(131072L)));
    }

    @Test
    void testToString() {
        AttributeSet set =
                AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}",
                set.toString());
    }

    @Test
    void testToString1() {
        AttributeSet set =
                AttributeSet.of(TRACKTYPE_GRADE1, SURFACE_GRASS, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1,surface=grass}",
                set.toString());
    }
}
