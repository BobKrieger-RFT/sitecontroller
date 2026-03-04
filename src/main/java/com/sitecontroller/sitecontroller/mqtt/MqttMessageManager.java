package com.sitecontroller.sitecontroller.mqtt;

/////import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sitecontroller.sitecontroller.common.enums.MQTTBrokerConfiguration;
//////import com.sitecontroller.sitecontroller.crypto.EncryptionDecryptionUtils;
import com.sitecontroller.sitecontroller.tag.net.TagMessageStatusEventListener;
import com.sitecontroller.sitecontroller.tag.message.dto.ChirpUpMessage;
import com.sitecontroller.sitecontroller.tag.net.TagMessageStatusEvent;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//////import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.util.Debug;

/////////import ch.qos.logback.classic.pattern.MessageConverter;

///////import java.io.ByteArrayOutputStream;
///////mport java.io.IOException;
////////import java.nio.charset.StandardCharsets;
///////import java.util.ArrayList;
//////import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
////import java.util.List;
import java.util.Map;
import java.util.Vector;
///////import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for managing incoming and outgoing MQTT messages, including connecting to the MQTT broker.
 * 
 * BK 2-25-2026 -- MODULE LEVERAGED FROM HELP ALERT (java8 repo).
 */
public class MqttMessageManager implements MqttCallbackExtended {

    //private static final int DECRYPTED_PACKET_MAC_ADDRESS_LENGTH = 6;
    private static final int DEFAULT_CONNECTION_POLL_SLEEP_TIME_SECONDS = 15;
    //private static final int DEFAULT_LOCATOR_BEACON_BATTERY_OFFSET = 1500;
    //private static final int LOCATOR_BEACON_BATTERY_MULTIPLIER = 10;
    private static final int MQTT_DEFAULT_CLIENT_CONNECTION_TIMEOUT_SECONDS = 30;
    private static final int MQTT_DEFAULT_CLIENT_KEEP_ALIVE_INTERVAL = 60;
    private static final int MQTT_DEFAULT_SUBSCRIBE_QOS = 0;

    private static final boolean MQTT_DEFAULT_CLIENT_CLEAN_SESSION = true;

    private static final Log logger = LogFactory.getLog(MqttMessageManager.class);
    private static final Map<String, Short> eventIdMap = new HashMap<>();

    //////private static final Map<String, Boolean> tagAlarmStateMap = new HashMap<>();
    //////private static final Map<String, Date> setTimeSinceBatteryInstalledMap = new HashMap<>();
    // NOTE:  Key here is lower case device EUI with no dashes.
    //////private static final Map<String, Integer> sequenceNumberMap = new HashMap<>();

    private final Object mainLockObject = new Object();
    private final Object clientLockObject = new Object();
    private InitializeMqttClientThread initializeMqttClientThread;

     //BK 2-2026 -- Not yet needed for AssetTracking - commenting out for now.
    //private MqttPublisher mqttPublisher;

    private MqttClient client;
    private MQTTBrokerConfiguration brokerConfiguration;

    private Vector<TagMessageStatusEventListener> statusEventListeners = new Vector<>();
    
    private String brokerId;
    private String brokerName;
    private String subscribeTopic = "";
    //private String downTopic = "";
    //private String clearTopic = "";

    // The default appEUI for Chirpstack v3 is "1", but we update this elsewhere depending on the
    // actual appEUI we see from the topic.
    private String appEUI = "1";
    private int subscribeQos = MQTT_DEFAULT_SUBSCRIBE_QOS;
    private boolean running = false;
    private boolean establishingInitialConnection = false;

    /**
     * Default constructor initializes all empty map contents.
     */
    //BK 2-2026 -- "MqttPublisher" not yet needed for AssetTracking - commenting out for now.
    public MqttMessageManager() {
        this(new HashMap<String, Short>(), new HashMap<String, Boolean>(), new HashMap<String, Date>());
    }

