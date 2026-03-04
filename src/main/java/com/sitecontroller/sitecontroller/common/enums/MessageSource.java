package com.sitecontroller.sitecontroller.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageSource {

    TAG((byte) 0),
    PDA((byte) 1);

    private byte value;
    private static Map<Byte, MessageSource> map;

    static {
        map = new HashMap<Byte, MessageSource>();
        for (MessageSource ms : MessageSource.values()) {
            map.put(ms.value(), ms);
        }
    }

    private MessageSource(final byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static MessageSource lookup(final byte value) {
        return map.get(new Byte(value));
    }
}
