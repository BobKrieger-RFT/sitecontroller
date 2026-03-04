package com.sitecontroller.sitecontroller.tag.message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ChirpUpMessage implements Serializable {
    private static final long serialVersionUID = 243352446375L;

    /**
     * Default constructor - does nothing.
     */
    public ChirpUpMessage() {
    }

    public String deduplicationId;
    public String time;
    public DeviceInfo deviceInfo;
    public String devAddr;
    public boolean adr;
    public int dr;
    public int fCnt;
    public int fPort;
    public boolean confirmed;
    public String data;
    public ArrayList<RxInfo> rxInfo;
    public TxInfo txInfo;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDeduplicationId() {
        return deduplicationId;
    }

    public void setDeduplicationId(String deduplicationId) {
        this.deduplicationId = deduplicationId;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(String devAddr) {
        this.devAddr = devAddr;
    }

    public boolean isAdr() {
        return adr;
    }

    public void setAdr(boolean adr) {
        this.adr = adr;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public int getfCnt() {
        return fCnt;
    }

    public void setfCnt(int fCnt) {
        this.fCnt = fCnt;
    }

    public int getfPort() {
        return fPort;
    }

    public void setfPort(int fPort) {
        this.fPort = fPort;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<RxInfo> getRxInfo() {
        return rxInfo;
    }

    public void setRxInfo(ArrayList<RxInfo> rxInfo) {
        this.rxInfo = rxInfo;
    }

    public TxInfo getTxInfo() {
        return txInfo;
    }

    public void setTxInfo(TxInfo txInfo) {
        this.txInfo = txInfo;
    }

    public static class DeviceInfo {
        public String tenantId;
        public String tenantName;
        public String applicationId;
        public String applicationName;
        public String deviceProfileId;
        public String deviceProfileName;
        public String deviceName;
        public String devEui;
        public String deviceClassEnabled;
        public Tags tags;

        public DeviceInfo() {

        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getDeviceProfileId() {
            return deviceProfileId;
        }

        public void setDeviceProfileId(String deviceProfileId) {
            this.deviceProfileId = deviceProfileId;
        }

        public String getDeviceProfileName() {
            return deviceProfileName;
        }

        public void setDeviceProfileName(String deviceProfileName) {
            this.deviceProfileName = deviceProfileName;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDevEui() {
            return devEui;
        }

        public void setDevEui(String devEui) {
            this.devEui = devEui;
        }

        public String getDeviceClassEnabled() {
            return deviceClassEnabled;
        }

        public void setDeviceClassEnabled(String deviceClassEnabled) {
            this.deviceClassEnabled = deviceClassEnabled;
        }

        public Tags getTags() {
            return tags;
        }

        public void setTags(Tags tags) {
            this.tags = tags;
        }
    }

    public static class Location {
        public Location() {

        }
    }

    public static class Lora {
        public int bandwidth;
        public int spreadingFactor;
        public String codeRate;

        public Lora() {

        }

        public int getSpreadingFactor() {
            return spreadingFactor;
        }

        public void setSpreadingFactor(int spreadingFactor) {
            this.spreadingFactor = spreadingFactor;
        }

        public String getCodeRate() {
            return codeRate;
        }

        public void setCodeRate(String codeRate) {
            this.codeRate = codeRate;
        }

        public int getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(int bandwidth) {
            this.bandwidth = bandwidth;
        }
    }

    public static class Metadata {
        public String region_config_id;
        public String region_common_name;

        public Metadata() {
        }

        public String getRegion_config_id() {
            return region_config_id;
        }

        public void setRegion_config_id(String region_config_id) {
            this.region_config_id = region_config_id;
        }

        public String getRegion_common_name() {
            return region_common_name;
        }

        public void setRegion_common_name(String region_common_name) {
            this.region_common_name = region_common_name;
        }
    }

    public static class Modulation {
        public Lora lora;

        public Modulation() {

        }

        public Lora getLora() {
            return lora;
        }

        public void setLora(Lora lora) {
            this.lora = lora;
        }
    }

    public static class RxInfo {
        public String gatewayId;
        public long uplinkId;
        public String gwTime;
        public String nsTime;
        public int rssi;
        public double snr;
        public int channel;
        public int rfChain;
        public Location location;
        public String context;
        public Metadata metadata;
        public String crcStatus;

        public RxInfo() {
        }

        public String getGatewayId() {
            return gatewayId;
        }

        public void setGatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
        }

        public long getUplinkId() {
            return uplinkId;
        }

        public void setUplinkId(long uplinkId) {
            this.uplinkId = uplinkId;
        }

        public String getGwTime() {
            return gwTime;
        }

        public void setGwTime(String gwTime) {
            this.gwTime = gwTime;
        }

        public String getNsTime() {
            return nsTime;
        }

        public void setNsTime(String nsTime) {
            this.nsTime = nsTime;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public double getSnr() {
            return snr;
        }

        public void setSnr(double snr) {
            this.snr = snr;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public int getRfChain() {
            return rfChain;
        }

        public void setRfChain(int rfChain) {
            this.rfChain = rfChain;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        public String getCrcStatus() {
            return crcStatus;
        }

        public void setCrcStatus(String crcStatus) {
            this.crcStatus = crcStatus;
        }
    }

    public static class Tags {
        public Tags() {

        }
    }

    public static class TxInfo {
        public int frequency;
        public Modulation modulation;

        public TxInfo() {

        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public Modulation getModulation() {
            return modulation;
        }

        public void setModulation(Modulation modulation) {
            this.modulation = modulation;
        }
    }
}