    /**
     * Alternate constructor must contain a definition for the mappings
     * of each device status value.
     *
     * @param eventIdMapping          A map of all LORA/BLE Devices and their last known Event Id values.
     * @param deviceAlarmMapping      A map of all LORA/BLE Devices and their last known alarm state (in alarm or not).
     * @param batteryInstalledTimeMap A map of all LORA/BLE Devices and their last known times since battery installation.
     */
    public MqttMessageManager(final Map<String, Short> eventIdMapping,
            final Map<String, Boolean> deviceAlarmMapping, final Map<String, Date> batteryInstalledTimeMap) {

        //BK 2-2026 -- Not yet needed for AssetTracking - commenting out for now.
        //As part of this, removed the mqttPublisher incoming parameter of this method.
        //setMqttPublisher(mqttPublisher);

        eventIdMap.putAll(eventIdMapping);

        //tagAlarmStateMap.putAll(deviceAlarmMapping);
        //setTimeSinceBatteryInstalledMap.putAll(batteryInstalledTimeMap);
    }

    /**
     * Establish the MQTT publisher for the message contents.
     * @param mqttPublisher The instance of the publisher thread for message contents.
     */
    //BK 2-2026 -- Not yet needed for AssetTracking - commenting out for now.
    /* public void setMqttPublisher(final MqttPublisher mqttPublisher) {
        this.mqttPublisher = mqttPublisher;
    }*/
    @Override
    public void connectComplete(final boolean reconnect, final String serverURI) {

        notifyConnectionEstablished(new TagMessageStatusEvent(this.brokerId,
                String.format("Gateway %s:  Connected to '%s', reconnect = %s", brokerName, serverURI, reconnect),
                reconnect));
        try {
            logger.debug(String.format("Subscribing to MQTT Topic '%s'", subscribeTopic));

            //
            this.client.subscribe(subscribeTopic, subscribeQos);

            } catch (MqttException mqttEx) {
            logger.error(mqttEx);
        }
    }

    @Override
    public void connectionLost(final Throwable arg0) {

        // The connection to the MQTT Broker was lost.
        String errMsg = arg0.getMessage();
        arg0.printStackTrace();

        // For this to be invoked, the broker had to have thrown an actual error.
        notifyConnectionClosed(new TagMessageStatusEvent(this.brokerId, "Mqtt Broker " + brokerName + ":  " + errMsg));

        // Dump out debug log output if it is enabled.
        if (logger.isDebugEnabled()) {
            if (this.client == null) {
                logger.error("MQTT CLIENT IS NULL UPON LOST CONNECTION!");
            } else if (this.client.getDebug() == null) {
                logger.error("MQTT CLIENT IS NULL, BUT CANNOT ACCESS DEBUG CONTENTS!");
            } else {
                Debug clientDebug = this.client.getDebug();
                logger.debug("CLIENT DISCONNECTED, ATTEMPTING TO DUMP DEBUG CONTENTS!");
                clientDebug.dumpClientDebug();
            }
        }
    }

    @Override
    public void deliveryComplete(final IMqttDeliveryToken arg0) {
        logger.debug("Delivery Complete For Id = " + arg0.getMessageId());
    }

    @Override
    public void messageArrived(final String topic, final MqttMessage msg) throws Exception {
        //logger.debug(String.format("Message Arrived From Topic %s, id = %d", topic, msg.getId()));

        //if (MQTTBrokerConfiguration.GATEWAY_HOSTED.getIdentifier().equals(brokerConfiguration.getIdentifier())) {
        if (MQTTBrokerConfiguration.SERVER_HOSTED.getIdentifier().equals(brokerConfiguration.getIdentifier())) {

            var x = 12345;

            ///logger.debug("Handling Lora Up Message");
            ///             
            //try {
            //    handleLoraUpMessage(msg);
            //} catch (Exception ex) {
           //     // PBI #915 will address how this problem should be communicated to a customer.
             //   ex.printStackTrace();
            //}            
        /////}
        //////else
        ///////{
            // Assume the topic is in the format of "application/+/device/+" and the appEUI can change
            // Always just use the appEUI from the message received topic to be safe.

            /*
            appEUI = topic.replace("application/", "").split("/")[0];
            logger.debug("Handling Chirp Up Message");
            try {
                handleChirpUpMessage(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex, ex.getCause());
            }
            */

            logger.warn("Handling limited topic, +/gateway/+/event/up, for testing purposes");

            /* VIP TO DO ---- 
            Once we've decided upon the exact payload that will be used for the "Chirp up" message
            corresponding to a "Site Controller" devices notification message, then we can implement this parser.
            
            try {
                handleChirpUpMessage(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex, ex.getCause());
            }
            */
        }
    }

