package com.sitecontroller.sitecontroller.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum TagType {

    UNDEFINED((byte) 0),
    REFERENCE((byte) 1),
    PENDANT((byte) 2),
    ASSET((byte) 3),
    MASS_NOTIFY((byte) 9),
    BLE_LOCATOR_BEACON((byte) 10),
    LORA_FOB((byte) 11),
    BLE_BEACON_MONITOR((byte) 12),
    LORA_BADGE((byte) 13),
    MINI_BADGE((byte) 14);

    private byte value;
    private static Map<Byte, TagType> map;

    static {
        map = new HashMap<Byte, TagType>();
        for (TagType tagType : TagType.values()) {
            map.put(new Byte(tagType.value()), tagType);
        }
    }

    TagType(final byte value) {
        this.value = value;
    }

    public final byte value() {
        return this.value;
    }

    public static final TagType lookup(final byte value) {
        return map.get(new Byte(value));
    }
}
