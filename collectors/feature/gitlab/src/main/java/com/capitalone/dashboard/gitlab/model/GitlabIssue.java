package com.capitalone.dashboard.gitlab.model;

import java.util.List;

public class GitlabIssue {

	private Long id;
	private Long iid;
	private Long project_id;
	private String title;
	private String description;
	private String state;
	private String updated_at;
	private List<String> labels;
	private GitlabMilestone milestone;
	private GitlabProject project;

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

	public Long getProject_id() {
		return project_id;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
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

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
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

	public GitlabProject getProject() {
		return project;
	}

	public void setProject(GitlabProject project) {
		this.project = project;
	}

}
