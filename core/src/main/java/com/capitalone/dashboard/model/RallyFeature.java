package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents the rally metrics data for a particular iteration with remaining days calculated 
 * by start date and end date.
 */
@Document(collection = "rally_feature")
public class RallyFeature extends BaseModel {
	private long timestamp;
	private Object endDate;
	private int remainingDays;
	private Object startDate;
	private long lastUpdated;

	private ObjectId collectorItemId;
	private String plannedVelocity;
	private String state;
	private String taskActualTotal;
	private String taskEstimateTotal;
	private String taskRemainingTotal;
	private String projectId;
	private String projectName;
	private String planEstimate;
	private static final String INSTANCE_URL = "instanceUrl";
	private static final String ITERATION_NAME = "iterationName";
	private static final String ITERATION_ID = "iterationId";
	private String userListCount;
	private String lastExecuted;
	private Map<String, Object> options = new HashMap<>();

	private Set<RallyStoryStages> storyStages = new HashSet<>();
	
	public Object getStartDate() {
		return startDate;
	}

	public void setStartDate(Object startDate) {
		this.startDate = startDate;
	}

	public int getRemainingDays() {
		return remainingDays;
	}

	public void setRemainingDays(int remainingDays) {
		this.remainingDays = remainingDays;
	}

	public String getLastExecuted() {
		return lastExecuted;
	}

	public void setLastExecuted(String lastExecuted) {
		this.lastExecuted = lastExecuted;
	}

	public String getUserListCount() {
		return userListCount;
	}

	public void setUserListCount(String userListCount) {
		this.userListCount = userListCount;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public String getPlanEstimate() {
		return planEstimate;
	}

	public void setPlanEstimate(String planEstimate) {
		this.planEstimate = planEstimate;
	}

	public String getPlannedVelocity() {
		return plannedVelocity;
	}

	public void setPlannedVelocity(String plannedVelocity) {
		this.plannedVelocity = plannedVelocity;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTaskActualTotal() {
		return taskActualTotal;
	}

	public void setTaskActualTotal(String taskActualTotal) {
		this.taskActualTotal = taskActualTotal;
	}

	public String getTaskEstimateTotal() {
		return taskEstimateTotal;
	}

	public void setTaskEstimateTotal(String taskEstimateTotal) {
		this.taskEstimateTotal = taskEstimateTotal;
	}

	public String getTaskRemainingTotal() {
		return taskRemainingTotal;
	}

	public void setTaskRemainingTotal(String taskRemainingTotal) {
		this.taskRemainingTotal = taskRemainingTotal;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}

	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}

	public String getInstanceUrl() {
		return (String) getOptions().get(INSTANCE_URL);
	}

	public void setInstanceUrl(String instanceUrl) {
		getOptions().put(INSTANCE_URL, instanceUrl);
	}

	public String getIterationId() {
		return (String) getOptions().get(ITERATION_ID);
	}

	public void setIterationId(String id) {
		getOptions().put(ITERATION_ID, id);
	}

	public String getIterationName() {
		return (String) getOptions().get(ITERATION_NAME);
	}

	public void setIterationName(String name) {
		getOptions().put(ITERATION_NAME, name);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getEndDate() {
		return endDate;
	}

	public void setEndDate(Object endDate) {
		this.endDate = endDate;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Set<RallyStoryStages> getStoryStages() {
		return storyStages;
	}

	public void setStoryStages(Set<RallyStoryStages> storyStages) {
		this.storyStages = storyStages;
	}

}
