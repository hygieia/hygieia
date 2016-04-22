package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "cloud_subnetwork")
public class CloudSubNetwork extends BaseModel{
    @Indexed
    @NotNull
    @NotBlank
    private String subnetId;
    private ObjectId collectorItemId;
    @NotNull
    @NotBlank
    private String accountNumber;
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

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<NameValue> getTags() {
        return tags;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getCidrCount() {
        return cidrCount;
    }

    public void setCidrCount(int cidrCount) {
        this.cidrCount = cidrCount;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
