package com.capitalone.dashboard.gitlab.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitlabRequest {

	private String id;
	private String iid;
	private String title;
	private String state;
	private String sha;

	@JsonProperty("created_at")
	private String createdAt;
	@JsonProperty("updated_at")
	private String updatedAt;
	@JsonProperty("target_branch")
	private String targetBranch;
	@JsonProperty("source_branch")
	private String sourceBranch;
	@JsonProperty("web_url")
	private String webUrl;

	private String authorId;
	private String authorName;

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
	 * @return the iid
	 */
	public String getIid() {
		return iid;
	}

	/**
	 * @param iid the iid to set
	 */
	public void setIid(String iid) {
		this.iid = iid;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * @param sha the sha to set
	 */
	public void setSha(String sha) {
		this.sha = sha;
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

	/**
	 * @return the targetBranch
	 */
	public String getTargetBranch() {
		return targetBranch;
	}

	/**
	 * @param targetBranch the targetBranch to set
	 */
	public void setTargetBranch(String targetBranch) {
		this.targetBranch = targetBranch;
	}

	/**
	 * @return the sourceBranch
	 */
	public String getSourceBranch() {
		return sourceBranch;
	}

	/**
	 * @param sourceBranch the sourceBranch to set
	 */
	public void setSourceBranch(String sourceBranch) {
		this.sourceBranch = sourceBranch;
	}

	/**
	 * @return the webUrl
	 */
	public String getWebUrl() {
		return webUrl;
	}

	/**
	 * @param webUrl the webUrl to set
	 */
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
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

    @JsonProperty("author")
    private void unpackAuthorInfo(Map<String, Object> authorInfo) {
        this.authorId = String.valueOf((Integer) authorInfo.get("id"));
        this.authorName = (String) authorInfo.get("name");
    }

}
