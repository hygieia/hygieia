package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.NameValue;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CloudSubnetCreateRequest {
    @NotNull
    private String subnetId;
    private String accountNumber;
    private String virtualNetworkId;
    private String cidrBlock;
    private String cidrCount;
    private String zone;
    private String availableIPCount;
    private String subscribedIPCount;
    private String usedIPCount;
    private String defaultForZone;
    private String state;
    private String creationDate;
    private String lastUpdateDate;
    private List<NameValue> tags = new ArrayList<>();
    private Map<String, Integer> ipUsage;
    private Map<String, Integer> subscribedIPUsage;


    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    public String getCidrCount() {
        return cidrCount;
    }

    public void setCidrCount(String cidrCount) {
        this.cidrCount = cidrCount;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getAvailableIPCount() {
        return availableIPCount;
    }

    public void setAvailableIPCount(String availableIPCount) {
        this.availableIPCount = availableIPCount;
    }

    public String getUsedIPCount() {
        return usedIPCount;
    }

    public void setUsedIPCount(String usedIPCount) {
        this.usedIPCount = usedIPCount;
    }

    public String getDefaultForZone() {
        return defaultForZone;
    }

    public void setDefaultForZone(String defaultForZone) {
        this.defaultForZone = defaultForZone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<NameValue> getTags() {
        return tags;
    }

    public void setTags(List<NameValue> tags) {
        this.tags = tags;
    }

    public Map<String, Integer> getIpUsage() {
        return ipUsage;
    }

    public void setIpUsage(Map<String, Integer> ipUsage) {
        this.ipUsage = ipUsage;
    }

    public String getSubscribedIPCount() {
        return subscribedIPCount;
    }

    public void setSubscribedIPCount(String subscribedIPCount) {
        this.subscribedIPCount = subscribedIPCount;
    }

    public Map<String, Integer> getSubscribedIPUsage() {
        return subscribedIPUsage;
    }

    public void setSubscribedIPUsage(Map<String, Integer> subscribedIPUsage) {
        this.subscribedIPUsage = subscribedIPUsage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CloudSubnetCreateRequest that = (CloudSubnetCreateRequest) o;

        if (!subnetId.equals(that.subnetId)) return false;
        return accountNumber.equals(that.accountNumber);

    }

    @Override
    public int hashCode() {
        int result = subnetId.hashCode();
        result = 31 * result + accountNumber.hashCode();
        return result;
    }
}
