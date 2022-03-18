package ch.epfl.javelo;

public class TestGasp {
    public static void main(String[] s) {
        System.out.println(test(156));
    }

    public static int extractSigned(int value, int start, int length) {

        String bits = Integer.toBinaryString(value);
        String zero = "";
        for (int i = 0; i < 32 - bits.length(); i++) {
            zero += "0";
        }
        bits = zero + bits;

        for (int i = 0; i < 32 - length - start; i++) {
            bits = bits.substring(1) + "0";

        }
        for (int i = 0; i < 32 - length; i++) {
            bits = "1" + bits.substring(0, bits.length() - 1);
        }
        return Integer.parseInt(bits);
    }

    public static int extractUnsigned(int value, int start, int length) {

        String bits = Integer.toBinaryString(value);
        String zero = "";
        for (int i = 0; i < 32 - bits.length(); i++) {
            zero += "0";
        }
        bits = zero + bits;

        for (int i = 0; i < 32 - length - start; i++) {
            bits = bits.substring(1) + "0";
            System.out.println(bits);

        }
        for (int i = 0; i < 32 - length; i++) {
            bits = "0" + bits.substring(0, bits.length() - 1);
        }
        System.out.println(bits);
        return Integer.parseInt(bits);
    }

    public static double test(int value) {
        int rest = value >> 4;
        double bits = rest - 2 * Integer.highestOneBit(rest);
        double floating = 0;
        String BITS = Integer.toBinaryString(value << 28);
        for (int i = 0; i < BITS.length(); i++) {
            floating += Math.scalb((BITS.charAt(i) == '1') ? 1 : 0, -(i + 1));
        }
        return floating + rest;
    }

    public static float testfloat(int value) {
        int rest = value >> 4;
        float bits = rest - 2 * Integer.highestOneBit(rest);
        float floating = 0;
        String BITS = Integer.toBinaryString(value << 28);
        for (int i = 0; i < BITS.length(); i++) {
            floating += Math.scalb((BITS.charAt(i) == '1') ? 1 : 0, -(i + 1));
        }
        return floating + rest;
    }


}

