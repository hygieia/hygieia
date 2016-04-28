package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "cloud_subnetwork")
public class CloudSubNetwork extends BaseModel{
    @Indexed
    private String subnetId;
    private String virtualNetworkId;
    private String cidrBlock;
    private int cidrCount;
    private String zone;
    private int availableIPCount;
    private int usedIPCount;
    private boolean defaultForZone;
    private String state;
    private long creationDate;
    private long lastUpdateDate;
    private List<NameValue> tags = new ArrayList<>();
    private Map<String, Integer> ipUsage;


    public Map<String, Integer> getIpUsage() {
        return ipUsage;
    }

    public void setIpUsage(Map<String, Integer> ipUsage) {
        this.ipUsage = ipUsage;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public String getVirtualNetworkId() {
        return virtualNetworkId;
    }

    public void setVirtualNetworkId(String virtualNetworkId) {
        this.virtualNetworkId = virtualNetworkId;
    }

    public String getCidrBlock() {
        return cidrBlock;
    }

    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getAvailableIPCount() {
        return availableIPCount;
    }

    public void setAvailableIPCount(int availableIPCount) {
        this.availableIPCount = availableIPCount;
    }

    public int getUsedIPCount() {
        return usedIPCount;
    }

    public void setUsedIPCount(int usedIPCount) {
        this.usedIPCount = usedIPCount;
    }

    public boolean isDefaultForZone() {
        return defaultForZone;
    }

    public void setDefaultForZone(boolean defaultForZone) {
        this.defaultForZone = defaultForZone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<NameValue> getTags() {
        return tags;
    }

    public int getCidrCount() {
        return cidrCount;
    }

    public void setCidrCount(int cidrCount) {
        this.cidrCount = cidrCount;
    }


    @Override
    public int hashCode() {
        return Objects.hash(subnetId);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(!(obj instanceof CloudSubNetwork)) return false;
        CloudSubNetwork c =(CloudSubNetwork) obj;
        return Objects.equals(getSubnetId(), c.getSubnetId());
    }
}
