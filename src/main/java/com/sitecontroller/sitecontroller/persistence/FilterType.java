package com.sitecontroller.sitecontroller.persistence;

import java.util.HashMap;
import java.util.Map;

public enum FilterType {

    UNDEFINED((byte) 0),
    EQUAL_TO((byte) 1),
    NOT_EQUAL_TO((byte) 2),
    LIKE((byte) 3),
    NOT_LIKE((byte) 4),
    GREATER_THAN((byte) 5),
    GREATER_THAN_OR_EQUAL_TO((byte) 6),
    LESS_THAN((byte) 7),
    LESS_THAN_OR_EQUAL_TO((byte) 8),
    IS_NULL((byte) 9),
    NOT_NULL((byte) 10),
    IN((byte) 11),
    NOT_IN((byte) 12);

    private byte value;
    private static Map<Byte, FilterType> map;

    static {
        map = new HashMap<Byte, FilterType>();
        for (FilterType filterType: FilterType.values()) {
            map.put(Byte.valueOf(filterType.value()), filterType);
        }
    }

    FilterType(final byte value) {
        this.value = value;
    }

    public final byte value() {
        return this.value;
    }

    public static final FilterType lookup(final byte value) {
        return map.get(Byte.valueOf(value));
    }
}
