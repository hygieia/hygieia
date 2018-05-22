package com.capitalone.dashboard.model;

public class UserStory {
	
	private String storyId;
	private String storyName;
	private String ownerName;
	private String storyUrl;
	private long lastUpdateDate;
	private String state;
	
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public long getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getStoryUrl() {
		return storyUrl;
	}
	public void setStoryUrl(String storyUrl) {
		this.storyUrl = storyUrl;
	}
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	public String getStoryName() {
		return storyName;
	}
	public void setStoryName(String storyName) {
		this.storyName = storyName;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	
}
