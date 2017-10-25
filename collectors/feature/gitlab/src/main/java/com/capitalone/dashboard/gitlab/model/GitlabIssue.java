package com.capitalone.dashboard.gitlab.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitlabIssue {

	private Long id;
	private Long iid;
	@JsonProperty("project_id")
	private Long projectId;
	private String title;
	private String description;
	private String state;
	@JsonProperty("updated_at")
	private String updatedAt;
	private String weight;
	private List<String> labels;
	private GitlabMilestone milestone;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIid() {
		return iid;
	}

	public void setIid(Long iid) {
		this.iid = iid;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
    public String getWeight() {
        return weight == null ? "1" : weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public GitlabMilestone getMilestone() {
		return milestone;
	}

	public void setMilestone(GitlabMilestone milestone) {
		this.milestone = milestone;
	}

}
