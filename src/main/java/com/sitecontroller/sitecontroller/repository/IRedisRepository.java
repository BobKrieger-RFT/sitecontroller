package com.sitecontroller.sitecontroller.repository;

public interface IRedisRepository {
    
    //Get Gateway value by key obtained from a notification (through Cloud Mqtt Broker)
    String getGatewaySyncValue(String key);

    String setPendingGatewaySyncValue(String key);
}
