package com.sitecontroller.sitecontroller.common.enums;

import org.apache.commons.lang3.Strings;

/**
 * To be used when determining which server configuration we are using for the
 * MQTT broker.
 * <p>
 * Gateway Hosted(currently a primary multitech gateway)
 * Server Hosted(currently gateway passthrough to chirpstack)
 */
public enum MQTTBrokerConfiguration {
    GATEWAY_HOSTED("Gateway Hosted - Legacy", "lora/+/up", "lora/%s/down", "lora/%s/clear"),
    SERVER_HOSTED("Server Hosted - High Volume", "application/+/device/+/event/up",
            "application/%s/device/%s/command/down",
            "application/%s/device/%s/command/clear");

    private final String identifier;
    private final String subscriptionTopic;
    private final String downloadTopic;
    private final String clearTopic;

    MQTTBrokerConfiguration(final String identifier, final String subscriptionTopic, final String downloadTopic,
            final String clearTopic) {
        this.identifier = identifier;
        this.subscriptionTopic = subscriptionTopic;
        this.downloadTopic = downloadTopic;
        this.clearTopic = clearTopic;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSubscriptionTopic() {
        return subscriptionTopic;
    }

    public String getDownloadTopic() {
        return downloadTopic;
    }

    public String getClearTopic() {
        return clearTopic;
    }

    public static MQTTBrokerConfiguration determineBrokerConfiguration(final String brokerType) {
        for (MQTTBrokerConfiguration configuration : MQTTBrokerConfiguration.values()) {

            //Mods: Use Strings per deprecation of earlier method used in HelpAlert.
            if (Strings.CI.equals(configuration.identifier, brokerType)) {
                return configuration;
            }
        }
        return GATEWAY_HOSTED;
    }
}
