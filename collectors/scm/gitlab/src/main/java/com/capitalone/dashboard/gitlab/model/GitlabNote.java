package com.capitalone.dashboard.gitlab.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitlabNote {

	private String id;
	private String body;
	private boolean system;
	private boolean resolvable;

	private String authorId;
	private String authorName;

	@JsonProperty("created_at")
	private String createdAt;
	@JsonProperty("updated_at")
	private String updatedAt;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the system
	 */
	public boolean isSystem() {
		return system;
	}

	/**
	 * @param system the system to set
	 */
	public void setSystem(boolean system) {
		this.system = system;
	}

	/**
	 * @return the resolvable
	 */
	public boolean isResolvable() {
		return resolvable;
	}

	/**
	 * @param resolvable the resolvable to set
	 */
	public void setResolvable(boolean resolvable) {
		this.resolvable = resolvable;
	}

	/**
	 * @return the authorId
	 */
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * @param authorId the authorId to set
	 */
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @param authorName the authorName to set
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	/**
	 * @return the createdAt
	 */
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the updatedAt
	 */
	public String getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

    @JsonProperty("author")
    private void unpackAuthorInfo(Map<String, Object> authorInfo) {
        this.authorId = String.valueOf((Integer) authorInfo.get("id"));
        this.authorName = (String) authorInfo.get("name");
    }

}
