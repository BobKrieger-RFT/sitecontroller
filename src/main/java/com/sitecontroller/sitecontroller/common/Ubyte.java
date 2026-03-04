package com.sitecontroller.sitecontroller.common;

public class Ubyte extends Number {

    private static final long serialVersionUID = -4567768574539385758L;

    private byte value;

    public Ubyte(final byte value) {
        this.value = value;
    }

    public final byte signedValue() {
        return this.value;
    }

    public final short unsignedValue() {
        return (short) (this.value & 0xFF);
    }

    public static final Ubyte fromUnsignedValue(final short unsignedValue) {
        return new Ubyte(new Short(unsignedValue).byteValue());
    }

    @Override
    public final short shortValue() {
        return (short) signedValue();
    }

    @Override
    public final int intValue() {
        return (int) signedValue();
    }

    @Override
    public final long longValue() {
        return (long) signedValue();
    }

    @Override
    public final double doubleValue() {
        return (double) signedValue();
    }

    @Override
    public final float floatValue() {
        return (float) signedValue();
    }

    @Override
    public final String toString() {
        return Short.toString(unsignedValue());
    }
}
