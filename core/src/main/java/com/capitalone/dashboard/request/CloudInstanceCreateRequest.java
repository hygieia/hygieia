package com.capitalone.dashboard.request;


import com.capitalone.dashboard.model.NameValue;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CloudInstanceCreateRequest {
    @NotNull
    private String instanceId;
    @NotNull
    private String accountNumber;
    private String instanceType;
    private String imageId;
    private String imageExpirationDate;
    private String imageApproved;
    private String instanceOwner;
    private String isMonitored;
    private String privateDns;
    private String privateIp;
    private String publicDns;
    private String publicIp;
    private String subnetId;
    private String virtualNetworkId;
    private String age;
    private String isEncrypted;
    private String status;
    private String isStopped;
    private String isTagged;
    private String cpuUtilization;
    private String lastUpdatedDate;
    private List<String> securityGroups = new ArrayList<>();
    private List<NameValue> tags = new ArrayList<>();
    private String networkIn;
    private String networkOut;
    private String diskRead;
    private String diskWrite;
    private String rootDeviceName;
    private String lastAction;
    private String autoScaleName;


    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageExpirationDate() {
        return imageExpirationDate;
    }

    public void setImageExpirationDate(String imageExpirationDate) {
        this.imageExpirationDate = imageExpirationDate;
    }

    public String getImageApproved() {
        return imageApproved;
    }

    public void setImageApproved(String imageApproved) {
        this.imageApproved = imageApproved;
    }

    public String getInstanceOwner() {
        return instanceOwner;
    }

    public void setInstanceOwner(String instanceOwner) {
        this.instanceOwner = instanceOwner;
    }

    public String getIsMonitored() {
        return isMonitored;
    }

    public void setIsMonitored(String isMonitored) {
        this.isMonitored = isMonitored;
    }

    public String getPrivateDns() {
        return privateDns;
    }

    public void setPrivateDns(String privateDns) {
        this.privateDns = privateDns;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPublicDns() {
        return publicDns;
    }

    public void setPublicDns(String publicDns) {
        this.publicDns = publicDns;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(String isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsStopped() {
        return isStopped;
    }

    public void setIsStopped(String isStopped) {
        this.isStopped = isStopped;
    }

    public String getIsTagged() {
        return isTagged;
    }

    public void setIsTagged(String isTagged) {
        this.isTagged = isTagged;
    }

    public String getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(String cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public List<NameValue> getTags() {
        return tags;
    }

    public void setTags(List<NameValue> tags) {
        this.tags = tags;
    }

    public String getNetworkIn() {
        return networkIn;
    }

    public void setNetworkIn(String networkIn) {
        this.networkIn = networkIn;
    }

    public String getNetworkOut() {
        return networkOut;
    }

    public void setNetworkOut(String networkOut) {
        this.networkOut = networkOut;
    }

    public String getDiskRead() {
        return diskRead;
    }

    public void setDiskRead(String diskRead) {
        this.diskRead = diskRead;
    }

    public String getDiskWrite() {
        return diskWrite;
    }

    public void setDiskWrite(String diskWrite) {
        this.diskWrite = diskWrite;
    }

    public String getRootDeviceName() {
        return rootDeviceName;
    }

    public void setRootDeviceName(String rootDeviceName) {
        this.rootDeviceName = rootDeviceName;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getAutoScaleName() {
        return autoScaleName;
    }

    public void setAutoScaleName(String autoScaleName) {
        this.autoScaleName = autoScaleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CloudInstanceCreateRequest that = (CloudInstanceCreateRequest) o;

        if (!instanceId.equals(that.instanceId)) return false;
        return accountNumber.equals(that.accountNumber);

    }

    @Override
    public int hashCode() {
        int result = instanceId.hashCode();
        result = 31 * result + accountNumber.hashCode();
        return result;
    }
}
