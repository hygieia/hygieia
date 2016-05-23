package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an EC2 instance from AWS
 */
@Document(collection = "cloud_instance")
public class CloudInstance extends BaseModel{
    @Indexed
    private String instanceId;

    @Indexed
    private String accountNumber;
    private String instanceType;
    private String imageId;
    private long imageExpirationDate;
    private boolean imageApproved;
    private String instanceOwner;
    private boolean isMonitored;
    private String privateDns;
    private String privateIp;
    private String publicDns;
    private String publicIp;
    private String subnetId;
    private String virtualNetworkId;
    private int age;
    private String status;
    private boolean isStopped;
    private boolean isTagged;
    private double cpuUtilization;
    private long lastUpdatedDate;
    private List<String> securityGroups = new ArrayList<>();
    private List<NameValue> tags = new ArrayList<>();
    private double networkIn;
    private double networkOut;
    private double diskRead;
    private double diskWrite;
    private String rootDeviceName;
    private String autoScaleName;
    private String lastAction;


    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setIsStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public boolean isTagged() {
        return isTagged;
    }

    public void setIsTagged(boolean isTagged) {
        this.isTagged = isTagged;
    }

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
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

    public boolean isMonitored() {
        return isMonitored;
    }

    public void setIsMonitored(boolean isMonitored) {
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

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void addSecurityGroups(String securityGroupName) {
        this.securityGroups.add(securityGroupName);
    }

    public String getRootDeviceName() {
        return rootDeviceName;
    }

    public void setRootDeviceName(String rootDeviceName) {
        this.rootDeviceName = rootDeviceName;
    }

    public double getNetworkIn() {
        return networkIn;
    }

    public void setNetworkIn(double networkIn) {
        this.networkIn = networkIn;
    }

    public double getNetworkOut() {
        return networkOut;
    }

    public void setNetworkOut(double networkOut) {
        this.networkOut = networkOut;
    }

    public double getDiskRead() {
        return diskRead;
    }

    public void setDiskRead(double diskRead) {
        this.diskRead = diskRead;
    }

    public double getDiskWrite() {
        return diskWrite;
    }

    public void setDiskWrite(double diskWrite) {
        this.diskWrite = diskWrite;
    }

    public long getImageExpirationDate() {
        return imageExpirationDate;
    }

    public void setImageExpirationDate(long imageExpirationDate) {
        this.imageExpirationDate = imageExpirationDate;
    }

    public boolean isImageApproved() {
        return imageApproved;
    }

    public void setImageApproved(boolean imageApproved) {
        this.imageApproved = imageApproved;
    }

    public String getInstanceOwner() {
        return instanceOwner;
    }

    public void setInstanceOwner(String instanceOwner) {
        this.instanceOwner = instanceOwner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NameValue> getTags() {
        return tags;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAutoScaleName() {
        return autoScaleName;
    }

    public void setAutoScaleName(String autoScaleName) {
        this.autoScaleName = autoScaleName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(!(obj instanceof CloudInstance)) return false;
        CloudInstance c =(CloudInstance) obj;
        return Objects.equals(getInstanceId(), c.getInstanceId());
    }
}
