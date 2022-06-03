package ch.epfl.javelo.projection;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    void ztest() {
        Integer i = 2;
        Integer w = 2;
        String o = "o";
        Arrays.stream(new int[] {1,2,3,4,5}).max().orElseThrow();
        Stream.of(2,"string").map( e -> ((Comparable) e).compareTo(2)).collect(Collectors.toList());
        }


}
