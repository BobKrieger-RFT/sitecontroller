package com.sitecontroller.sitecontroller.updateorchestrator;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface iHandleChirpUpFromCloud
{
    //Handles message flow when receiving a notification update (via Cloud-based MQTT Client) for a particular device.
    //Assumption: The only real data of interest in this notification is the Device Eui (ID).
    void ProcessChirpstackUpdateFromCloud(final MqttMessage msg);    
}