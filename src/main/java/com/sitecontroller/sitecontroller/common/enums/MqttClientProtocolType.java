package com.sitecontroller.sitecontroller.common.enums;

import java.util.HashMap;
import java.util.Map;

import com.sitecontroller.sitecontroller.common.i18n.ILocaleMessageProvider;

public enum MqttClientProtocolType {
    UNDEFINED((byte) 0),
    TCP((byte) 1),
    SSL((byte) 2),
    LOCAL((byte) 3);

    private byte value;
    private static Map<Byte, MqttClientProtocolType> map;

    static {
        map = new HashMap<Byte, MqttClientProtocolType>();
        for (MqttClientProtocolType protocolType : MqttClientProtocolType.values()) {
            map.put(protocolType.value(), protocolType);
        }
    }

    private MqttClientProtocolType(final byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static MqttClientProtocolType lookup(final byte value) {
        return map.get(new Byte(value));
    }

    //String getFriendlyNameForType(MqttClientProtocolType mqttClientProtocolType,
     //       ILocaleMessageProvider localeMessageProvider) {
    //    // TO DO Auto-generated method stub
    //    throw new UnsupportedOperationException("Unimplemented method 'getFriendlyNameForType'");
    //}
}
