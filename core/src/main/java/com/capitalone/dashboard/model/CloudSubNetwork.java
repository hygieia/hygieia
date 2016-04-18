package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Document(collection = "cloud_subnetwork")
public class CloudSubNetwork extends BaseModel{
    @Indexed
    private String subnetId;
    private ObjectId collectorItemId;
    private String virtualNetworkId;
    private String cidrBlock;
    private String zone;
    private int availableIPCount;
    private int usedIPCount;
    private boolean defaultForZone;
    private String state;
    private long creationDate;
    private Date lastUpdateDate;

    private Map<String, String> tags = new HashMap<>();

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Map<String, String> getTags() {
        return tags;
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
