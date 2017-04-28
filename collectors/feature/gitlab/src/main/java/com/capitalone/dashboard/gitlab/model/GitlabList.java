package com.capitalone.dashboard.gitlab.model;

public class GitlabList {

	private Long id;
	private Long position;
	private GitlabLabel label;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public GitlabLabel getLabel() {
		return label;
	}

	public void setLabel(GitlabLabel label) {
		this.label = label;
	}

}
