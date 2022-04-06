package ch.epfl.javelo;

/**
 * This final not instantiable is used to test is some argument is acceptable or not.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class Preconditions {

    /**
     * Private constructor.
     */
    private Preconditions() {
    }

    /**
     * Allows us to check an IllegalArgumentException() for several classes while using cleaner code.
     *
     * @param shouldBeTrue - boolean :  Argument which we want to verify.
     * @throws IllegalArgumentException : An exception is thrown if the argument is false.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
