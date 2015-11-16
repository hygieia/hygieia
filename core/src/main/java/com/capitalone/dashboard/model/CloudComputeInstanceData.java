package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an EC2 instance from AWS
 * 
 * Possible collectors: AWS, Microsoft Azure
 * 
 * 
 */
public class CloudComputeInstanceData {
	private String instanceId;
	private String instanceType;
	private String imageId;
	private boolean isMonitored;
	private String privateDns;
	private String privateIp;
	private String publicDns;
	private String publicIp;
	private String subnetId;
	private String virtualPrivateCloudId;
	private int age;
	private boolean isEncrypted;
	private boolean isStopped;
	private boolean isTagged;
	private double cpuUtilization;
	private Date timestamp;
	private List<String> securityGroupNames = new ArrayList<>();
	private double networkIn;
	private double networkOut;
	private double diskRead;
	private double diskWrite;
	private String rootDeviceName;

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

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public boolean isTagged() {
		return isTagged;
	}

	public void setTagged(boolean isTagged) {
		this.isTagged = isTagged;
	}

	public double getCpuUtilization() {
		return cpuUtilization;
	}

	public void setCpuUtilization(double cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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

	public void setMonitored(boolean isMonitored) {
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

	public String getVirtualPrivateCloudId() {
		return virtualPrivateCloudId;
	}

	public void setVirtualPrivateCloudId(String virtualPrivateCloudId) {
		this.virtualPrivateCloudId = virtualPrivateCloudId;
	}

	public List<String> getSecurityGroups() {
		return securityGroupNames;
	}

	public void addSecurityGroups(String securityGroupName) {
		this.securityGroupNames.add(securityGroupName);
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

}
