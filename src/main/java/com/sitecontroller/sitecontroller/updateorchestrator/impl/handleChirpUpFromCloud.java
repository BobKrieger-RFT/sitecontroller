package com.sitecontroller.sitecontroller.updateorchestrator.impl;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.sitecontroller.sitecontroller.repository.IRedisRepository;

import com.sitecontroller.sitecontroller.model.FakeDevice;

@Component
public class handleChirpUpFromCloud
{
    @Autowired
    private IRedisRepository redisRepository;

    //Handles message flow when receiving a notification update (via Cloud-based MQTT Client) for a particular device.
    //Assumption: The only real data of interest in this notification is the Device Eui (ID).
    //
    void ProcessChirpstackUpdateFromCloud(final MqttMessage msg){

        //--------------------------------------------------------------------------------------------------------------------------
        //TO DO: 
        //Note that in module "mqtt\MqttMessageManager.java", we have the method "handleChirpstackMessage".
        //There, we are receiving and handling ChirpUp MQTT message from a local Mqtt broker.
        //How could we leverage that here?

        //Once we are assured that method can handle Chirp up messages, can we get rid of this entire orchestrator class 
        // and simply call the necessary methods directly from "handleChirpstackMessage" method in "MqttMessageManager.java" class?

        //--------------------------------------------------------------------------------------------------------------------------
        //String devEui = deviceId;

        //[1]
        //Retrieve the current sync value from Redis and set a pending sync value for this device update...         
        String devEui = "TO DO: Get this from the notification message";
        SyncToRedis(devEui);

        //[2] 
        // Do an HTTP GET call to Device Retrieval Service (AssetService HTTP controller).
        // This will get the updated information for the device.
        HttpGetDeviceRetrievalServiceInfo(devEui);
    }
       
    //[2] Call to Redis class to make synchronization request    
    private void SyncToRedis(String deviceEui){

        //Retrieve the current (if any) sync value from Redis ...
         redisRepository.getGatewaySyncValue(deviceEui);

         //.. and set a pending sync value for this device update.
         redisRepository.setPendingGatewaySyncValue(deviceEui);
    }

    //[3] Call Device Retrieval Service to get the updated information for the device...
    //
    //HTTP GET Gateways and Devices with current sync value and latest notification sync value.
    //
    //The return value is expected to be a list of Gateways with that dev Eui and their associated device names.

    private void HttpGetDeviceRetrievalServiceInfo(String devEui){  
        
        //TO DO: What endpoint are we calling here?

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();

        FakeDevice[] fakeDeviceList = restClient.get()
                .uri("/device-retrieval-service/devices/{devEui}", devEui)
                .retrieve()
                .body(FakeDevice[].class);
    
     }
}