    public void start() throws Exception {
        if (!running) {
            synchronized (mainLockObject) {
                if (!running) {
                    try {
                        running = true;
                        initializeMqttClientThread = new InitializeMqttClientThread(this);
                        initializeMqttClientThread.start();
                    } catch (Exception ex) {
                        running = false;

                        if (initializeMqttClientThread != null) {
                            try {
                                initializeMqttClientThread.interrupt();
                            } catch (Exception ex2) {
                                logger.error(ex2);
                            }
                        }

                        throw ex;
                    }
                }
            }
        }
    }

    public void resetConnection(final Map<String, Object> settingsMap, final String connectionUri,
            final boolean firstLaunch) {
        if (this.running) {
            logger.warn("Attempting To Disconnect MQTT Client From Broker.");
            synchronized (mainLockObject) {
                disconnectClient();
            }

            // Signal a warning of disconnect/reconnect effort.
            if (!firstLaunch) {
                String warningMsg = "Resetting Connection";
                notifyConnectionClosed(
                        new TagMessageStatusEvent(this.brokerId, "Gateway " + brokerName + ":  " + warningMsg, true));
            }

            // Now signal to the connection thread to start trying for a connection.
            logger.warn("Attempting To Connect MQTT Client To Broker...");
            //////////////initializeMqttClientThread.setSettingsMap(settingsMap);
            initializeMqttClientThread.setConnectionUri(connectionUri);
            setEstablishingInitialConnection(true);
        }
    }

    public void setEstablishingInitialConnection(final boolean establishingInitialConnection) {
        synchronized (clientLockObject) {
            this.establishingInitialConnection = establishingInitialConnection;
        }
    }

    public void stop() {
        if (running) {
            synchronized (mainLockObject) {
                if (running) {
                    // Shutdown connection first.
                    disconnectClient();
                    try {
                        running = false;
                        initializeMqttClientThread.interrupt();
                    } catch (Exception ex) {
                        logger.warn(ex);
                    } finally {
                        running = false;
                        initializeMqttClientThread = null;
                    }
                }
            }
        }
    }

    /* BK 2-2026 Commenting out methods migrated from HelpAlert: not yet needed for AssetTracking project. 
    public void send(final IMessage message, final String deviceEui, final int qos) {
        if (this.running && (this.client != null)) {
            if (MQTTBrokerConfiguration.GATEWAY_HOSTED.getIdentifier().equals(brokerConfiguration.getIdentifier())) {
                sendLoraDownMessage(message, deviceEui, qos);
            } else {
                sendChirpDownMessage(message, deviceEui, qos);
            }
        }
    }
    private void sendChirpDownMessage(final IMessage message, final String deviceEui, final int qos) {      
    }
    //BK 2-2026 --- Not yet needed for AssetTracking - method body not migrated.
    //private void sendLoraDownMessage(final IMessage message, final String deviceEui, final int qos){}    
    public void clearQueue(String deviceEui, final int qos) {
    }*/

    public boolean isClientConnected() {
        boolean isConnected = false;
        if (this.client != null) {
            isConnected = this.client.isConnected();
        }

        return isConnected;
    }

    // Event related methods.
    public void addEventListener(TagMessageStatusEventListener listener) {
        if (!statusEventListeners.contains(listener)) {
            statusEventListeners.add(listener);
        }
    }

    public void removeEventListener(TagMessageStatusEventListener listener) {
        statusEventListeners.remove(listener);
    }
    public String getBrokerId() {
        return this.brokerId;
    }
    public String getBrokerName() {
        return this.brokerName;
    }

