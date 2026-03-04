package com.sitecontroller.sitecontroller.model;

public class FakeDevice {
    private String name;
    private String devEui;
    private String joinEui;
    private String applicationwide;
    private String deviceProfileId;
    private String description;   
    private boolean isDisabled;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevEui() {
        return devEui;
    }

    public void setDevEui(String devEui) {
        this.devEui = devEui;
    }

    public String getJoinEui() {
        return joinEui;
    }

    public void setJoinEui(String joinEui) {
        this.joinEui = joinEui;
    }

    public String getApplicationwide() {
        return applicationwide;
    }

    public void setApplicationwide(String applicationwide) {
        this.applicationwide = applicationwide;
    }

    public String getDeviceProfileId() {
        return deviceProfileId;
    }

    public void setDeviceProfileId(String deviceProfileId) {
        this.deviceProfileId = deviceProfileId;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}
