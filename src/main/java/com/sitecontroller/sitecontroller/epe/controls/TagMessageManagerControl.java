package com.sitecontroller.sitecontroller.epe.controls;

import com.sitecontroller.sitecontroller.common.enums.MqttClientProtocolType;
import com.sitecontroller.sitecontroller.common.enums.TagType;
import com.sitecontroller.sitecontroller.common.i18n.ILocaleMessageProvider;
import com.sitecontroller.sitecontroller.common.util.MqttBrokerUtils;
import com.sitecontroller.sitecontroller.epe.IEventRuntimeContext;
import com.sitecontroller.sitecontroller.epe.IEventRuntimeContextAware;
import com.sitecontroller.sitecontroller.epe.IEventRuntimeControl;
import com.sitecontroller.sitecontroller.epe.annotations.EventProcess;
import com.sitecontroller.sitecontroller.epe.annotations.ThreadSafe;
import com.sitecontroller.sitecontroller.mqtt.MqttMessageManager;

import com.sitecontroller.sitecontroller.persistence.entity.IMqttBrokerSettings;

import com.sitecontroller.sitecontroller.persistence.Filter;
import com.sitecontroller.sitecontroller.persistence.FilterType;
import com.sitecontroller.sitecontroller.persistence.ObjectFilter;

