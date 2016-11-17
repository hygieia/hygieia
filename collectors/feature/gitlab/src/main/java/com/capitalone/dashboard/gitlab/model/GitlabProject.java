package com.capitalone.dashboard.gitlab.model;

public class GitlabProject {
	
	private Long id;
	private String name;
	private String path;
	private String last_activity_at;
	private GitlabNamespace namespace;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLast_activity_at() {
		return last_activity_at;
	}

	public void setLast_activity_at(String last_activity_at) {
		this.last_activity_at = last_activity_at;
	}

	public GitlabNamespace getNamespace() {
		return namespace;
	}

	public void setNamespace(GitlabNamespace namespace) {
		this.namespace = namespace;
	}

}
