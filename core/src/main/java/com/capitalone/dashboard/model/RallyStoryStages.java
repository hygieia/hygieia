package com.capitalone.dashboard.model;

import java.util.List;

public class RallyStoryStages {
	
	private String backlog;
	private String defined;
	private String inProgress;
	private String completed;
	private String accepted;
	private String defects;
	private List<UserStory> userStories;
	
	
	public List<UserStory> getUserStories() {
		return userStories;
	}
	public void setUserStories(List<UserStory> userStories) {
		this.userStories = userStories;
	}
	public String getDefects() {
		return defects;
	}
	public void setDefects(String defects) {
		this.defects = defects;
	}
	public String getBacklog() {
		return backlog;
	}
	public void setBacklog(String backlog) {
		this.backlog = backlog;
	}
	public String getDefined() {
		return defined;
	}
	public void setDefined(String defined) {
		this.defined = defined;
	}
	public String getInProgress() {
		return inProgress;
	}
	public void setInProgress(String inProgress) {
		this.inProgress = inProgress;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	public String getAccepted() {
		return accepted;
	}
	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}

}