import com.sitecontroller.sitecontroller.persistence.entity.IMqttBrokerSettingsEntityManager;
import com.sitecontroller.sitecontroller.persistence.entity.IProfile;
import com.sitecontroller.sitecontroller.persistence.entity.ITag;
import com.sitecontroller.sitecontroller.persistence.entity.manager.IProfileEntityManager;
import com.sitecontroller.sitecontroller.persistence.entity.manager.ITagEntityManager;
import com.sitecontroller.sitecontroller.tag.net.MqttPublisherThreadDiedEvent;
import com.sitecontroller.sitecontroller.tag.net.MqttPublisherThreadDiedEventListener;
import com.sitecontroller.sitecontroller.tag.net.TagMessageDataEvent;
import com.sitecontroller.sitecontroller.tag.net.TagMessageDataEventListener;
import com.sitecontroller.sitecontroller.tag.net.TagMessageManager;
import com.sitecontroller.sitecontroller.tag.net.TagMessageStatusEvent;
import com.sitecontroller.sitecontroller.tag.net.TagMessageStatusEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
/// 
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TagMessageManagerControl implements IEventRuntimeContextAware, IEventRuntimeControl,
        MqttPublisherThreadDiedEventListener, TagMessageDataEventListener, TagMessageStatusEventListener {

    private static final Log logger = LogFactory.getLog(TagMessageManagerControl.class);
    private static final List<String> tagLoadRelatedEndsInit = new ArrayList<>();
    private static final String BASE_PROFILE_NAME = "Base Profile";

    private final ConcurrentHashMap<String, Object> tagLockObjectHash = new ConcurrentHashMap<>();
    
    private IEventRuntimeContext eventRuntimeContext;
    private IMqttBrokerSettingsEntityManager mqttBrokerSettingsEntityManager;
    private IProfileEntityManager profileEntityManager;
    private ITagEntityManager tagEntityManager;

    private List<MqttMessageManager> mqttMessageManagerList = new CopyOnWriteArrayList<>();
    private TagMessageManager tagMessageManager;
    private ILocaleMessageProvider localeMessageProvider;

    //private final MqttPublisher mqttPublisher;
    private String ipAddress;
    private int port;

    static {
        tagLoadRelatedEndsInit.add("lastTagAlert");
        tagLoadRelatedEndsInit.add("lastTagCheckin");
    }

    public TagMessageManagerControl() {
        //this.mqttPublisher = MqttPublisher.getInstance();
        //this.mqttPublisher.addDataEventListener(this);
        //this.mqttPublisher.addMqttPublisherThreadListener(this);
        //this.mqttPublisher.start();
    }

    @Inject
    public void setLocaleMessageProvider(ILocaleMessageProvider localeMessageProvider) {
        this.localeMessageProvider = localeMessageProvider;
    }

    @Inject
    public void setMqttBrokerSettingsEntityManager(IMqttBrokerSettingsEntityManager mqttBrokerSettingsEntityManager) {
        this.mqttBrokerSettingsEntityManager = mqttBrokerSettingsEntityManager;
    }

    @Inject
    public void setProfileEntityManager(IProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    @Inject
    public void setTagEntityManager(ITagEntityManager tagEntityManager) {
        this.tagEntityManager = tagEntityManager;
    }

    public final String getIpAddress() {
        return ipAddress;
    }
    public final void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public final int getPort() {
        return port;
    }
    public final void setPort(final int port) {
        this.port = port;
    }

    @Override
    public final void setEventRuntimeContext(final IEventRuntimeContext eventRuntimeContext) {
        this.eventRuntimeContext = eventRuntimeContext;
    }

    @Override
    public final void init() throws Exception {

        /*
        // First, lookup the Base Profile id value.
        Filter lookupFilter = new ObjectFilter("name", BASE_PROFILE_NAME, FilterType.EQUAL_TO);        
        //TO DO ... Try to make this work without hauling in all of the hibernate calls ...  
        ////////////String baseProfileId = "12345";        
        IProfile baseProfile = this.profileEntityManager.findByFilter1(lookupFilter, null);
        String baseProfileId = baseProfile.getId();               

        // First setup the tag managing component.
        if (StringUtils.isEmpty(this.ipAddress) || this.ipAddress.equals("0.0.0.0")) {
            this.tagMessageManager = new TagMessageManager(baseProfileId, null, this.port);
        } else {
            this.tagMessageManager = new TagMessageManager(baseProfileId, InetAddress.getAllByName(this.ipAddress)[0],
                    this.port);
        }
        this.tagMessageManager.addEventListener((TagMessageStatusEventListener) this);
        //TO DO: BK 2-2026, Commented out, not sure needed yet.
        //this.tagMessageManager.addEventListener((TagMessageDataEventListener) this);
        */

        //TO DO: Remove this bypass/hardcoded fakeout ...
        this.port = 1883; 

        try {
            this.tagMessageManager = new TagMessageManager("54321", InetAddress.getAllByName(this.ipAddress)[0], this.port);
            this.tagMessageManager.addEventListener((TagMessageStatusEventListener) this);
        }
        catch (Exception ex) {
            logger.error("Error During TagMessageManager Initialization:  " + ex.getMessage(), ex);
        }
    }

    @Override
    public final void activate() throws Exception {

        // Now start up the rest of the contents.
        this.tagMessageManager.start();

       //// initAllClientConnections();
       FakeEstablishClientConnection(true);
    }

    @Override
    public final void deactivate() {
        if (this.tagMessageManager != null) {
            this.tagMessageManager.stop();
        }

        // Deactivate each one!
        if (this.mqttMessageManagerList != null) {
            for (MqttMessageManager mqttMgr : mqttMessageManagerList) {
                logger.debug("Stopping an MqttMessageManager!");
                stopMqttMessageManager(mqttMgr);
            }
        }

        /////////mqttPublisher.shutdown();
    }

    @Override
    public final void destroy() {

        if (this.tagMessageManager != null) {
            this.tagMessageManager.removeEventListener((TagMessageStatusEventListener) this);
            //////this.tagMessageManager.removeEventListener((TagMessageDataEventListener) this);
        }

        this.tagMessageManager = null;

        // Remove listeners from each one!
        if (this.mqttMessageManagerList != null) {
            for (MqttMessageManager mqttMgr : mqttMessageManagerList) {
                logger.debug("Destroying an MqttMessageManager!");
                stopMqttMessageManager(mqttMgr);
            }
        }

        this.mqttMessageManagerList = new CopyOnWriteArrayList<>();

        /*
        // Kill the thread and shutdown the MqttPublisher entity.
        this.mqttPublisher.removeDataEventListener(this);
        this.mqttPublisher.removeMqttPublisherThreadListener(this);
        this.mqttPublisher.shutdown();
        */
    }

    private void stopMqttMessageManager(MqttMessageManager mqttMessageManager) {
        mqttMessageManager.removeEventListener(this);
        mqttMessageManager.stop();
    }

    @Override
    public final void connectionClosed(final TagMessageStatusEvent event) {
        logger.warn(String.format("Connection Closed; reconnect = %s, message = %s", event.isReconnect(),
                event.getMessage()));
        /* TO DO                
        // Flag differently based on status.  If a reconnection effort is
        // underway due to reset, then this should only show up as a warning.
        SystemAlertSeverity severityLevel = SystemAlertSeverity.WARNING;
        if (!event.isReconnect()) {
            // Something more serious happened, so flag it as critical.
            severityLevel = SystemAlertSeverity.CRITICAL;
        }
        publishEvent(new SystemAlertEvent(event.getId(), event.getMessage(), severityLevel)); */
    }

    @Override
    public final void connectionError(final TagMessageStatusEvent event) {
        // TO DO: This is where we need to log to CloudWatch
        logger.error(String.format("Connection Error (id=%s):  %s", event.getId(), event.getMessage()));
        ////publishEvent(new SystemAlertEvent(event.getId(), event.getMessage(), SystemAlertSeverity.CRITICAL));
    }

    @Override
    public final void connectionEstablished(final TagMessageStatusEvent event) {
        logger.warn(String.format("Connection Established (id=%s):  %s", event.getId(), event.getMessage()));
        /////publishEvent(new SystemAlertEvent(event.getId(), event.getMessage(), SystemAlertSeverity.RESOLVED));
    }

    @Override
    public void messageReceiveError(final TagMessageStatusEvent event) {
        logger.error(String.format("Message Receive Error (id=%s):  %s", event.getId(), event.getMessage()));
    }
  
    @Override
    public final void messageReceived(final TagMessageDataEvent event) {
         
        // First, decide whether to clear MQTT Downlink Queue.
        logger.debug("Message Received!");

        // Clear the downlink queue as necessary.
        /////if (shouldClearDownlinkQueue(event)) {
        ///////    clearDownlinkQueue(event);
        ///////}

        // Publish the event regardless.
        //////////publishEvent(new TagReportMessageEvent(event.getMessage(), event.getPacket()));
    }
     
    @Override
    public final synchronized void mqttPublisherThreadDied(final MqttPublisherThreadDiedEvent event) {

        /* TO DO: Not needed yet, migrated from Help Alert
        // Only do this once, since multiple gateway controllers will all send this update.
        if (!this.mqttPublisher.isThreadAlive()) {

            // Re-spawn it!
            this.mqttPublisher.restart();
        }
        // Process the orphaned message entries.
        // Two (2) possible approaches here:
        // 1.  Have the publisher thread 'adopt' them for re-processing.
        // 2.  Feed each message here into the messageReceived method.

        // Option #1:  Create a new 'adopt' method for the orphaned messages.
        // this.mqttPublisher.adoptOrphans(event.getMessageList());
        // Option #2:  Loop through the list and create new Data Event.
        if ((event != null) && (event.getMessageList() != null)) {
            for (IMessage nextMessage : event.getMessageList()) {
                messageReceived(new TagMessageDataEvent(nextMessage, null));
            }
        } */
    }

    /*
    @EventProcess
    public final void processCommandMessage(final TagCommandMessageEvent tagCommandEvent) throws Exception {
      
        if ((tagCommandEvent != null) && (tagCommandEvent.getIpAddress() != null)) {
            this.tagMessageManager.sendTagCommandMessage(tagCommandEvent.getMessage(), tagCommandEvent.getPort(),
                    tagCommandEvent.getIpAddress());
        }
        // Send it also to the MQTT message manager for any LoRa stuff.
        if ((tagCommandEvent != null) && (tagCommandEvent.getDeviceEui() != null)
                && !tagCommandEvent.getDeviceEui().isEmpty()) {
            List<IMqttBrokerSettings> mqttBrokerSettingsList = this.mqttBrokerSettingsEntityManager.findAll(null);
            if (mqttBrokerSettingsList != null) {
                for (IMqttBrokerSettings settings : mqttBrokerSettingsList) {
                    MqttMessageManager mqttMsgMgr = getMatchingMqttMessageManager(settings.getId());
                    if (mqttMsgMgr != null) {
                        mqttMsgMgr.send(tagCommandEvent.getMessage(), tagCommandEvent.getDeviceEui(),
                                Byte.toUnsignedInt(settings.getPubQos()));
                    }
                }
            }
        }       
    }*/

    /* TO DO --- LATER         
    @EventProcess
    @ThreadSafe
    public final void processMqttBrokerSettingsUpdateEvent(final MqttBrokerSettingsUpdateEvent event) {
        if (event != null && event.getMqttBrokerSettings() != null) {
            // Convert settings to mapped object for setting/resetting in the MQTT Message Manager.
            IMqttBrokerSettings mqttBrokerSettings = event.getMqttBrokerSettings();
            logger.debug("Establishing a Client Connection for Mqtt Broker: " + mqttBrokerSettings.getBrokerName());
            establishClientConnection(mqttBrokerSettings, event.isPostInsert());
        }
    }

    @EventProcess
    @ThreadSafe
    public final void processMqttBrokerSettingsDeleteEvent(final MqttBrokerSettingsDeleteEvent event) {
        if (event != null && event.getMqttBrokerSettings() != null) {
            // Convert settings to mapped object for setting/resetting in the MQTT Message Manager.
            IMqttBrokerSettings mqttBrokerSettings = event.getMqttBrokerSettings();
            logger.debug("Killing a Client Connection for Mqtt Broker: " + mqttBrokerSettings.getBrokerName());
            killClientConnection(mqttBrokerSettings);
        }
    }
    */
   
    private void publishEvent(final Object event) {
        ///////this.eventRuntimeContext.getEventBroker().publish(this, event);
    }

    private static String getTagKey(final String macAddress, final TagType tagType) {
        return macAddress + "." + tagType.value();
    }

    // ----------------
    // Tag Lock Related
    // ----------------
    private Object getTagLockObject(final String tagKey) {
        Object lockObject = this.tagLockObjectHash.get(tagKey);
        if (lockObject == null) {
            Object newLockObject = new Object();
            lockObject = this.tagLockObjectHash.putIfAbsent(tagKey, newLockObject);
            if (lockObject == null) {
                lockObject = newLockObject;
            }
        }

        return lockObject;
    }

    private void initAllClientConnections() {
             
        // First, grab the settings contents list from the database.
        List<IMqttBrokerSettings> mqttBrokerSettings = this.mqttBrokerSettingsEntityManager.findAll(null);

        // Establish the client connection if the user provided sufficient settings content.
        if (mqttBrokerSettings != null) {
            for (IMqttBrokerSettings settings : mqttBrokerSettings) {

                String hostName = settings.getHostName();
                if ((hostName != null) && !hostName.isEmpty()) {
                    logger.debug("Trying To Initialize MQTT Broker Connection...");
                    establishClientConnection(settings, true);
                }
            }
        } 
    }

    private void establishClientConnection(final IMqttBrokerSettings mqttBrokerSettings, final boolean firstLaunch) {

        Map<String, Object> loraSettingsMap = getParameterizedMap(mqttBrokerSettings);

        // Reset connection on the MQTT Message Manager.
        String hostName = mqttBrokerSettings.getHostName();
        int portNumber = mqttBrokerSettings.getPort();
        MqttClientProtocolType protocolType = MqttClientProtocolType.lookup(mqttBrokerSettings.getProtocolType());
        String serverURI = MqttBrokerUtils.formMqttBrokerUri(protocolType, hostName, portNumber,
                localeMessageProvider);

        // Create and launch if the entry is new; otherwise, simply update it.
        logger.debug(String.format("Trying To Establish MQTT Broker Connection With URI = %s", serverURI));
        MqttMessageManager mqttMsgMgr = getMatchingMqttMessageManager(mqttBrokerSettings.getId());
        if (mqttMsgMgr == null) {
            mqttMsgMgr = createNewMqttMessageManager();

            // Try to start it up!
            try {
                mqttMsgMgr.start();
            } catch (Exception ex) {
                mqttMsgMgr = null;
                logger.error(ex);
            }
        }

        // Now invoke the rest of the logic to establish the connection!
        if (mqttMsgMgr != null) {
            mqttMsgMgr.resetConnection(loraSettingsMap, serverURI, firstLaunch);
        }
    }

    private void FakeInitAllClientConnections() {                

        logger.debug("Trying To FakeOut Initialize MQTT Broker Connection...");
        FakeEstablishClientConnection(true);          
    }

    private void FakeEstablishClientConnection(final boolean firstLaunch) {
    
        // This is a fake method to allow us to test the connection management logic without needing to haul in the full MQTT Message Manager contents.
        Map<String, Object> loraSettingsMap = null;
        
        // Reset connection on the MQTT Message Manager.
        String hostName = "10.8.2.2"; //mqttBrokerSettings.getHostName();
        int portNumber = 1883;
        
        MqttClientProtocolType protocolType = MqttClientProtocolType.LOCAL; //MqttClientProtocolType.lookup(mqttBrokerSettings.getProtocolType());
        //String serverURI = MqttBrokerUtils.formMqttBrokerUri(protocolType, hostName, portNumber,
        //        localeMessageProvider);
        String serverURI = "tcp://10.8.2.2:1883"; 
;
        // Create and launch if the entry is new; otherwise, simply update it.
        logger.debug(String.format("Trying To Establish MQTT Broker Connection With URI = %s", serverURI));
        MqttMessageManager mqttMsgMgr = null; ///////getMatchingMqttMessageManager(mqttBrokerSettings.getId());
        if (mqttMsgMgr == null) {
            mqttMsgMgr = createNewMqttMessageManager();

            // Try to start it up!
            try {
                mqttMsgMgr.start();
            } catch (Exception ex) {
                mqttMsgMgr = null;
                logger.error(ex);
            }
        }
        // Now invoke the rest of the logic to establish the connection!

        //VIP: It seems like the "serverURI" is really the most critical parameter here.
        if (mqttMsgMgr != null) {
            mqttMsgMgr.resetConnection(loraSettingsMap, serverURI, firstLaunch);
        }
    }

    private void killClientConnection(final IMqttBrokerSettings mqttBrokerSettings) {
        MqttMessageManager mqttMsgMgr = getMatchingMqttMessageManager(mqttBrokerSettings.getId());
        if (mqttMsgMgr != null) {
            logger.debug("Killing Connection to Client Gateway!");
            mqttMsgMgr.stop();
            int size = 0;
            if (mqttMessageManagerList != null) {
                size = mqttMessageManagerList.size();

                int index = mqttMessageManagerList.indexOf(mqttMsgMgr);
                logger.debug(
                        "Removing MQTT Message Manager from list (with " + size + " elements) at Index = " + index);
                mqttMessageManagerList.remove(index);
            }
        }
    }

    private Map<String, Object> getParameterizedMap(final IMqttBrokerSettings incomingMqttBrokerSettings) {
        Map<String, Object> loraSettingsMap = new HashMap<>();
        loraSettingsMap.put("overrideClientId", incomingMqttBrokerSettings.isOverrideClientId());
        if (incomingMqttBrokerSettings.isOverrideClientId()) {
            loraSettingsMap.put("clientId", incomingMqttBrokerSettings.getClientId());
        }

        loraSettingsMap.put("keepAliveInterval", incomingMqttBrokerSettings.getKeepAliveInterval());
        loraSettingsMap.put("cleanSession", incomingMqttBrokerSettings.isCleanSession());
        loraSettingsMap.put("brokerId", incomingMqttBrokerSettings.getId());
        loraSettingsMap.put("brokerName", incomingMqttBrokerSettings.getBrokerName());
        loraSettingsMap.put("brokerType", incomingMqttBrokerSettings.getBrokerType());
        loraSettingsMap.put("authRequired", incomingMqttBrokerSettings.isAuthRequired());
        if (incomingMqttBrokerSettings.isAuthRequired()) {
            loraSettingsMap.put("username", incomingMqttBrokerSettings.getUsername());
            loraSettingsMap.put("password", incomingMqttBrokerSettings.getPassword());
        }

        loraSettingsMap.put("pubQos", Byte.toUnsignedInt(incomingMqttBrokerSettings.getPubQos()));
        loraSettingsMap.put("subQos", Byte.toUnsignedInt(incomingMqttBrokerSettings.getSubQos()));
        return loraSettingsMap;
    }

    private MqttMessageManager createNewMqttMessageManager() {
        // Acquire the current data status of devices needed to instantiate the message manager.
        Map<String, Object> deviceDataMap = getCurrentSystemDeviceData();
        Map<String, Short> eventIdMap = (Map<String, Short>) deviceDataMap.get("eventIdMap");
        Map<String, Boolean> tagAlarmStateMap = (Map<String, Boolean>) deviceDataMap.get("tagAlarmStateMap");
        Map<String, Date> timeSinceBatteryInstalledMap = (Map<String, Date>) deviceDataMap.get(
                "timeSinceBatteryInstalledMap");

        // Now create it and add it to the main listings.
        //BK 2-2026, Removed 1st parameter (mqttPublisher) from constructor, as we are no longer using the MqttPublisher class in this module.
        MqttMessageManager mqttMsgMgr = new MqttMessageManager(eventIdMap, tagAlarmStateMap,
                timeSinceBatteryInstalledMap);

        mqttMsgMgr.addEventListener((TagMessageStatusEventListener) this);
        mqttMessageManagerList.add(mqttMsgMgr);

        return mqttMsgMgr;
    }

    private MqttMessageManager getMatchingMqttMessageManager(final String mqttBrokerId) {
        MqttMessageManager mqttMsgMgr = null;
        if (mqttMessageManagerList != null) {
            for (MqttMessageManager mgr : mqttMessageManagerList) {
                if (mqttBrokerId.equals(mgr.getBrokerId())) {
                    mqttMsgMgr = mgr;
                    break;
                }
            }
        }
        return mqttMsgMgr;
    }

    private Map<String, Object> getCurrentSystemDeviceData() {

        // Acquire the data contents.
        Map<String, Short> eventIdMap = new HashMap<>();
        Map<String, Boolean> tagAlarmStateMap = new HashMap<>();
        Map<String, Date> timeSinceBatteryInstalledMap = new HashMap<>();

        ///* TO DO: LATER ...
        // Acquire all Lora Fob and BLE Locator Beacon base information for the MQTT Message Manager.
        List<Filter> tagTypeFilters = new ArrayList<>();
        List<Byte> tagList = new ArrayList<>();
        tagList.add(TagType.LORA_FOB.value());
        tagList.add(TagType.LORA_BADGE.value());
        tagList.add(TagType.MINI_BADGE.value());
        tagList.add(TagType.BLE_LOCATOR_BEACON.value());
        tagList.add(TagType.BLE_BEACON_MONITOR.value());
        tagTypeFilters.add(new ObjectFilter("type", tagList.toArray(), FilterType.IN));
        //List<ITag> tags = this.tagEntityManager.findByFilters(tagTypeFilters, tagLoadRelatedEndsInit);
        //*/

        int tagListSize = 0;
        List<ITag> tags = new ArrayList<>();
        if (tags != null) {
            tagListSize = tags.size();
        }
        
        /* TO DO LATER ...
        logger.debug(String.format("Total # of Tags:  %d", tagListSize));
        if ((tags != null) && !tags.isEmpty()) {
            for (ITag tag : tags) {
                // Check key check-in parameters as well as alert status.
                String tagMacAddress = tag.getMacAddress();
                String tagKey = getTagKey(tagMacAddress, TagType.lookup(tag.getType()));
                synchronized (getTagLockObject(tagKey)) {
                    // Determine alarm status.
                    boolean inAlarm = (tag.getLastTagAlert() != null) && ((tag.getLastTagAlert()
                            .getStatus() == AlertStatusType.ACTIVE.value()) || (tag.getLastTagAlert()
                            .getStatus() == AlertStatusType.ACKNOWLEDGED.value()) || (tag.getLastTagAlert()
                            .getStatus() == AlertStatusType.CLEARED.value() && tag.getLastTagAlert()
                            .getTagClearDate() == null));

                    logger.debug(String.format("Tag %s In Alarm:  %s", tagMacAddress, inAlarm));
                    tagAlarmStateMap.put(tagMacAddress, inAlarm);

                    // Now check basic check-in parameters.
                    String lastTagCheckinDesc = "IS NULL";
                    if (tag.getLastTagCheckin() != null) {
                        lastTagCheckinDesc = "IS NOT NULL";
                    }

                    logger.debug(String.format("Last Check-In Event For Tag %s %s", tagMacAddress, lastTagCheckinDesc));
                    if (tag.getLastTagCheckin() != null) {
                        logger.debug(String.format("Last Event Id For Tag %s Is:  %d", tagMacAddress,
                                tag.getLastTagCheckin().getEventId()));
                        eventIdMap.put(tagMacAddress, tag.getLastTagCheckin().getEventId());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(tag.getLastTagCheckin().getLastCheckinDate());
                        calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) - tag.getLastTagCheckin()
                                .getTimeSinceBatteryInstalled()));
                        logger.debug(String.format("Recomputing Time Since Battery Installed For Tag %s As:  %s",
                                tagMacAddress, calendar.getTime()));
                        timeSinceBatteryInstalledMap.put(tagMacAddress, calendar.getTime());
                    }
                }
            }
        }
        */

        // Put them into one gigantic map for later retrieval.
        Map<String, Object> finalMap = new HashMap<>();
        finalMap.put("eventIdMap", eventIdMap);
        finalMap.put("tagAlarmStateMap", tagAlarmStateMap);
        finalMap.put("timeSinceBatteryInstalledMap", timeSinceBatteryInstalledMap);
        return finalMap;
    }

    /* BK 2-2026 Forklifting from HelpAlert module of the same name ... don't need these yet.
    /*    
    // Determine whether to clear the Downlink MQTT Queue or not.
    //
    // @param event The event passed in from the MQTT layer.
    // @return boolean True if the downlink Queue should be cleared, false
    // otherwise.
    //
    private boolean shouldClearDownlinkQueue(final TagMessageDataEvent event) {        
    }
    private void clearDownlinkQueue(final TagMessageDataEvent event) {      
    }

    ///
    // * Determine the Device EUI from the raw message contents.
    // *
    // * @param event The event passed in from the MQTT layer.
    // * @return String The device EUI contents.
     
    private String getDeviceEuiFromMessageEvent(final TagMessageDataEvent event) {
        String deviceEui = null;
        if (event != null) {
            IMessage coreMsg = event.getMessage();
            if (coreMsg instanceof CheckinReportMessage) {
                CheckinReportMessage checkinReportMsg = (CheckinReportMessage) coreMsg;
                deviceEui = checkinReportMsg.getTagName();
            }
        }
        return deviceEui;
    }
    */
}
