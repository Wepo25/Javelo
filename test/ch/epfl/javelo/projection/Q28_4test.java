package ch.epfl.javelo.projection;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4test {
    @Test
    void ofInt() {
        assertEquals(Q28_4.ofInt(42), 0b00000000000000000000001010100000);
        assertEquals(Q28_4.ofInt(0), 0b00000000000000000000000000000000);
        assertEquals(Q28_4.ofInt(108989878), 0b01100111111100001101101101100000);
    }

    @Test
    void asDouble() {
        assertEquals(Q28_4.asDouble(0b00000000000000000000000001100100), 6.25);
        assertEquals(Q28_4.asDouble(0b00000000000000000000000000000000), 0);
    }

    @Test
    void asFloat() {
        assertEquals(Q28_4.asFloat(0b00000000000000000000000001100100), 6.25);
        assertEquals(Q28_4.asFloat(0b00000000000000000000000000000000), 0);
    }
}
