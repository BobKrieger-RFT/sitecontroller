package com.sitecontroller.sitecontroller.common.util;

import com.sitecontroller.sitecontroller.common.enums.MqttClientProtocolType;
import com.sitecontroller.sitecontroller.common.i18n.ILocaleMessageProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;

public class MqttBrokerUtils {
    private static final int MQTT_DEFAULT_CLIENT_CONNECTION_TIMEOUT_SECONDS = 5;
    private static final int MQTT_CLIENT_TCP_DEFAULT_PORT = 1883;
    private static final int MQTT_CLIENT_SSL_DEFAULT_PORT = 8883;

    private static final Log logger = LogFactory.getLog(MqttBrokerUtils.class);

    private MqttBrokerUtils() {
        //not implemented
    }

    public static boolean testBrokerConnection(final MqttClientProtocolType mqttClientProtocolType,
            final String hostName, final Integer port, final String clientId, final Integer keepAliveInterval,
            final boolean cleanSession, final boolean isAuthRequired, final String username,
            final String password,
            final ILocaleMessageProvider localeMessageProvider) throws Exception {
        boolean connectOk = false;
        boolean exceptionThrown = false;
        MqttClient newClient = null;
        try {
            String theClientId = clientId;
            if ((clientId == null) || clientId.isEmpty()) {
                theClientId = MqttClient.generateClientId();
            }

            try (MemoryPersistence persistence = new MemoryPersistence()) {
                logger.info("Connecting to broker: " + hostName + ":" + port);
                String serverURI = formMqttBrokerUri(mqttClientProtocolType, hostName, port,
                        localeMessageProvider);
                logger.info(
                        String.format("Attempting to Connect with MQTT Broker using URI %s and Client Id %s", serverURI,
                                theClientId));

                newClient = new MqttClient(serverURI, theClientId, persistence);
            }

            // Establish connection options also!
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(cleanSession);
            connOpts.setKeepAliveInterval(keepAliveInterval);
            connOpts.setConnectionTimeout(MQTT_DEFAULT_CLIENT_CONNECTION_TIMEOUT_SECONDS);
            if (isAuthRequired) {
                connOpts.setUserName(username);
                connOpts.setPassword(password.toCharArray());
            }

            // Now connect to it already!
            MqttCallbackTest testCallback = new MqttCallbackTest();
            newClient.setCallback(testCallback);
            newClient.connect(connOpts);
        } catch (MqttException mqttEx) {
            logger.error(mqttEx.getMessage(), mqttEx);
            exceptionThrown = true;
            throw new Exception(mqttEx.getMessage());
        } finally {
            if (newClient != null) {
                if (!exceptionThrown) {
                    connectOk = newClient.isConnected();
                }

                newClient.disconnect();
                newClient.close();
            }

            logger.debug("Disconnected MQTT Client From Connection Testing!");
        }

        return connectOk;
    }

    public static String formMqttBrokerUri(final MqttClientProtocolType mqttClientProtocolType,
            final String hostName, final int port, final ILocaleMessageProvider localeMessageProvider) {

        return String.format("%s://%s%s",
                MqttClientProtocolTypeUtils.getFriendlyNameForType(mqttClientProtocolType, localeMessageProvider)
                        .toLowerCase(),
                hostName,
                formPortText(port, mqttClientProtocolType));

    }

    private static String formPortText(final int port, final MqttClientProtocolType mqttClientProtocolType) {
        String portText = "";
        if ((port == -1) && (mqttClientProtocolType == MqttClientProtocolType.TCP)) {
            portText = ":" + MQTT_CLIENT_TCP_DEFAULT_PORT;
        } else if ((port == -1) && (mqttClientProtocolType == MqttClientProtocolType.SSL)) {
            portText = ":" + MQTT_CLIENT_SSL_DEFAULT_PORT;
        } else if (port != -1) {
            portText = ":" + port;
        }

        return portText;
    }

    private static class MqttCallbackTest implements MqttCallback {

        public MqttCallbackTest() {
        }

        @Override
        public void connectionLost(final Throwable arg0) {
            logger.error("Connection Lost:  " + arg0.getMessage());
        }

        @Override
        public void deliveryComplete(final IMqttDeliveryToken arg0) {
            logger.debug("Delivery Complete:  " + arg0.getMessageId());
        }

        @Override
        public void messageArrived(final String s, final MqttMessage msg) throws Exception {
            logger.debug("Message Arrived:  " + s + Arrays.toString(msg.getPayload()));
        }
    }
}
