package com.sitecontroller.sitecontroller.common.util;

import java.util.Base64;

import com.sitecontroller.sitecontroller.common.Ubyte;

public final class HexUtils {

    private HexUtils() {
    }

    public static String convertToHexString(final String source) {
        return convertToHexString(Base64.getDecoder().decode(source));
    }

    public static String convertToHexString(final Ubyte b) {
        return convertToHexString(b.signedValue());
    }

    public static String convertToHexString(final byte b) {
        return padLeft(Integer.toHexString((int) (b & 0xFF)), 2, '0').toUpperCase();
    }

    public static String convertToHexString(final byte[] bytes) {
        return convertToHexString(bytes, 0, bytes.length);
    }

    public static String convertToHexString(final byte[] bytes, final int index, final int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = index; i < (index + count); i++) {
            builder.append(convertToHexString(bytes[i]));
        }

        return builder.toString();
    }

    public static byte[] convertFromHexString(final String source) {
        String adjSource = source;
        if (source.length() % 2 != 0) {
            adjSource = "0" + source;
        }

        byte[] bytes = new byte[adjSource.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Integer.decode("0x" + adjSource.substring(i * 2, ((i * 2) + 2))).byteValue();
        }

        return bytes;
    }

    public static String padLeft(String source, final int totalLength, final char paddingChar) {
        while (source.length() < totalLength) {
            source = paddingChar + source;
        }

        return source;
    }
}
