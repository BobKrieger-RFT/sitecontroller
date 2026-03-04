package com.sitecontroller.sitecontroller.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

    ACK((byte) 0),
    ALARM_ACK((byte) 1),
    REFERENCE_TAG_INFO((byte) 2),
    GET_CONFIG((byte) 20),
    KLV_CONFIG((byte) 21),
    TAG_CONFIG((byte) 22),
    GET_CONFIG_REPLY((byte) 23),
    FORCE_ALARM((byte) 39),
    FORCE_LOCATE((byte) 40),
    LOCATION_UPDATE((byte) 41),
    CLEAR_ALARM((byte) 42),
    FIRMWARE_UPGRADE((byte) 68),
    FIRMWARE_DATA((byte) 69),
    FIRMWARE_ACK((byte) 70),
    LORA_LOCATION_UPDATE((byte) 71),
    LORA_ALARM_ACK((byte) 72),
    LORA_CLEAR_ALARM((byte) 73);

    private byte value;
    private static Map<Byte, MessageType> map;

    static {
        map = new HashMap<Byte, MessageType>();
        for (MessageType mt : MessageType.values()) {
            map.put(mt.value(), mt);
        }
    }

    private MessageType(final byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static MessageType lookup(final byte value) {
        return map.get(new Byte(value));
    }
}
