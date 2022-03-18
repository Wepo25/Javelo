package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.BitSet;
import java.util.StringJoiner;

public record AttributeSet(long bits) {

    /**
     * Constructor checking the validity of its argument.
     *
     * @param bits - long : A long containing the indexes of different Attributes.
     * @throw IllegalArgumentException (checkArgument) : Throws an exception if one of the indexes is greater than the number of Attributes.
     */
    public AttributeSet {

        long validBits = bits >> Attribute.COUNT;
        Preconditions.checkArgument(validBits == 0);
    }

    /**
     * This method allows us to store data in form of a long.
     *
     * @param attributes - Attribute ellipse : An array containing several attributes.
     * @return - AttributeSet : The long representation of these attributes indexes.
     */
    public static AttributeSet of(Attribute... attributes) {
        long result = 0L;
        for (Attribute attribute : attributes) {
            result += 1L << attribute.ordinal();
        }
        return new AttributeSet(result);
    }

    /**
     * This method allows us to check whether an attribute is stored inside this AttributeSet.
     *
     * @param attribute - Attribute : A given attribute.
     * @return - boolean : If this AttributeSet contains the given attribute index.
     */
    public boolean contains(Attribute attribute) {
        return (this.bits() & 1L << attribute.ordinal()) != 0;
    }

    /**
     * This method allows us to check whether two AttributeSets have some similar attributes.
     *
     * @param that - AttributeSet : Another set of attributes.
     * @return - boolean : If these two AttributeSets have at least one attribute in common.
     */
    public boolean intersects(AttributeSet that) {
        return (this.bits() & that.bits()) != 0;
    }

    /**
     * This method allows us to see the keys and values of the contained attributes.
     *
     * @return - String : A string representation of this AttributeSet's attributes keys and values.
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        BitSet bs = BitSet.valueOf(new long[]{this.bits()});
        int index = 0;
        for (int i = 0; i < bs.cardinality(); ++i) {
            index = (bs.cardinality() != 1) ? bs.nextSetBit(index + 1) : bs.nextSetBit(index);
            j.add(Attribute.ALL.get(index).keyValue());
        }
        return j.toString();
    }
}
