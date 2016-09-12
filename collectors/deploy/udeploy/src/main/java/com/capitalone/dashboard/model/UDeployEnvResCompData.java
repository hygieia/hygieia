package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

public class UDeployEnvResCompData {
	private ObjectId collectorItemId;
	private String environmentName;
	private String environmentUrl;
	private String componentID;
	private String componentName;
	private String componentVersion;
	private boolean deployed;
	private long asOfDate;
    private String resourceName;
    private String parentAgentName;
    private boolean online;
    
	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}
	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	public String getEnvironmentUrl() {
		return environmentUrl;
	}
	public void setEnvironmentUrl(String environmentUrl) {
		this.environmentUrl = environmentUrl;
	}
	public String getComponentID() {
		return componentID;
	}
	public void setComponentID(String componentID) {
		this.componentID = componentID;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getComponentVersion() {
		return componentVersion;
	}
	public void setComponentVersion(String componentVersion) {
		this.componentVersion = componentVersion;
	}
	public boolean isDeployed() {
		return deployed;
	}
	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}
	public long getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(long asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getParentAgentName() {
		return parentAgentName;
	}
	public void setParentAgentName(String parentAgentName) {
		this.parentAgentName = parentAgentName;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
}
