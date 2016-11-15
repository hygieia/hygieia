package com.capitalone.dashboard.model;

public class GitlabIssue {

	private Long id;
	private Long iid;
	private Long project_id;
	private String title;
	private String description;
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
