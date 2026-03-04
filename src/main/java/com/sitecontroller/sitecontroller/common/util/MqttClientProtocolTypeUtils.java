package com.sitecontroller.sitecontroller.common.util;

import com.sitecontroller.sitecontroller.common.enums.MqttClientProtocolType;
import com.sitecontroller.sitecontroller.common.i18n.ILocaleMessageProvider;

public final class MqttClientProtocolTypeUtils {

    private MqttClientProtocolTypeUtils() {
    }

    public static String getFriendlyNameForType(final byte mqttClientProtocolType, final ILocaleMessageProvider localeMessageProvider) {
        return getFriendlyNameForType(MqttClientProtocolType.lookup(mqttClientProtocolType), localeMessageProvider);
    }

    public static String getFriendlyNameForType(final MqttClientProtocolType mqttClientProtocolType, final ILocaleMessageProvider localeMessageProvider) {
        String friendlyName = "undefined";
        if (mqttClientProtocolType == MqttClientProtocolType.TCP) {
            friendlyName = localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.TCP");
        }
        else if (mqttClientProtocolType == MqttClientProtocolType.SSL) {
            friendlyName = localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.SSL");
        }
        else if (mqttClientProtocolType == MqttClientProtocolType.LOCAL) {
            friendlyName = localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.LOCAL");
        }

        if ((friendlyName != null) && !friendlyName.isEmpty()) {
            friendlyName = friendlyName.toLowerCase();
        }

        return friendlyName;
    }

    public static String getUserFriendlyNameForType(final byte mqttClientProtocolType, final ILocaleMessageProvider localeMessageProvider) {
        return getUserFriendlyNameForType(MqttClientProtocolType.lookup(mqttClientProtocolType), localeMessageProvider);
    }

    public static String getUserFriendlyNameForType(final MqttClientProtocolType mqttClientProtocolType, final ILocaleMessageProvider localeMessageProvider) {
        String userFriendlyName = "undefined";
        if (mqttClientProtocolType == MqttClientProtocolType.TCP) {
            userFriendlyName = localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.TCP.userFriendlyName");
        }
        else if (mqttClientProtocolType == MqttClientProtocolType.SSL) {
            userFriendlyName = localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.SSL.userFriendlyName");
        }
        else if (mqttClientProtocolType == MqttClientProtocolType.LOCAL) {
            userFriendlyName = localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.LOCAL.userFriendlyName");
        }

        if ((userFriendlyName != null) && !userFriendlyName.isEmpty()) {
            userFriendlyName = userFriendlyName.toLowerCase();
        }

        return userFriendlyName;
    }

    public static MqttClientProtocolType getMqttClientProtocolTypeFromFriendlyName(final String friendlyName, final ILocaleMessageProvider localeMessageProvider) {
    	MqttClientProtocolType retType = MqttClientProtocolType.UNDEFINED;
        String nameToTest = friendlyName;
        if ((friendlyName != null) && !friendlyName.isEmpty()) {
            nameToTest = nameToTest.toLowerCase();
        }

        if (localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.TCP").equalsIgnoreCase(nameToTest)) {
            retType = MqttClientProtocolType.TCP;
        }
        else if (localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.SSL").equalsIgnoreCase(nameToTest)) {
            retType = MqttClientProtocolType.SSL;
        }
        else if (localeMessageProvider.getCurrentLocaleMessage("com.rft.pinpoint.mqtt.client.protocol.LOCAL").equalsIgnoreCase(nameToTest)) {
            retType = MqttClientProtocolType.LOCAL;
        }

        return retType;
    }
}
