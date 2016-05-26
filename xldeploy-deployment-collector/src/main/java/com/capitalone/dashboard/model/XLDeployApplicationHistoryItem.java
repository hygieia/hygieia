package com.capitalone.dashboard.model;

public class XLDeployApplicationHistoryItem {
	private String deploymentPackage;
	private String environmentId;
	private String environmentName;
	private long completionDate;
	// environmentIdWithoutRoot don't need
	private String type;
	private String user;
	private String taskId;
	private long startDate;
	private String status;
	
	/**
	 * @return the deploymentPackage
	 */
	public String getDeploymentPackage() {
		return deploymentPackage;
	}
	/**
	 * @param deploymentPackage the deploymentPackage to set
	 */
	public void setDeploymentPackage(String deploymentPackage) {
		this.deploymentPackage = deploymentPackage;
	}
	/**
	 * @return the environmentId
	 */
	public String getEnvironmentId() {
		return environmentId;
	}
	/**
	 * @param environmentId the environmentId to set
	 */
	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}
	/**
	 * @return the environmentName
	 */
	public String getEnvironmentName() {
		return environmentName;
	}
	/**
	 * @param environmentName the environmentName to set
	 */
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	/**
	 * @return the completionDate
	 */
	public long getCompletionDate() {
		return completionDate;
	}
	/**
	 * @param completionDate the completionDate to set
	 */
	public void setCompletionDate(long completionDate) {
		this.completionDate = completionDate;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}
	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	/**
	 * @return the startDate
	 */
	public long getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
