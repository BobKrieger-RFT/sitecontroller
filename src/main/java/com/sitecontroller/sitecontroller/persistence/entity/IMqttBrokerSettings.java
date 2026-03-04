package com.sitecontroller.sitecontroller.persistence.entity;

public interface IMqttBrokerSettings extends ISoftDeleteEntity {
    String getBrokerName();

    void setBrokerName(String brokerName);

    String getHostName();

    void setHostName(String hostName);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    byte getProtocolType();

    void setProtocolType(byte protocolType);

    int getPort();

    void setPort(int port);

    String getBrokerType();

    void setBrokerType(String brokerType);

    boolean isOverrideClientId();

    void setOverrideClientId(boolean overrideClientId);

    String getClientId();

    void setClientId(String clientId);

    int getKeepAliveInterval();

    void setKeepAliveInterval(int keepAliveInterval);

    boolean isCleanSession();

    void setCleanSession(boolean cleanSession);

    boolean isAuthRequired();

    void setAuthRequired(boolean authRequired);

    byte getPubQos();

    void setPubQos(byte pubQos);

    byte getSubQos();

    void setSubQos(byte subQos);

    ISite getSite();

    void setSite(ISite site);

    boolean isSiteLoaded();
}
