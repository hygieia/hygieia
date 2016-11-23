package com.capitalone.dashboard.gitlab.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitlabProject {
	
	private Long id;
	private String name;
	private String path;
	@JsonProperty("last_activity_at")
	private String lastActivityAt;
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

	public String getLastActivityAt() {
		return lastActivityAt;
	}

	public void setLastActivityAt(String lastActivityAt) {
		this.lastActivityAt = lastActivityAt;
	}

	public GitlabNamespace getNamespace() {
		return namespace;
	}

	public void setNamespace(GitlabNamespace namespace) {
		this.namespace = namespace;
	}

}