    private void handleChirpUpMessage(final MqttMessage msg) throws Exception {

        byte[] payload = msg.getPayload();
        String packetString = new String(payload);
        logger.debug("msg Payload: " + packetString);
        ObjectMapper mapper = new ObjectMapper();
        logger.debug("AppEUI:  " + appEUI);

        ChirpUpMessage chirpUpMessage = mapper.reader().readValue(packetString, ChirpUpMessage.class);

        logger.info(String.format("Device EUI:  %s", chirpUpMessage.getDeviceInfo().getDevEui()));

        if (StringUtils.isBlank(chirpUpMessage.getData())) {
            logger.warn("Received ChirpUpMessage with no data, ignoring.");
            return;
        }

        try {
            byte[] decodedDataPacket = Base64.getDecoder().decode(chirpUpMessage.getData());

            //BK 2-2026 Slight change here ... using HexUtils from Tomcat which is already imported, instead of HexUtils from earlier HelpAlert code which was commented out.  
            // Should have same effect of converting byte array to hex string for logging purposes.
            String decodedDataPacketHexString = HexUtils.toHexString(decodedDataPacket);  

            logger.debug(String.format("Data Content (Encoded):  %s", chirpUpMessage.getData()));
            logger.info(String.format("Data Content (Decoded as Hex String):  %s", decodedDataPacketHexString));

            //BK 2-2026 --- Not yet needed for AssetTracking - commenting out for now.
            /* // Create the message contents and publish them on the outbound thread.
            List<IMessage> messageList = generateReportMessageList(decodedDataPacket,
                    chirpUpMessage.getDeviceInfo().getDevEui(), chirpUpMessage.getRxInfo().get(0).getGatewayId(),
                    (byte) chirpUpMessage.getRxInfo().get(0).rssi);
            
            // Publish them on the outbound thread.
                logger.debug("MqttMessageManager:  Adding " + messageList.size() + " Packet(s) to Event List");
            mqttPublisher.addMessages(messageList);  */

        } catch (Exception ex) {
            logger.error(ex, ex.getCause());
            ex.printStackTrace();
        }
    }

    //BK 2-2026 --- Not yet needed for AssetTracking - method body not migrated.
    //private void handleLoraUpMessage(final MqttMessage msg) throws Exception {        
    //}
    private void notifyConnectionEstablished(TagMessageStatusEvent event) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionEstablished(event);
        }
    }
    private void notifyConnectionError(TagMessageStatusEvent event) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionError(event);
        }
    }
    private void notifyConnectionClosed(TagMessageStatusEvent event) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.connectionClosed(event);
        }
    }
    private void notifyMessageReceiveError(TagMessageStatusEvent event) {
        for (TagMessageStatusEventListener listener : statusEventListeners) {
            listener.messageReceiveError(event);
        }
    }

    private void disconnectClient() {
        if (client != null) {
            try {
                logger.debug("Step #1 for MQTT Client Disconnect:  Set Callback to NULL!");
                client.setCallback(null);
                if (client.isConnected()) {
                    logger.debug("Step #2 for MQTT Client Disconnect:  Call Disconnect Method!");
                    client.disconnect();
                } else {
                    logger.debug("Step #2 Skipped - MQTT Client is Already Disconnected!");
                }

                logger.debug("Step #3 for MQTT Client Disconnect:  Close the Connection!");
                client.close();
                logger.debug("MQTT Client Has Been Successfully Disconnected!");
            } catch (MqttException mqttEx) {
                logger.error(mqttEx);
            } finally {
                client = null;
            }
        }
    }

    //BK 2-2026 --- Not yet needed for AssetTracking - method body not migrated.
    /* private void submitToTopic(final MqttMessage msg, final String topic) throws MqttException {
        logger.debug("Publishing Message to Topic:  " + topic);
        this.client.publish(topic, msg);
    }*/
    //BK 2-2026 --- Not yet needed for AssetTracking - commenting out for now.  
    /*
    private String serializeJson(final Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }*/
    /* BK 2-2026 --- Not yet needed for AssetTracking - method body not migrated.    
    private byte[] generateCommandRawMessage(final IMessage message, final String deviceEui) throws Exception {
        if (message instanceof AlarmAckCommandMessage) {
            logger.debug("message to be sent is a Claimed");
            AlarmAckCommandMessage msg = (AlarmAckCommandMessage) message;
            msg.setType(MessageType.LORA_ALARM_ACK);
        } else if (message instanceof ClearCommandMessage) {
            logger.debug("message to be sent is an Clear");
            ClearCommandMessage msg = (ClearCommandMessage) message;
            msg.setType(MessageType.LORA_CLEAR_ALARM);
        }

        // Build the byte array contents, and then account for the alarm sequence number.
        byte[] byteArray = MessageConverter.convertToBytes(message);
        String formattedDevEUI = deviceEui.replace("-", "").toLowerCase();
        if (sequenceNumberMap.containsKey(formattedDevEUI)) {
            int seqInt = sequenceNumberMap.get(formattedDevEUI);
            byteArray = appendByte(byteArray, seqInt);
        }        
        return byteArray;
    }*/

    /* BK 2-2026 --- Not yet needed for AssetTracking - not migrated yet. 
    private byte[] appendByte(final byte[] initArray, final int toAppend) {       
    }    
    private List<IMessage> generateReportMessageList(final byte[] decryptedData, final String devEUI,
            final String gatewayExtendedUId, final byte gatewayRssi) throws Exception {
    }    
    private IMessage generateSingleReportMessage(final LoraMessageDecoder decoder, final LoraRawDataContents rawDataContents,
            final TagType tagType, final byte[] decryptedData, final String devEUI, final String gatewayExtendedUId,
            final byte gatewayRssi) throws Exception {
    }
    private void addGatewayInfoToReportMessage(final IMessage message, final String gatewayExtendedUId,final byte gwRSSI) {}
    private List<IMessage> generateMultipleReportMessages(final LoraRawDataContents rawDataContents,
            final byte[] decryptedData, final String devEUI, final String gatewayExtendedUId,
            final byte gatewayRssi) throws Exception {
    }    
    private short generateEventId(final String tagMacAddress, final boolean inAlarm) {        
    }    
    private boolean lastStatusInAlarm(final String tagMacAddress) {      
    }    
    private String displayPayload(final byte[] payload) {        
    }
    private void displayLoraUpMessage(final LoraUpMessage msg) {       
    }*/

    /**
     * This class executes as a separate thread in order to initialize the
     * main connection between this client and the MQTT Broker.
     */
    private static class InitializeMqttClientThread extends Thread {
        private final MqttMessageManager parent;
        private Map<String, Object> settingsMap;
        private String connectionUri;

        public InitializeMqttClientThread(final MqttMessageManager parent) {
            super("MqttMessageManager-" + parent.brokerName);
            this.parent = parent;
        }

        public void setSettingsMap(final Map<String, Object> settingsMap) {
            this.settingsMap = settingsMap;
        }

        public void setConnectionUri(final String connectionUri) {
            this.connectionUri = connectionUri;
        }

        @Override
        public void run() {
            try {
                while (parent.running) {
                    // Connect if trying to do so.
                    if (parent.establishingInitialConnection) {
                        // Attempt the connection.
                        logger.debug("Attempting to Establish Connection With MQTT Broker");
                        parent.setEstablishingInitialConnection(!connectToMqttBroker());
                    }

                    // Sleep regardless; either attempt again or just do a periodic sleep.
                    logger.debug(String.format("Sleep for %d seconds", DEFAULT_CONNECTION_POLL_SLEEP_TIME_SECONDS));
                    Thread.sleep(DEFAULT_CONNECTION_POLL_SLEEP_TIME_SECONDS * 1000L);
                }
            } catch (InterruptedException iEx) {
                logger.error(iEx);
            }
        }

        private boolean connectToMqttBroker() {
            try {
                String clientId = MqttClient.generateClientId();

                //TO DO
                /////if (settingsMap.containsKey("clientId")) {
                ///    clientId = (String) settingsMap.get("clientId");
                ///}

                // Form connection URI and form the client.
                MemoryPersistence persistence = new MemoryPersistence();
                parent.client = new MqttClient(connectionUri, clientId, persistence);
                parent.client.setCallback(parent);

                // Setup credentials if authorization is required.
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setConnectionTimeout(MQTT_DEFAULT_CLIENT_CONNECTION_TIMEOUT_SECONDS);
                connOpts.setAutomaticReconnect(true);

                int keepAliveInterval = MQTT_DEFAULT_CLIENT_KEEP_ALIVE_INTERVAL;
                //TO DO 
                //////if (settingsMap.containsKey("keepAliveInterval")) {
                ///////    keepAliveInterval = (Integer) settingsMap.get("keepAliveInterval");
                ///////}

                connOpts.setKeepAliveInterval(keepAliveInterval);
                boolean cleanSession = MQTT_DEFAULT_CLIENT_CLEAN_SESSION;
                //TO DO 
                ///if (settingsMap.containsKey("cleanSession")) {
                ////    cleanSession = (Boolean) settingsMap.get("cleanSession");
                ////}

                connOpts.setCleanSession(cleanSession);

                ///////connOpts.setUserName("rft-mqtt"); //"admin" //"rft-mqtt");
                ///////String strPassword = "RFT7901771";
                ////////connOpts.setPassword(strPassword.toCharArray());

                //TO DO
                /*connOpts.setCleanSession(cleanSession);
                if (settingsMap.containsKey("username") && settingsMap.containsKey("password")) {
                    connOpts.setUserName((String) settingsMap.get("username"));
                    connOpts.setPassword(
                            EncryptionDecryptionUtils.decrypt((String) settingsMap.get("password")).toCharArray());
                }
                // Record the subscription topic and qos values for later usage.
                int qos = MQTT_DEFAULT_SUBSCRIBE_QOS;
                if (settingsMap.containsKey("subQos")) {
                    qos = (Integer) settingsMap.get("subQos");
                }
                
                // Same at a class level
                parent.brokerId = ((String) settingsMap.get("brokerId"));
                parent.brokerName = ((String) settingsMap.get("brokerName"));

                // Currently only Gateway Hosted is supported.
                parent.brokerConfiguration = MQTTBrokerConfiguration.determineBrokerConfiguration(
                        ((String) settingsMap.get("brokerType")));
                parent.subscribeTopic = parent.brokerConfiguration.getSubscriptionTopic();

                parent.downTopic = parent.brokerConfiguration.getDownloadTopic();
                parent.clearTopic = parent.brokerConfiguration.getClearTopic();
                parent.subscribeQos = qos;
                */

                parent.brokerId = "testBrokerId";
                parent.brokerName = connectionUri; //"testBrokerName";
                parent.brokerConfiguration = MQTTBrokerConfiguration.SERVER_HOSTED; //.GATEWAY_HOSTED;    
                parent.subscribeQos = MQTT_DEFAULT_SUBSCRIBE_QOS;

                //TO DO: 3-2026, per schematic, we are anticipating the topic to be of the format:
                //
                //site\%s\devices\notification, 
                //i.e., wildcard this as "site/+/"devices/notification" for subscription purposes.  
                // For now, hardcoding to the Chirpstack topic for testing purposes.
                parent.subscribeTopic = "+/gateway/+/event/up"; //"+/application/+/device/+"; //"application/+/device/+"; //"/gw/+/status"; //"#"; //"/gw/+/status";
        
                // Now connect to the client and notify upon success.
                parent.client.connect(connOpts);

            } catch (Exception ex) {
                logger.error(ex);
                
                parent.notifyConnectionError(new TagMessageStatusEvent(parent.brokerId,
                        "Broker " + parent.brokerName + ":  " + ex.getMessage()));

                parent.disconnectClient();
            }

            boolean connectOk = false;
            if (parent.client != null) {
                connectOk = parent.client.isConnected();
            }

            logger.debug("Client Connected = " + connectOk);
            return connectOk;
        }
    }
}
